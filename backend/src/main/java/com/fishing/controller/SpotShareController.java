package com.fishing.controller;

import com.fishing.common.PageQuery;
import com.fishing.common.PageResult;
import com.fishing.common.Result;
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
@RequestMapping
@Validated
public class SpotShareController {

    @Autowired
    private SpotShareService spotShareService;

    @PostMapping("/api/share/add")
    public Result<Void> share(@RequestHeader("X-User-Id") Long userId, @RequestBody @Valid SpotShareDTO dto) {
        log.debug("share invoked, userId={}, param={}", userId, dto);
        spotShareService.share(userId, dto);
        return Result.success();
    }

    @PostMapping("/api/share/list")
    public Result<PageResult<SpotShareVO>> list(@RequestParam(required = false) String keyword, @RequestParam(required = false) Integer waterType, @RequestBody PageQuery query) {
        log.debug("list invoked, keyword={}, waterType={}, param={}", keyword, waterType, query);
        PageResult<SpotShareVO> pageResult = spotShareService.list(keyword, waterType, query);
        return Result.success(pageResult);
    }

    @GetMapping("/api/share/{shareId}")
    public Result<SpotShareVO> getDetail(@PathVariable Long shareId) {
        log.debug("getDetail invoked, shareId={}", shareId);
        SpotShareVO spotShareVO = spotShareService.getDetail(shareId);
        return Result.success(spotShareVO);
    }

    @PostMapping("/api/share/like/{shareId}")
    public Result<Void> like(@PathVariable Long shareId) {
        log.debug("like invoked, shareId={}", shareId);
        spotShareService.like(shareId);
        return Result.success();
    }

    @DeleteMapping("/api/share/{shareId}")
    public Result<Void> delete(@RequestHeader("X-User-Id") Long userId, @PathVariable Long shareId) {
        log.debug("delete invoked, userId={}, shareId={}", userId, shareId);
        spotShareService.delete(userId, shareId);
        return Result.success();
    }
}
