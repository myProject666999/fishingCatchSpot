package com.fishing.controller;

import com.fishing.common.PageQuery;
import com.fishing.common.PageResult;
import com.fishing.common.Result;
import com.fishing.dto.FishingSpotDTO;
import com.fishing.service.FishingSpotService;
import com.fishing.vo.FishingSpotVO;
import com.fishing.vo.SpotNearbyVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/spot")
@Validated
public class FishingSpotController {

    @Autowired
    private FishingSpotService fishingSpotService;

    @PostMapping("/add")
    public Result<Void> add(@RequestHeader(value = "X-User-Id", required = false) Long userId, @RequestBody @Valid FishingSpotDTO dto) {
        if (userId == null) {
            return Result.fail(401, "请先登录");
        }
        log.debug("add invoked, userId={}, param={}", userId, dto);
        fishingSpotService.add(userId, dto);
        return Result.success();
    }

    @PutMapping("/update")
    public Result<Void> update(@RequestHeader(value = "X-User-Id", required = false) Long userId, @RequestBody @Valid FishingSpotDTO dto) {
        if (userId == null) {
            return Result.fail(401, "请先登录");
        }
        log.debug("update invoked, userId={}, param={}", userId, dto);
        fishingSpotService.update(userId, dto);
        return Result.success();
    }

    @PostMapping("/list")
    public Result<PageResult<FishingSpotVO>> listByUser(@RequestHeader(value = "X-User-Id", required = false) Long userId, @RequestBody PageQuery query) {
        if (userId == null) {
            return Result.fail(401, "请先登录");
        }
        log.debug("listByUser invoked, userId={}, param={}", userId, query);
        PageResult<FishingSpotVO> pageResult = fishingSpotService.listByUser(userId, query);
        return Result.success(pageResult);
    }

    @GetMapping("/nearby")
    public Result<List<SpotNearbyVO>> listNearby(@RequestHeader(value = "X-User-Id", required = false) Long userId, @RequestParam BigDecimal lng, @RequestParam BigDecimal lat, @RequestParam(defaultValue = "5") Double radius) {
        if (userId == null) {
            return Result.fail(401, "请先登录");
        }
        log.debug("listNearby invoked, userId={}, lng={}, lat={}, radius={}", userId, lng, lat, radius);
        List<SpotNearbyVO> list = fishingSpotService.listNearby(userId, lng, lat, radius);
        return Result.success(list);
    }

    @PostMapping("/toggleFavorite/{spotId}")
    public Result<Void> toggleFavorite(@RequestHeader(value = "X-User-Id", required = false) Long userId, @PathVariable Long spotId) {
        if (userId == null) {
            return Result.fail(401, "请先登录");
        }
        log.debug("toggleFavorite invoked, userId={}, spotId={}", userId, spotId);
        fishingSpotService.toggleFavorite(userId, spotId);
        return Result.success();
    }

    @GetMapping("/{spotId}")
    public Result<FishingSpotVO> getDetail(@RequestHeader(value = "X-User-Id", required = false) Long userId, @PathVariable Long spotId) {
        if (userId == null) {
            return Result.fail(401, "请先登录");
        }
        log.debug("getDetail invoked, userId={}, spotId={}", userId, spotId);
        FishingSpotVO fishingSpotVO = fishingSpotService.getDetail(userId, spotId);
        return Result.success(fishingSpotVO);
    }

    @DeleteMapping("/{spotId}")
    public Result<Void> delete(@RequestHeader(value = "X-User-Id", required = false) Long userId, @PathVariable Long spotId) {
        if (userId == null) {
            return Result.fail(401, "请先登录");
        }
        log.debug("delete invoked, userId={}, spotId={}", userId, spotId);
        fishingSpotService.delete(userId, spotId);
        return Result.success();
    }
}
