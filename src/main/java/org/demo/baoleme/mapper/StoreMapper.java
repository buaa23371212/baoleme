package org.demo.baoleme.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.demo.baoleme.pojo.Store;

import java.util.List;

@Mapper
public interface StoreMapper extends BaseMapper<Store> {

    /**
     * 更新店铺信息
     * @param store 店铺对象
     * @return 受影响的行数
     */
    @Update("UPDATE store SET name=#{name}, `description`=#{description}, location=#{location}, rating=#{rating}, balance=#{balance}, status=#{status}, image=#{image}, merchant_id=#{merchantId} WHERE id = #{id}")
    int updateStore(Store store);

    @Select("""
    SELECT id, name, description, location, rating, balance, status, created_at, image
    FROM store
    ORDER BY created_at DESC
    LIMIT #{offset}, #{limit}
""")
    List<Store> selectStoresPaged(@Param("offset") int offset, @Param("limit") int limit);

    @Delete("DELETE FROM store WHERE name = #{storeName}")
    int deleteByName(@Param("storeName") String storeName);
}