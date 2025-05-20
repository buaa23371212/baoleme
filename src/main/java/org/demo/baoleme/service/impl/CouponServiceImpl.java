package org.demo.baoleme.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.demo.baoleme.mapper.CouponMapper;
import org.demo.baoleme.pojo.Coupon;
import org.demo.baoleme.service.CouponService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class CouponServiceImpl implements CouponService {

    private final CouponMapper couponMapper;

    public CouponServiceImpl(CouponMapper couponMapper) {
        this.couponMapper = couponMapper;
    }

    @Override
    @Transactional
    public Coupon createCoupon(Coupon coupon) {
        // 重置自动生成字段
        coupon.setId(null);
        coupon.setCreatedAt(null);
        // 校验必要字段
        if (coupon.getCode() == null || coupon.getType() == null) {
            throw new IllegalArgumentException("优惠券代码和类型不能为空");
        }
        int result = couponMapper.insert(coupon);
        return result > 0 ? coupon : null;
    }

    @Override
    @Transactional(readOnly = true)
    public Coupon getCouponById(Long couponId) {
        return couponMapper.selectById(couponId);
    }

    @Override
    @Transactional
    public boolean updateCoupon(Coupon coupon) {
        // 禁止修改创建时间
        coupon.setCreatedAt(null);
        return couponMapper.updateById(coupon) > 0;
    }

    @Override
    @Transactional
    public boolean deleteCoupon(Long couponId) {
        return couponMapper.deleteById(couponId) > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Coupon> getCouponsByType(Coupon.CouponType type) {
        return couponMapper.selectList(
                new LambdaQueryWrapper<Coupon>()
                        .eq(Coupon::getType, type)
        );
    }
}