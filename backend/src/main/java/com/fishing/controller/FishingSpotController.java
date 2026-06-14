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
@RequestMapping
@Validated
public class FishingSpotController {

    @Autowired
    private FishingSpotService fishingSpotService;

    @PostMapping("/api/spot/add")
    public Result<Void> add(@RequestHeader("X-User-Id") Long userId, @RequestBody @Valid FishingSpotDTO dto) {
        log.debug("add invoked, userId={}, param={}", userId, dto);
        fishingSpotService.add(userId, dto);
        return Result.success();
    }

    @PutMapping("/api/spot/update")
    public Result<Void> update(@RequestHeader("X-User-Id") Long userId, @RequestBody @Valid FishingSpotDTO dto) {
        log.debug("update invoked, userId={}, param={}", userId, dto);
        fishingSpotService.update(userId, dto);
        return Result.success();
    }

    @DeleteMapping("/api/spot/{spotId}")
    public Result<Void> delete(@RequestHeader("X-User-Id") Long userId, @PathVariable Long spotId) {
        log.debug("delete invoked, userId={}, spotId={}", userId, spotId);
        fishingSpotService.delete(userId, spotId);
        return Result.success();
    }

    @GetMapping("/api/spot/{spotId}")
    public Result<FishingSpotVO> getDetail(@RequestHeader("X-User-Id") Long userId, @PathVariable Long spotId) {
        log.debug("getDetail invoked, userId={}, spotId={}", userId, spotId);
        FishingSpotVO fishingSpotVO = fishingSpotService.getDetail(userId, spotId);
        return Result.success(fishingSpotVO);
    }

    @PostMapping("/api/spot/list")
    public Result<PageResult<FishingSpotVO>> listByUser(@RequestHeader("X-User-Id") Long userId, @RequestBody PageQuery query) {
        log.debug("listByUser invoked, userId={}, param={}", userId, query);
        PageResult<FishingSpotVO> pageResult = fishingSpotService.listByUser(userId, query);
        return Result.success(pageResult);
    }

    @GetMapping("/api/spot/nearby")
    public Result<List<SpotNearbyVO>> listNearby(@RequestHeader("X-User-Id") Long userId, @RequestParam BigDecimal lng, @RequestParam BigDecimal lat, @RequestParam(defaultValue = "5") Double radius) {
        log.debug("listNearby invoked, userId={}, lng={}, lat={}, radius={}", userId, lng, lat, radius);
        List<SpotNearbyVO> list = fishingSpotService.listNearby(userId, lng, lat, radius);
        return Result.success(list);
    }

    @PostMapping("/api/spot/toggleFavorite/{spotId}")
    public Result<Void> toggleFavorite(@RequestHeader("X-User-Id") Long userId, @PathVariable Long spotId) {
        log.debug("toggleFavorite invoked, userId={}, spotId={}", userId, spotId);
        fishingSpotService.toggleFavorite(userId, spotId);
        return Result.success();
    }
}
