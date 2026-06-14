package com.fishing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fishing.entity.FishingSpot;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface FishingSpotMapper extends BaseMapper<FishingSpot> {

    List<Map<String, Object>> selectNearbySpots(@Param("userId") Long userId,
                                                @Param("lng") BigDecimal lng,
                                                @Param("lat") BigDecimal lat,
                                                @Param("radius") Double radius);

    List<Map<String, Object>> selectSpotWithRecordCount();
}
