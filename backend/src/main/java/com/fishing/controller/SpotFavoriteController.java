package com.fishing.controller;

import com.fishing.common.PageQuery;
import com.fishing.common.PageResult;
import com.fishing.common.Result;
import com.fishing.dto.SpotFavoriteDTO;
import com.fishing.service.SpotFavoriteService;
import com.fishing.vo.SpotFavoriteVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/favorite")
@Validated
public class SpotFavoriteController {

    @Autowired
    private SpotFavoriteService spotFavoriteService;

    @PostMapping("/add")
    public Result<Void> add(@RequestHeader(value = "X-User-Id", required = false) Long userId, @RequestBody @Valid SpotFavoriteDTO dto) {
        if (userId == null) {
            return Result.fail(401, "请先登录");
        }
        log.debug("add invoked, userId={}, param={}", userId, dto);
        spotFavoriteService.add(userId, dto);
        return Result.success();
    }

    @PostMapping("/list")
    public Result<PageResult<SpotFavoriteVO>> listByUser(@RequestHeader(value = "X-User-Id", required = false) Long userId, @RequestBody PageQuery query) {
        if (userId == null) {
            return Result.fail(401, "请先登录");
        }
        log.debug("listByUser invoked, userId={}, param={}", userId, query);
        PageResult<SpotFavoriteVO> pageResult = spotFavoriteService.listByUser(userId, query);
        return Result.success(pageResult);
    }

    @DeleteMapping("/{favoriteId}")
    public Result<Void> delete(@RequestHeader(value = "X-User-Id", required = false) Long userId, @PathVariable Long favoriteId) {
        if (userId == null) {
            return Result.fail(401, "请先登录");
        }
        log.debug("delete invoked, userId={}, favoriteId={}", userId, favoriteId);
        spotFavoriteService.delete(userId, favoriteId);
        return Result.success();
    }
}
