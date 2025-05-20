package org.demo.baoleme.controller;

import org.demo.baoleme.dto.request.coupon.*;
import org.demo.baoleme.dto.response.coupon.*;
import org.demo.baoleme.pojo.Coupon;
import org.demo.baoleme.pojo.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coupons")
public class CouponController {

    @PostMapping
    public CouponCreateResponse createCoupon(
            @RequestHeader("Authorization") String tokenHeader,
            @RequestBody CouponCreateRequest request
    ) {
        // 控制台输出请求
        System.out.println("\n=== CREATE Request ===");
        System.out.println("Desc: " + request.getDesc());
        System.out.println("Type: " + request.getType());

        // 模拟业务处理
        CouponCreateResponse response = new CouponCreateResponse();
        response.setId(1L);
        response.setCode("CPN-2023");

        // 控制台输出响应
        System.out.println("=== CREATE Response ===");
        System.out.println("ID: " + response.getId());
        System.out.println("Code: " + response.getCode() + "\n");
        return response;
    }

    @DeleteMapping
    public void deleteCoupon(
            @RequestHeader("Authorization") String tokenHeader,
            @RequestBody CouponDeleteRequest request
    ) {
        // 控制台输出请求
        System.out.println("\n=== DELETE Request ===");
        System.out.println("Delete ID: " + request.getId());

        // 模拟业务处理

        // 控制台输出响应
        System.out.println("=== DELETE Response ===");
        System.out.println("Deleted ID: " + request.getId() + "\n");
    }

    @PutMapping
    public CouponUpdateResponse updateCoupon(
            @RequestHeader("Authorization") String tokenHeader,
            @RequestBody CouponUpdateRequset request
    ) {
        // 控制台输出请求
        System.out.println("\n=== UPDATE Request ===");
        System.out.println("Update ID: " + request.getId());
        System.out.println("New Desc: " + request.getDesc());

        // 模拟业务处理
        CouponUpdateResponse response = new CouponUpdateResponse();
        response.setDesc(request.getDesc());
        response.setStartAt(request.getStartAt());
        // ...其他字段赋值

        // 控制台输出响应
        System.out.println("=== UPDATE Response ===");
        System.out.println("Updated Desc: " + response.getDesc() + "\n");
        return response;
    }

    @GetMapping
    public CouponPageResponse queryCoupons(
            @RequestHeader("Authorization") String tokenHeader,
            @RequestBody CouponViewRequest request
    ) {
        // 控制台输出请求
        System.out.println("\n=== QUERY Request ===");
        System.out.println("Page: " + request.getPage());
        System.out.println("PageSize: " + request.getPageSize());

        // 模拟分页数据
        Page<Coupon> page = new Page<>();
        page.setList(List.of(new Coupon())); // 假设查询到1条数据
        page.setPageCount(1);

        CouponPageResponse response = new CouponPageResponse();
        response.setData(page);

        // 控制台输出响应
        System.out.println("=== QUERY Response ===");
        System.out.println("Total Pages: " + page.getPageCount() + "\n");
        return response;
    }
}