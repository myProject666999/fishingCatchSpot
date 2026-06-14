package com.fishing.controller;

import com.fishing.common.Result;
import com.fishing.dto.LoginDTO;
import com.fishing.service.SysUserService;
import com.fishing.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping
@Validated
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @PostMapping("/api/auth/login")
    public Result<UserVO> login(@RequestBody @Valid LoginDTO dto) {
        log.debug("login invoked, dto={}", dto);
        UserVO userVO = sysUserService.login(dto);
        return Result.success(userVO);
    }
}
