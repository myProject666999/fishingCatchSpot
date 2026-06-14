package com.fishing.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@TableName("spot_favorite")
@Data
@Accessors(chain = true)
public class SpotFavorite {

    @TableId(type = IdType.AUTO)
    private Long favoriteId;

    private Long userId;

    private Long shareId;

    private Long spotId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
