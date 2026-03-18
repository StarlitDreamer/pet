package com.petboarding.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("order_info")
public class OrderInfo {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;

    private Long ownerId;

    private Long shopId;

    private Long petId;

    private Long serviceId;

    private LocalDate startDate;

    private LocalDate endDate;

    private Integer days;

    private BigDecimal price;

    private BigDecimal totalAmount;

    private BigDecimal discountAmount;

    private BigDecimal payAmount;

    private Integer insurance;

    private BigDecimal insuranceAmount;

    private Long couponId;

    private Integer status;

    private String cancelReason;

    private String rejectReason;

    private String remark;

    private LocalDateTime payTime;

    private LocalDateTime acceptTime;

    private LocalDateTime checkinTime;

    private LocalDateTime checkoutTime;

    private LocalDateTime completeTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
