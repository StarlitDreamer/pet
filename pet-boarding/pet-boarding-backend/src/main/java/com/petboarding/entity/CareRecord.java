package com.petboarding.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("care_record")
public class CareRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long orderId;

    private Long petId;

    private Long shopId;

    private Long employeeId;

    private Integer type;

    private String content;

    private String photos;

    private String video;

    private String healthStatus;

    private String healthNote;

    private BigDecimal temperature;

    private BigDecimal weight;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
