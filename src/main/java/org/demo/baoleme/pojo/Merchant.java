package org.demo.baoleme.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("merchant")
public class Merchant {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String password;    // 数据库中保存加密后的字符串

    private String phone;

    private LocalDateTime createdAt;

    private String avatar;
}