package com.fishing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fishing.entity.SpotShare;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface SpotShareMapper extends BaseMapper<SpotShare> {

    List<Map<String, Object>> selectShareList(@Param("keyword") String keyword,
                                              @Param("waterType") Integer waterType);

    void incrementViewCount(@Param("shareId") Long shareId);

    void incrementLikeCount(@Param("shareId") Long shareId);
}
