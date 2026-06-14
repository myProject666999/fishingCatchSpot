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
@RequestMapping
@Validated
public class SpotFavoriteController {

    @Autowired
    private SpotFavoriteService spotFavoriteService;

    @PostMapping("/api/favorite/add")
    public Result<Void> add(@RequestHeader("X-User-Id") Long userId, @RequestBody @Valid SpotFavoriteDTO dto) {
        log.debug("add invoked, userId={}, param={}", userId, dto);
        spotFavoriteService.add(userId, dto);
        return Result.success();
    }

    @DeleteMapping("/api/favorite/{favoriteId}")
    public Result<Void> delete(@RequestHeader("X-User-Id") Long userId, @PathVariable Long favoriteId) {
        log.debug("delete invoked, userId={}, favoriteId={}", userId, favoriteId);
        spotFavoriteService.delete(userId, favoriteId);
        return Result.success();
    }

    @PostMapping("/api/favorite/list")
    public Result<PageResult<SpotFavoriteVO>> listByUser(@RequestHeader("X-User-Id") Long userId, @RequestBody PageQuery query) {
        log.debug("listByUser invoked, userId={}, param={}", userId, query);
        PageResult<SpotFavoriteVO> pageResult = spotFavoriteService.listByUser(userId, query);
        return Result.success(pageResult);
    }
}
