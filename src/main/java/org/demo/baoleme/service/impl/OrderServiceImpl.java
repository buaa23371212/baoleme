package org.demo.baoleme.service.impl;

import org.demo.baoleme.common.RedisLockUtil;
import org.demo.baoleme.mapper.OrderMapper;
import org.demo.baoleme.pojo.Order;
import org.demo.baoleme.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private RedisLockUtil redisLockUtil;

    @Override
    public List<Order> getAvailableOrders(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return orderMapper.selectAvailableOrders(offset, pageSize);
    }

    @Override
    public boolean grabOrder(Long orderId, Long riderId) {
        String lockKey = "order_lock:" + orderId;
        String lockValue = String.valueOf(riderId);
        boolean locked = redisLockUtil.tryLock(lockKey, lockValue, 30, TimeUnit.SECONDS);
        if (!locked) {
            return false;  // 没抢到锁，直接返回
        }
        try {
            // 加锁后执行数据库更新
            return orderMapper.grabOrder(orderId, riderId) > 0;
        } finally {
            redisLockUtil.unlock(lockKey, lockValue);
        }
    }

    @Override
    public boolean riderCancelOrder(Long orderId, Long riderId) {
        return orderMapper.riderCancelOrder(orderId, riderId) > 0;
    }

    @Override
    public boolean riderUpdateOrderStatus(Long orderId, Long riderId, Integer targetStatus) {
        if (targetStatus != null && targetStatus == 3) {
            // 特判：完成订单，调用专门 SQL
            return orderMapper.completeOrder(orderId, riderId) > 0;
        } else {
            // 其他普通状态
            System.out.println("1");
            return orderMapper.riderUpdateOrderStatus(orderId, riderId, targetStatus) > 0;
        }
    }

    @Override
    public List<Order> getRiderOrders(Long riderId, Integer status, String startTime, String endTime, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return orderMapper.selectRiderOrders(riderId, status, startTime, endTime, offset, pageSize);
    }

    @Override
    public Map<String, Object> getRiderEarnings(Long riderId) {
        return orderMapper.selectRiderEarnings(riderId);
    }

    @Override
    public Order getOrderById(Long orderId) {
        return orderMapper.selectById(orderId);
    }

    @Override
    public boolean updateOrderByMerchant(Long orderId, Long storeId, Integer newStatus, String cancelReason) {
        // Step 1: 查询订单当前状态和店铺ID
        Order order = orderMapper.selectById(orderId);
        if (order == null || !order.getStoreId().equals(storeId)) {
            return false; // 订单不存在或店铺不匹配
        }

        // TODO: 未过滤空值

        // Step 2: 执行更新操作
        int rowsUpdated = orderMapper.updateByMerchant(
                orderId,
                storeId,
                newStatus
        );

        // Step 3: 返回更新结果
        return rowsUpdated > 0;
    }
}