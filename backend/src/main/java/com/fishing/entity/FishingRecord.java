package com.fishing.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@TableName(value = "fishing_record", autoResultMap = true)
@Data
@Accessors(chain = true)
public class FishingRecord {

    @TableId(type = IdType.AUTO)
    private Long recordId;

    private Long spotId;

    private Long userId;

    private LocalDate fishingDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private Integer fishingMethod;

    private String bait;

    private String weather;

    private String temperature;

    private String windDirection;

    private String windLevel;

    private String waterLevel;

    private Integer totalCount;

    private BigDecimal totalWeight;

    private String remark;

    @TableField(typeHandler = FastjsonTypeHandler.class)
    private String images;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
