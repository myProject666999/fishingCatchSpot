package com.fishing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fishing.entity.CatchDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface CatchDetailMapper extends BaseMapper<CatchDetail> {

    List<Map<String, Object>> selectSpeciesMonthlyStat(@Param("userId") Long userId,
                                                       @Param("month") String month,
                                                       @Param("waterType") Integer waterType,
                                                       @Param("fishingMethod") Integer fishingMethod);
}
