package com.petboarding.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("shop")
public class Shop {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long providerId;

    private String name;

    private String description;

    private String logo;

    private String photos;

    private String province;

    private String city;

    private String district;

    private String address;

    private BigDecimal longitude;

    private BigDecimal latitude;

    private String phone;

    private String businessLicense;

    private String qualification;

    private Integer capacity;

    private Integer currentCount;

    private BigDecimal rating;

    private Integer status;

    private Integer verified;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
