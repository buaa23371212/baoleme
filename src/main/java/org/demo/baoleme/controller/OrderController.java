package org.demo.baoleme.controller;

import jakarta.validation.Valid;
import org.demo.baoleme.common.CommonResponse;
import org.demo.baoleme.common.ResponseBuilder;
import org.demo.baoleme.dto.request.order.*;
import org.demo.baoleme.dto.request.rider.RiderOrderHistoryQueryRequest;
import org.demo.baoleme.dto.response.order.*;
import org.demo.baoleme.dto.response.rider.RiderEarningsResponse;
import org.demo.baoleme.dto.response.rider.RiderOrderHistoryResponse;
import org.demo.baoleme.pojo.Order;
import org.demo.baoleme.service.OrderService;
import org.demo.baoleme.common.UserHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 获取可抢订单列表
     */
    @GetMapping("/available")
    public CommonResponse getAvailableOrders(@RequestParam("page") int page,
                                             @RequestParam("page_size") int pageSize) {
        List<Order> orders = orderService.getAvailableOrders(page, pageSize);
        return ResponseBuilder.ok(Map.of("orders", orders));
    }

    /**
     * 抢单
     */
    @PutMapping("/grab")
    public CommonResponse grabOrder(@Valid @RequestBody OrderGrabRequest request) {
        Long riderId = UserHolder.getId();
        boolean ok = orderService.grabOrder(request.getOrder_id(), riderId);
        if (!ok) {
            return ResponseBuilder.fail("订单已被抢或不存在");
        }

        OrderGrabResponse response = new OrderGrabResponse();
        response.setOrder_id(request.getOrder_id());
        response.setPickup_deadline(LocalDateTime.now().plusMinutes(30)); // 假设30分钟取货

        return ResponseBuilder.ok(response);
    }

    /**
     * 取消订单
     */
    @PutMapping("/cancel")
    public CommonResponse cancelOrder(@Valid @RequestBody OrderCancelRequest request) {
        Long riderId = UserHolder.getId();
        boolean ok = orderService.riderCancelOrder(request.getOrder_id(), riderId);
        if (!ok) {
            return ResponseBuilder.fail("当前状态不可取消或订单不存在");
        }
        return ResponseBuilder.ok(Map.of(
                "order_id", request.getOrder_id(),
                "status", "CANCELLED"
        ));
    }

    /**
     * 更新订单状态
     */
    @PostMapping("/rider-update-status")
    public CommonResponse updateOrderStatus(@Valid @RequestBody OrderStatusRiderUpdateRequest request) {
        Long riderId = UserHolder.getId();
        boolean ok = orderService.riderUpdateOrderStatus(request.getOrder_id(), riderId, request.getTarget_status());
        if (!ok) {
            return ResponseBuilder.fail("订单状态更新失败");
        }

        OrderStatusRiderUpdateResponse response = new OrderStatusRiderUpdateResponse();
        response.setOrder_id(request.getOrder_id());
        response.setStatus(request.getTarget_status());
        response.setUpdated_at(LocalDateTime.now());

        return ResponseBuilder.ok(response);
    }

    /**
     * 查询骑手订单记录
     */
    @PostMapping("/rider-history-query")
    public CommonResponse getRiderOrders(@Valid @RequestBody RiderOrderHistoryQueryRequest request) {
        Long riderId = UserHolder.getId();
        List<Order> orders = orderService.getRiderOrders(
                riderId,
                request.getStatus(),
                request.getStart_time(),
                request.getEnd_time(),
                request.getPage(),
                request.getPage_size()
        );

        List<RiderOrderHistoryResponse> responses = orders.stream().map(order -> {
            RiderOrderHistoryResponse resp = new RiderOrderHistoryResponse();
            resp.setOrder_id(order.getId());
            resp.setStatus(order.getStatus());
            resp.setTotal_amount(order.getTotalPrice());
            resp.setCompleted_at(order.getEndedAt());
            return resp;
        }).toList();

        return ResponseBuilder.ok(Map.of("orders", responses));
    }

    /**
     * 查询收入统计
     */
    @GetMapping("/rider-earnings")
    public CommonResponse getRiderEarnings() {
        Long riderId = UserHolder.getId();
        Map<String, Object> result = orderService.getRiderEarnings(riderId);

        RiderEarningsResponse response = new RiderEarningsResponse();
        response.setCompleted_orders(((Number) result.getOrDefault("completed_orders", 0)).intValue());
        response.setTotal_earnings((BigDecimal) result.getOrDefault("total_earnings", BigDecimal.ZERO));
        response.setCurrent_month((BigDecimal) result.getOrDefault("current_month", BigDecimal.ZERO));

        return ResponseBuilder.ok(response);
    }

    /**
     * 商家更新订单状态
     */
    @PutMapping("/merchant-update")
    public CommonResponse updateOrderByMerchant(
            @RequestHeader("Authorization") String tokenHeader,
            @Valid @RequestBody OrderUpdateByMerchantRequest request
    ) {
        Order order = orderService.getOrderById(request.getId());
        if (order.getStatus() != 1) {
            return ResponseBuilder.fail("订单更新失败：当前状态商家无权更新");
        }
        if (request.getNewStatus() == null){
            return ResponseBuilder.fail("订单更新失败：商家未设置新状态");
        }
        if (request.getCancelReason() == null){
            return ResponseBuilder.fail("订单更新失败：商家未设置取消原因");
        }

        // Step 1: 调用Service层执行更新
        boolean ok = orderService.updateOrderByMerchant(
                request.getId(),
                request.getStoreId(),
                request.getNewStatus()
        );

        // Step 2: 处理失败情况
        if (!ok) {
            return ResponseBuilder.fail("订单更新失败：权限不足或订单不存在");
        }

        // Step 3: 查询更新后的订单信息（旧状态需在Service中获取）
        Order newOrder = orderService.getOrderById(request.getId());

        // Step 4: 构造响应
        OrderUpdateByMerchantResponse response = new OrderUpdateByMerchantResponse();
        response.setId(newOrder.getId());
        response.setOldStatus(newOrder.getStatus()); // 假设Service已处理旧状态
        response.setNewStatus(request.getNewStatus());
        response.setUpdateAt(LocalDateTime.now());
        response.setCancelReason(request.getCancelReason());

        return ResponseBuilder.ok(response);
    }

    /**
     * 商家分页查看订单
     */
    @PostMapping("/merchant-list")
    public CommonResponse ordersReadByMerchant(
            @RequestHeader("Authorization") String tokenHeader,
            @Valid @RequestBody OrderReadByMerchantRequest request
    ) {
        // Step 1: 参数校验
        if (request.getStoreId() == null) {
            return ResponseBuilder.fail("订单查看失败：未提供store_id字段");
        }

        // Step 2: 调用Service分页查询
        List<Order> orders = orderService.getOrdersByMerchant(
                request.getStoreId(),
                request.getStatus(),
                request.getPage(),
                request.getPageSize()
        );

        // Step 3: 转换为响应对象
        List<OrderReadByMerchantResponse> responses = orders.stream().map(order -> {
            OrderReadByMerchantResponse resp = new OrderReadByMerchantResponse();
            resp.setOrderId(order.getId());
            resp.setUserName(order.getUserId()); // 假设用户ID字段为userId
            resp.setStatus(order.getStatus());
            resp.setTotalPrice(order.getTotalPrice());
            resp.setCreatedAt(order.getCreatedAt());
            return resp;
        }).toList();

        // Step 4: 包装分页响应
        OrderPageForMerchantResponse pageResponse = new OrderPageForMerchantResponse();
        pageResponse.setList(responses);
        pageResponse.setCurrentPage(request.getPage());
        pageResponse.setPageSize(request.getPageSize());
        // 总记录数需另查（此处省略具体实现）

        return ResponseBuilder.ok(pageResponse);
    }
}