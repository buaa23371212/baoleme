package org.demo.baoleme.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.demo.baoleme.pojo.Product;

import java.util.List;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    /**
     * 根据店铺ID查询商品列表
     * @param storeId 店铺ID
     * @return 商品列表
     */
    @Select("SELECT * FROM product WHERE store_id = #{storeId}")
    List<Product> selectByStoreId(Long storeId);

    // 新增分页查询方法
    @Select("""
        SELECT * FROM product 
        WHERE store_id = #{storeId} 
        LIMIT #{pageSize} OFFSET #{offset}
    """)
    List<Product> selectByStore(
            @Param("storeId") Long storeId,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize
    );

    // 新增总数统计方法
    @Select("SELECT COUNT(*) FROM product WHERE store_id = #{storeId}")
    int countByStore(@Param("storeId") Long storeId);

    @Delete("""
    DELETE FROM product 
    WHERE name = #{productName} 
    AND store_id = (SELECT id FROM store WHERE name = #{storeName} LIMIT 1)
    """)
    int deleteByNameAndStore(@Param("productName") String productName,
                             @Param("storeName") String storeName);
}