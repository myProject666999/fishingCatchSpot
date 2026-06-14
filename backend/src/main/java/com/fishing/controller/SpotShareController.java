package com.fishing.controller;

import com.fishing.common.PageResult;
import com.fishing.common.Result;
import com.fishing.dto.ShareQueryDTO;
import com.fishing.dto.SpotShareDTO;
import com.fishing.service.SpotShareService;
import com.fishing.vo.SpotShareVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/share")
@Validated
public class SpotShareController {

    @Autowired
    private SpotShareService spotShareService;

    @PostMapping("/add")
    public Result<Void> share(@RequestHeader(value = "X-User-Id", required = false) Long userId, @RequestBody @Valid SpotShareDTO dto) {
        if (userId == null) {
            return Result.fail(401, "请先登录");
        }
        log.debug("share invoked, userId={}, param={}", userId, dto);
        spotShareService.share(userId, dto);
        return Result.success();
    }

    @PostMapping("/list")
    public Result<PageResult<SpotShareVO>> list(@RequestBody ShareQueryDTO dto) {
        log.debug("list invoked, keyword={}, waterType={}, param={}", dto.getKeyword(), dto.getWaterType(), dto);
        PageResult<SpotShareVO> pageResult = spotShareService.list(dto.getKeyword(), dto.getWaterType(), dto);
        return Result.success(pageResult);
    }

    @PostMapping("/like/{shareId}")
    public Result<Void> like(@RequestHeader(value = "X-User-Id", required = false) Long userId, @PathVariable Long shareId) {
        if (userId == null) {
            return Result.fail(401, "请先登录");
        }
        log.debug("like invoked, userId={}, shareId={}", userId, shareId);
        spotShareService.like(userId, shareId);
        return Result.success();
    }

    @GetMapping("/{shareId}")
    public Result<SpotShareVO> getDetail(@PathVariable Long shareId) {
        log.debug("getDetail invoked, shareId={}", shareId);
        SpotShareVO spotShareVO = spotShareService.getDetail(shareId);
        return Result.success(spotShareVO);
    }

    @DeleteMapping("/{shareId}")
    public Result<Void> delete(@RequestHeader(value = "X-User-Id", required = false) Long userId, @PathVariable Long shareId) {
        if (userId == null) {
            return Result.fail(401, "请先登录");
        }
        log.debug("delete invoked, userId={}, shareId={}", userId, shareId);
        spotShareService.delete(userId, shareId);
        return Result.success();
    }
}
