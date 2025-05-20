package org.demo.baoleme.dto.request.coupon;

import lombok.Data;
import org.demo.baoleme.pojo.Coupon;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CouponUpdateRequest {
    private Long id;
    private String desc;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private Coupon.CouponType type;
    private BigDecimal discount;
    private Integer fullAmount;
    private Integer reduceAmount;
}
