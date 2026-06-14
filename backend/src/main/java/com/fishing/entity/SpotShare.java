package com.fishing.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("spot_share")
@Data
@Accessors(chain = true)
public class SpotShare {

    @TableId(type = IdType.AUTO)
    private Long shareId;

    private Long spotId;

    private Long shareUserId;

    private String shareTitle;

    private String shareContent;

    private BigDecimal blurredLng;

    private BigDecimal blurredLat;

    private Integer viewCount;

    private Integer likeCount;

    private Integer shareStatus;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
