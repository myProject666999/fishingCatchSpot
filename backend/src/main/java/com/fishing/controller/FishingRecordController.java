package com.fishing.controller;

import com.fishing.common.PageQuery;
import com.fishing.common.PageResult;
import com.fishing.common.Result;
import com.fishing.dto.FishingRecordDTO;
import com.fishing.dto.StatisticsQueryDTO;
import com.fishing.service.FishingRecordService;
import com.fishing.vo.FishingRecordVO;
import com.fishing.vo.SeasonStatisticsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping
@Validated
public class FishingRecordController {

    @Autowired
    private FishingRecordService fishingRecordService;

    @PostMapping("/api/record/add")
    public Result<Void> add(@RequestHeader("X-User-Id") Long userId, @RequestBody @Valid FishingRecordDTO dto) {
        log.debug("add invoked, userId={}, param={}", userId, dto);
        fishingRecordService.add(userId, dto);
        return Result.success();
    }

    @PutMapping("/api/record/update")
    public Result<Void> update(@RequestHeader("X-User-Id") Long userId, @RequestBody @Valid FishingRecordDTO dto) {
        log.debug("update invoked, userId={}, param={}", userId, dto);
        fishingRecordService.update(userId, dto);
        return Result.success();
    }

    @DeleteMapping("/api/record/{recordId}")
    public Result<Void> delete(@RequestHeader("X-User-Id") Long userId, @PathVariable Long recordId) {
        log.debug("delete invoked, userId={}, recordId={}", userId, recordId);
        fishingRecordService.delete(userId, recordId);
        return Result.success();
    }

    @GetMapping("/api/record/{recordId}")
    public Result<FishingRecordVO> getDetail(@RequestHeader("X-User-Id") Long userId, @PathVariable Long recordId) {
        log.debug("getDetail invoked, userId={}, recordId={}", userId, recordId);
        FishingRecordVO fishingRecordVO = fishingRecordService.getDetail(userId, recordId);
        return Result.success(fishingRecordVO);
    }

    @PostMapping("/api/record/listBySpot/{spotId}")
    public Result<PageResult<FishingRecordVO>> listBySpot(@RequestHeader("X-User-Id") Long userId, @PathVariable Long spotId, @RequestBody PageQuery query) {
        log.debug("listBySpot invoked, userId={}, spotId={}, param={}", userId, spotId, query);
        PageResult<FishingRecordVO> pageResult = fishingRecordService.listBySpot(userId, spotId, query);
        return Result.success(pageResult);
    }

    @PostMapping("/api/record/list")
    public Result<PageResult<FishingRecordVO>> listByUser(@RequestHeader("X-User-Id") Long userId, @RequestBody PageQuery query) {
        log.debug("listByUser invoked, userId={}, param={}", userId, query);
        PageResult<FishingRecordVO> pageResult = fishingRecordService.listByUser(userId, query);
        return Result.success(pageResult);
    }

    @PostMapping("/api/record/statistics")
    public Result<List<SeasonStatisticsVO>> getSeasonStatistics(@RequestHeader("X-User-Id") Long userId, @RequestBody StatisticsQueryDTO dto) {
        log.debug("getSeasonStatistics invoked, userId={}, param={}", userId, dto);
        List<SeasonStatisticsVO> list = fishingRecordService.getSeasonStatistics(userId, dto);
        return Result.success(list);
    }
}
