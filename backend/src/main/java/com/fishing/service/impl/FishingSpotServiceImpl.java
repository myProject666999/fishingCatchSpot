package com.fishing.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fishing.common.BusinessException;
import com.fishing.common.PageQuery;
import com.fishing.common.PageResult;
import com.fishing.dto.FishingSpotDTO;
import com.fishing.entity.FishingSpot;
import com.fishing.mapper.FishingSpotMapper;
import com.fishing.service.FishingSpotService;
import com.fishing.vo.FishingSpotVO;
import com.fishing.vo.SpotNearbyVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FishingSpotServiceImpl implements FishingSpotService {

    private static final String GEO_KEY = "fishing:spot:geo";

    @Autowired
    private FishingSpotMapper fishingSpotMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(Long userId, FishingSpotDTO dto) {
        log.debug("用户新增钓点，userId: {}, spotName: {}", userId, dto.getSpotName());
        FishingSpot spot = new FishingSpot();
        BeanUtils.copyProperties(dto, spot);
        spot.setUserId(userId);
        spot.setImages(JSON.toJSONString(dto.getImages()));
        spot.setIsShared(0);
        spot.setIsFavorite(0);
        spot.setRecordCount(0);
        fishingSpotMapper.insert(spot);

        try {
            stringRedisTemplate.opsForGeo().add(GEO_KEY,
                    new Point(dto.getLongitude().doubleValue(), dto.getLatitude().doubleValue()),
                    spot.getSpotId().toString());
            log.debug("钓点坐标已写入 Redis GEO，spotId: {}, lng: {}, lat: {}",
                    spot.getSpotId(), dto.getLongitude(), dto.getLatitude());
        } catch (Exception e) {
            log.warn("Redis GEO 写入失败，spotId: {}", spot.getSpotId(), e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long userId, FishingSpotDTO dto) {
        log.debug("用户更新钓点，userId: {}, spotId: {}", userId, dto.getSpotId());
        FishingSpot spot = fishingSpotMapper.selectById(dto.getSpotId());
        if (spot == null) {
            throw new BusinessException("钓点不存在");
        }
        if (!spot.getUserId().equals(userId)) {
            throw new BusinessException("无权修改该钓点");
        }
        BeanUtils.copyProperties(dto, spot);
        spot.setImages(JSON.toJSONString(dto.getImages()));
        fishingSpotMapper.updateById(spot);

        try {
            stringRedisTemplate.opsForGeo().remove(GEO_KEY, dto.getSpotId().toString());
            stringRedisTemplate.opsForGeo().add(GEO_KEY,
                    new Point(dto.getLongitude().doubleValue(), dto.getLatitude().doubleValue()),
                    dto.getSpotId().toString());
            log.debug("钓点 Redis GEO 坐标已更新，spotId: {}", dto.getSpotId());
        } catch (Exception e) {
            log.warn("Redis GEO 更新失败，spotId: {}", dto.getSpotId(), e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long userId, Long spotId) {
        log.debug("用户删除钓点，userId: {}, spotId: {}", userId, spotId);
        FishingSpot spot = fishingSpotMapper.selectById(spotId);
        if (spot == null) {
            throw new BusinessException("钓点不存在");
        }
        if (!spot.getUserId().equals(userId)) {
            throw new BusinessException("无权删除该钓点");
        }
        fishingSpotMapper.deleteById(spotId);

        try {
            stringRedisTemplate.opsForGeo().remove(GEO_KEY, spotId.toString());
            log.debug("钓点已从 Redis GEO 移除，spotId: {}", spotId);
        } catch (Exception e) {
            log.warn("Redis GEO 删除失败，spotId: {}", spotId, e);
        }
    }

    @Override
    public FishingSpotVO getDetail(Long userId, Long spotId) {
        log.debug("查询钓点详情，userId: {}, spotId: {}", userId, spotId);
        FishingSpot spot = fishingSpotMapper.selectById(spotId);
        if (spot == null) {
            throw new BusinessException("钓点不存在");
        }
        if (!spot.getUserId().equals(userId)) {
            throw new BusinessException("无权查看该钓点");
        }
        FishingSpotVO vo = new FishingSpotVO();
        BeanUtils.copyProperties(spot, vo);
        if (spot.getImages() != null) {
            vo.setImages(JSON.parseArray(spot.getImages(), String.class));
        }
        return vo;
    }

    @Override
    public PageResult<FishingSpotVO> listByUser(Long userId, PageQuery query) {
        log.debug("分页查询用户钓点列表，userId: {}, pageNum: {}, pageSize: {}",
                userId, query.getPageNum(), query.getPageSize());
        Page<FishingSpot> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<FishingSpot> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FishingSpot::getUserId, userId)
                .orderByDesc(FishingSpot::getCreateTime);
        Page<FishingSpot> result = fishingSpotMapper.selectPage(page, wrapper);

        List<FishingSpotVO> voList = result.getRecords().stream().map(spot -> {
            FishingSpotVO vo = new FishingSpotVO();
            BeanUtils.copyProperties(spot, vo);
            if (spot.getImages() != null) {
                vo.setImages(JSON.parseArray(spot.getImages(), String.class));
            }
            return vo;
        }).collect(Collectors.toList());

        return new PageResult<>(result.getTotal(), voList);
    }

    @Override
    public List<SpotNearbyVO> listNearby(Long userId, BigDecimal lng, BigDecimal lat, Double radius) {
        log.debug("查询附近钓点，userId: {}, lng: {}, lat: {}, radius: {}km",
                userId, lng, lat, radius);

        Map<Long, Double> distanceMap = new HashMap<>();
        List<Long> spotIds = new ArrayList<>();

        boolean redisAvailable = true;
        try {
            Circle circle = new Circle(
                    new Point(lng.doubleValue(), lat.doubleValue()),
                    new Distance(radius, Metrics.KILOMETERS));
            GeoResults<RedisGeoCommands.GeoLocation<String>> results =
                    stringRedisTemplate.opsForGeo().radius(GEO_KEY, circle,
                            RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                                    .includeDistance()
                                    .sortAscending());

            if (results != null && !results.getContent().isEmpty()) {
                results.forEach(result -> {
                    String member = result.getContent().getName();
                    Double distance = result.getDistance().getValue();
                    Long spotId = Long.parseLong(member);
                    spotIds.add(spotId);
                    distanceMap.put(spotId, distance);
                });
            }
            log.debug("Redis GEO 查询完成，找到 {} 个钓点", spotIds.size());
        } catch (Exception e) {
            log.warn("Redis GEO 查询失败，降级使用 MySQL 查询", e);
            redisAvailable = false;
        }

        List<SpotNearbyVO> voList;
        if (redisAvailable && !spotIds.isEmpty()) {
            LambdaQueryWrapper<FishingSpot> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(FishingSpot::getUserId, userId)
                    .in(FishingSpot::getSpotId, spotIds);
            List<FishingSpot> spots = fishingSpotMapper.selectList(wrapper);

            voList = spots.stream().map(spot -> {
                SpotNearbyVO vo = new SpotNearbyVO();
                BeanUtils.copyProperties(spot, vo);
                vo.setDistance(distanceMap.get(spot.getSpotId()));
                return vo;
            }).sorted((a, b) -> Double.compare(a.getDistance(), b.getDistance()))
                    .collect(Collectors.toList());
        } else {
            List<Map<String, Object>> fallbackResults =
                    fishingSpotMapper.selectNearbySpots(userId, lng, lat, radius);
            voList = fallbackResults.stream().map(map -> {
                SpotNearbyVO vo = new SpotNearbyVO();
                vo.setSpotId(((Number) map.get("spotId")).longValue());
                vo.setUserId(((Number) map.get("userId")).longValue());
                vo.setSpotName((String) map.get("spotName"));
                vo.setWaterType((Integer) map.get("waterType"));
                vo.setLongitude((BigDecimal) map.get("longitude"));
                vo.setLatitude((BigDecimal) map.get("latitude"));
                vo.setAddress((String) map.get("address"));
                vo.setDescription((String) map.get("description"));
                vo.setRecordCount((Integer) map.get("recordCount"));
                vo.setDistance(((Number) map.get("distance")).doubleValue());
                return vo;
            }).collect(Collectors.toList());
            log.debug("MySQL fallback 查询完成，找到 {} 个钓点", voList.size());
        }

        return voList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void toggleFavorite(Long userId, Long spotId) {
        log.debug("用户切换钓点收藏状态，userId: {}, spotId: {}", userId, spotId);
        FishingSpot spot = fishingSpotMapper.selectById(spotId);
        if (spot == null) {
            throw new BusinessException("钓点不存在");
        }
        if (!spot.getUserId().equals(userId)) {
            throw new BusinessException("无权操作该钓点");
        }
        Integer newStatus = (spot.getIsFavorite() == null || spot.getIsFavorite() == 0) ? 1 : 0;
        LambdaUpdateWrapper<FishingSpot> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(FishingSpot::getSpotId, spotId)
                .set(FishingSpot::getIsFavorite, newStatus);
        fishingSpotMapper.update(null, wrapper);
        log.debug("钓点收藏状态已更新，spotId: {}, isFavorite: {}", spotId, newStatus);
    }
}
