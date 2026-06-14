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
@RequestMapping("/api/record")
@Validated
public class FishingRecordController {

    @Autowired
    private FishingRecordService fishingRecordService;

    @PostMapping("/add")
    public Result<Void> add(@RequestHeader(value = "X-User-Id", required = false) Long userId, @RequestBody @Valid FishingRecordDTO dto) {
        if (userId == null) {
            return Result.fail(401, "请先登录");
        }
        log.debug("add invoked, userId={}, param={}", userId, dto);
        fishingRecordService.add(userId, dto);
        return Result.success();
    }

    @PutMapping("/update")
    public Result<Void> update(@RequestHeader(value = "X-User-Id", required = false) Long userId, @RequestBody @Valid FishingRecordDTO dto) {
        if (userId == null) {
            return Result.fail(401, "请先登录");
        }
        log.debug("update invoked, userId={}, param={}", userId, dto);
        fishingRecordService.update(userId, dto);
        return Result.success();
    }

    @PostMapping("/list")
    public Result<PageResult<FishingRecordVO>> listByUser(@RequestHeader(value = "X-User-Id", required = false) Long userId, @RequestBody PageQuery query) {
        if (userId == null) {
            return Result.fail(401, "请先登录");
        }
        log.debug("listByUser invoked, userId={}, param={}", userId, query);
        PageResult<FishingRecordVO> pageResult = fishingRecordService.listByUser(userId, query);
        return Result.success(pageResult);
    }

    @PostMapping("/listBySpot/{spotId}")
    public Result<PageResult<FishingRecordVO>> listBySpot(@RequestHeader(value = "X-User-Id", required = false) Long userId, @PathVariable Long spotId, @RequestBody PageQuery query) {
        if (userId == null) {
            return Result.fail(401, "请先登录");
        }
        log.debug("listBySpot invoked, userId={}, spotId={}, param={}", userId, spotId, query);
        PageResult<FishingRecordVO> pageResult = fishingRecordService.listBySpot(userId, spotId, query);
        return Result.success(pageResult);
    }

    @PostMapping("/statistics")
    public Result<List<SeasonStatisticsVO>> getSeasonStatistics(@RequestHeader(value = "X-User-Id", required = false) Long userId, @RequestBody StatisticsQueryDTO dto) {
        if (userId == null) {
            return Result.fail(401, "请先登录");
        }
        log.debug("getSeasonStatistics invoked, userId={}, param={}", userId, dto);
        List<SeasonStatisticsVO> list = fishingRecordService.getSeasonStatistics(userId, dto);
        return Result.success(list);
    }

    @GetMapping("/{recordId}")
    public Result<FishingRecordVO> getDetail(@RequestHeader(value = "X-User-Id", required = false) Long userId, @PathVariable Long recordId) {
        if (userId == null) {
            return Result.fail(401, "请先登录");
        }
        log.debug("getDetail invoked, userId={}, recordId={}", userId, recordId);
        FishingRecordVO fishingRecordVO = fishingRecordService.getDetail(userId, recordId);
        return Result.success(fishingRecordVO);
    }

    @DeleteMapping("/{recordId}")
    public Result<Void> delete(@RequestHeader(value = "X-User-Id", required = false) Long userId, @PathVariable Long recordId) {
        if (userId == null) {
            return Result.fail(401, "请先登录");
        }
        log.debug("delete invoked, userId={}, recordId={}", userId, recordId);
        fishingRecordService.delete(userId, recordId);
        return Result.success();
    }
}
