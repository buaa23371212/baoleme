package org.demo.baoleme.controller;

import org.demo.baoleme.dto.request.coupon.*;
import org.demo.baoleme.dto.response.coupon.*;
import org.demo.baoleme.pojo.Coupon;
import org.demo.baoleme.pojo.Page;
import org.demo.baoleme.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coupons")
public class CouponController {

    @Autowired
    private CouponService couponService;

    @PostMapping
    public CouponCreateResponse createCoupon(
            @RequestHeader("Authorization") String tokenHeader,
            @RequestBody CouponCreateRequest request
    ) {
        System.out.println("\n=== CREATE Request ===");
        System.out.println("Desc: " + request.getDesc());
        System.out.println("Type: " + request.getType());

        Coupon coupon = new Coupon();
        // 假设将请求中的属性复制到Coupon对象
        // 这里需要根据实际的CouponCreateRequest和Coupon类的属性进行赋值
        coupon.setType(request.getType());
        coupon.setCode(request.getCode());

        Coupon createdCoupon = couponService.createCoupon(coupon);
        CouponCreateResponse response = new CouponCreateResponse();
        response.setId(createdCoupon.getId());
        response.setCode(createdCoupon.getCode());

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
        System.out.println("\n=== DELETE Request ===");
        System.out.println("Delete ID: " + request.getId());

        couponService.deleteCoupon(request.getId());

        System.out.println("=== DELETE Response ===");
        System.out.println("Deleted ID: " + request.getId() + "\n");
    }

    @PutMapping
    public CouponUpdateResponse updateCoupon(
            @RequestHeader("Authorization") String tokenHeader,
            @RequestBody CouponUpdateRequest request
    ) {
        System.out.println("\n=== UPDATE Request ===");
        System.out.println("Update ID: " + request.getId());
        System.out.println("New Desc: " + request.getDesc());

        Coupon coupon = couponService.getCouponById(request.getId());
        // 假设将请求中的属性复制到Coupon对象
        // 这里需要根据实际的CouponUpdateRequest和Coupon类的属性进行赋值
        coupon.setDesc(request.getDesc());

        boolean success = couponService.updateCoupon(coupon);
        CouponUpdateResponse response = new CouponUpdateResponse();
        if (success) {
            response.setDesc(coupon.getDesc());
            response.setStartAt(coupon.getStartAt());
        }

        System.out.println("=== UPDATE Response ===");
        System.out.println("Updated Desc: " + response.getDesc() + "\n");
        return response;
    }

    @GetMapping
    public CouponPageResponse queryCoupons(
            @RequestHeader("Authorization") String tokenHeader,
            @RequestBody CouponViewRequest request
    ) {
        System.out.println("\n=== QUERY Request ===");
        System.out.println("Page: " + request.getPage());
        System.out.println("PageSize: " + request.getPageSize());

        // 这里简单模拟分页查询，实际需要根据页码和页大小进行查询
        List<Coupon> coupons = couponService.getCouponsByType(request.getType());
        Page<Coupon> page = new Page<>();
        page.setList(coupons);
        page.setPageCount(1);

        CouponPageResponse response = new CouponPageResponse();
        response.setData(page);

        System.out.println("=== QUERY Response ===");
        System.out.println("Total Pages: " + page.getPageCount() + "\n");
        return response;
    }
}