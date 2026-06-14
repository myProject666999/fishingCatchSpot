package com.fishing.service;

import com.fishing.common.PageQuery;
import com.fishing.common.PageResult;
import com.fishing.dto.FishingSpotDTO;
import com.fishing.vo.FishingSpotVO;
import com.fishing.vo.SpotNearbyVO;

import java.math.BigDecimal;
import java.util.List;

public interface FishingSpotService {

    void add(Long userId, FishingSpotDTO dto);

    void update(Long userId, FishingSpotDTO dto);

    void delete(Long userId, Long spotId);

    FishingSpotVO getDetail(Long userId, Long spotId);

    PageResult<FishingSpotVO> listByUser(Long userId, PageQuery query);

    List<SpotNearbyVO> listNearby(Long userId, BigDecimal lng, BigDecimal lat, Double radius);

    void toggleFavorite(Long userId, Long spotId);
}
