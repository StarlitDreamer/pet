package com.petboarding.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("pet")
public class Pet {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long ownerId;

    private String name;

    private String species;

    private String breed;

    private Integer gender;

    private Integer age;

    private BigDecimal weight;

    private String photo;

    private String healthStatus;

    private String vaccination;

    private String allergies;

    private String habits;

    private String emergencyContact;

    private String emergencyPhone;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
