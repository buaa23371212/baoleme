package org.demo.baoleme.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;
import org.demo.baoleme.pojo.Coupon;

@Mapper
public interface CouponMapper extends BaseMapper<Coupon> {

    /**
     * 更新优惠券完整信息
     * @param coupon 优惠券实体
     * @return 受影响的行数
     */
    @Update("UPDATE coupon SET " +
            "code = #{code}, " +
            "`desc` = #{desc}, " +  // 使用反引号转义关键字
            "expiration_date = #{expirationDate}, " +
            "start_at = #{startAt}, " +
            "end_at = #{endAt}, " +
            "type = #{type.code}, " +  // 使用枚举的code值存储
            "discount = #{discount}, " +
            "full_amount = #{fullAmount}, " +
            "reduce_amount = #{reduceAmount} " +
            "WHERE id = #{id}")
    int updateCoupon(Coupon coupon);
}