package com.fishing.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName(value = "fishing_spot", autoResultMap = true)
@Data
@Accessors(chain = true)
public class FishingSpot {

    @TableId(type = IdType.AUTO)
    private Long spotId;

    private Long userId;

    private String spotName;

    private Integer waterType;

    private BigDecimal longitude;

    private BigDecimal latitude;

    private String address;

    private String description;

    @TableField(typeHandler = FastjsonTypeHandler.class)
    private String images;

    private Integer isShared;

    private Integer isFavorite;

    private Integer recordCount;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
