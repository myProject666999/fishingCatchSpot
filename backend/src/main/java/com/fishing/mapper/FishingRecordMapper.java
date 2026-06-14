package com.fishing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fishing.entity.FishingRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface FishingRecordMapper extends BaseMapper<FishingRecord> {

    List<FishingRecord> selectBySpotId(@Param("spotId") Long spotId);

    List<Map<String, Object>> selectMonthlyStatistics(@Param("userId") Long userId,
                                                      @Param("startMonth") String startMonth,
                                                      @Param("endMonth") String endMonth,
                                                      @Param("waterType") Integer waterType,
                                                      @Param("fishingMethod") Integer fishingMethod);
}
