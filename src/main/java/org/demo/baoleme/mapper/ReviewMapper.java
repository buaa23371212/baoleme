package org.demo.baoleme.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.demo.baoleme.pojo.Review;
import java.util.List;

@Mapper
public interface ReviewMapper extends BaseMapper<Review> {

    /**
     * 查询指定店铺的高分评价（4-5星）
     * @param storeId 店铺ID
     * @return 高分评价列表
     */
    @Select("SELECT * FROM review WHERE store_id = #{storeId} AND rating BETWEEN 4 AND 5")
    List<Review> selectHighRatingReviews(Long storeId);

    /**
     * 查询指定店铺的低分评价（1-2星）
     * @param storeId 店铺ID
     * @return 低分评价列表
     */
    @Select("SELECT * FROM review WHERE store_id = #{storeId} AND rating BETWEEN 1 AND 2")
    List<Review> selectLowRatingReviews(Long storeId);
}