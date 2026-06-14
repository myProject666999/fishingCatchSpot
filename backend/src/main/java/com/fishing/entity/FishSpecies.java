package com.fishing.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@TableName("fish_species")
@Data
@Accessors(chain = true)
public class FishSpecies {

    @TableId(type = IdType.AUTO)
    private Long speciesId;

    private String speciesName;

    private String speciesCode;

    private String description;

    private Integer sortOrder;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
