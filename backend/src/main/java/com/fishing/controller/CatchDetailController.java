package com.fishing.controller;

import com.fishing.common.Result;
import com.fishing.service.CatchDetailService;
import com.fishing.vo.CatchDetailVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/catch")
@Validated
public class CatchDetailController {

    @Autowired
    private CatchDetailService catchDetailService;

    @GetMapping("/record/{recordId}")
    public Result<List<CatchDetailVO>> getByRecordId(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                                     @PathVariable Long recordId) {
        log.debug("查询渔获详情列表 invoked, userId={}, recordId={}", userId, recordId);
        List<CatchDetailVO> list = catchDetailService.getByRecordId(recordId);
        return Result.success(list);
    }
}
