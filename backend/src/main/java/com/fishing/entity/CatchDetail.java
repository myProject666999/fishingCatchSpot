package com.fishing.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("catch_detail")
@Data
@Accessors(chain = true)
public class CatchDetail {

    @TableId(type = IdType.AUTO)
    private Long detailId;

    private Long recordId;

    private Long speciesId;

    private String speciesName;

    private Integer catchCount;

    private BigDecimal totalWeight;

    private BigDecimal maxWeight;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
