package org.demo.baoleme.dto.response.coupon;

import lombok.Data;
import org.demo.baoleme.pojo.Page;
import org.demo.baoleme.pojo.Coupon;

@Data
public class CouponPageResponse {
    private Page<Coupon> data;
}