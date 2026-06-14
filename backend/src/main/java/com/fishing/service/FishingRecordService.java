package com.fishing.service;

import com.fishing.common.PageQuery;
import com.fishing.common.PageResult;
import com.fishing.dto.FishingRecordDTO;
import com.fishing.dto.StatisticsQueryDTO;
import com.fishing.vo.FishingRecordVO;
import com.fishing.vo.SeasonStatisticsVO;

import java.util.List;

public interface FishingRecordService {

    void add(Long userId, FishingRecordDTO dto);

    void update(Long userId, FishingRecordDTO dto);

    void delete(Long userId, Long recordId);

    FishingRecordVO getDetail(Long userId, Long recordId);

    PageResult<FishingRecordVO> listBySpot(Long userId, Long spotId, PageQuery query);

    PageResult<FishingRecordVO> listByUser(Long userId, PageQuery query);

    List<SeasonStatisticsVO> getSeasonStatistics(Long userId, StatisticsQueryDTO dto);
}
