package org.demo.baoleme.service;

import org.demo.baoleme.pojo.Coupon;
import java.util.List;

public interface CouponService {

    /**
     * 创建优惠券
     * @param coupon 优惠券对象（无需id和createdAt）
     * @return 包含生成的id和时间的优惠券对象
     */
    Coupon createCoupon(Coupon coupon);

    /**
     * 根据ID获取优惠券
     * @param couponId 优惠券ID
     * @return 优惠券对象或null
     */
    Coupon getCouponById(Long couponId);

    /**
     * 更新优惠券信息
     * @param coupon 需包含ID的完整对象
     * @return 是否成功
     */
    boolean updateCoupon(Coupon coupon);

    /**
     * 删除优惠券
     * @param couponId 优惠券ID
     * @return 是否成功
     */
    boolean deleteCoupon(Long couponId);

    /**
     * 根据类型查询优惠券
     * @param type 优惠券类型
     * @return 优惠券列表
     */
    List<Coupon> getCouponsByType(Coupon.CouponType type);
}