package com.petboarding.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("complaint")
public class Complaint {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long orderId;

    private Long complainantId;

    private Long shopId;

    private Integer type;

    private String content;

    private String photos;

    private Integer status;

    private String reply;

    private LocalDateTime replyTime;

    private String result;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
