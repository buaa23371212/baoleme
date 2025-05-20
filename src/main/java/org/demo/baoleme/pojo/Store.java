package org.demo.baoleme.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("store")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Store {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long merchantId;

    private String name;

    private String description;

    private String location;

    /**
     * 评分（decimal(2,1), 默认5.0）
     */
    // TODO: 我默认值呢？
    private BigDecimal rating;

    /**
     * 状态（1-开启，0-关闭）
     */
    private int status;

    private LocalDateTime createdAt;

    private String image;
}