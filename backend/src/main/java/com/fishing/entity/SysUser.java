package com.fishing.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@TableName("sys_user")
@Data
@Accessors(chain = true)
public class SysUser {

    @TableId(type = IdType.AUTO)
    private Long userId;

    private String username;

    private String nickname;

    private String password;

    private String avatar;

    private String phone;

    private String email;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
