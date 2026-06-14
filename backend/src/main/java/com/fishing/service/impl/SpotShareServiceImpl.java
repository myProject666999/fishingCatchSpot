package com.fishing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fishing.common.BusinessException;
import com.fishing.common.GeoUtil;
import com.fishing.common.PageQuery;
import com.fishing.common.PageResult;
import com.fishing.dto.SpotShareDTO;
import com.fishing.entity.FishingSpot;
import com.fishing.entity.SpotShare;
import com.fishing.entity.SysUser;
import com.fishing.mapper.FishingSpotMapper;
import com.fishing.mapper.SpotShareMapper;
import com.fishing.mapper.SysUserMapper;
import com.fishing.service.SpotShareService;
import com.fishing.vo.SpotShareVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SpotShareServiceImpl implements SpotShareService {

    private static final String SHARE_GEO_KEY = "fishing:spot:share:geo";

    @Autowired
    private SpotShareMapper spotShareMapper;

    @Autowired
    private FishingSpotMapper fishingSpotMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void share(Long userId, SpotShareDTO dto) {
        log.debug("用户分享钓点，userId: {}, spotId: {}", userId, dto.getSpotId());
        FishingSpot spot = fishingSpotMapper.selectById(dto.getSpotId());
        if (spot == null) {
            throw new BusinessException("钓点不存在");
        }
        if (!spot.getUserId().equals(userId)) {
            throw new BusinessException("无权分享该钓点");
        }

        double[] blurred = GeoUtil.blurCoordinate(
                spot.getLongitude().doubleValue(),
                spot.getLatitude().doubleValue());

        SpotShare share = new SpotShare();
        BeanUtils.copyProperties(dto, share);
        share.setShareUserId(userId);
        share.setBlurredLng(BigDecimal.valueOf(blurred[0]));
        share.setBlurredLat(BigDecimal.valueOf(blurred[1]));
        share.setViewCount(0);
        share.setLikeCount(0);
        share.setShareStatus(1);
        spotShareMapper.insert(share);

        LambdaUpdateWrapper<FishingSpot> spotWrapper = new LambdaUpdateWrapper<>();
        spotWrapper.eq(FishingSpot::getSpotId, dto.getSpotId())
                .set(FishingSpot::getIsShared, 1);
        fishingSpotMapper.update(null, spotWrapper);

        try {
            stringRedisTemplate.opsForGeo().add(SHARE_GEO_KEY,
                    new Point(blurred[0], blurred[1]),
                    share.getShareId().toString());
            log.debug("分享钓点模糊坐标已写入 Redis GEO，shareId: {}", share.getShareId());
        } catch (Exception e) {
            log.warn("分享钓点 Redis GEO 写入失败，shareId: {}", share.getShareId(), e);
        }

        log.debug("钓点分享完成，shareId: {}", share.getShareId());
    }

    @Override
    public PageResult<SpotShareVO> list(String keyword, Integer waterType, PageQuery query) {
        log.debug("分页查询分享列表，keyword: {}, waterType: {}, pageNum: {}, pageSize: {}",
                keyword, waterType, query.getPageNum(), query.getPageSize());

        List<Map<String, Object>> allResults = spotShareMapper.selectShareList(keyword, waterType);

        int total = allResults.size();
        int fromIndex = (query.getPageNum() - 1) * query.getPageSize();
        int toIndex = Math.min(fromIndex + query.getPageSize(), total);
        List<Map<String, Object>> pageResults;
        if (fromIndex >= total) {
            pageResults = new ArrayList<>();
        } else {
            pageResults = allResults.subList(fromIndex, toIndex);
        }

        List<SpotShareVO> voList = pageResults.stream().map(map -> {
            SpotShareVO vo = new SpotShareVO();
            vo.setShareId(((Number) map.get("shareId")).longValue());
            vo.setSpotId(((Number) map.get("spotId")).longValue());
            vo.setShareUserId(((Number) map.get("shareUserId")).longValue());
            vo.setShareTitle((String) map.get("shareTitle"));
            vo.setShareContent((String) map.get("shareContent"));
            vo.setBlurredLng((BigDecimal) map.get("blurredLng"));
            vo.setBlurredLat((BigDecimal) map.get("blurredLat"));
            vo.setViewCount((Integer) map.get("viewCount"));
            vo.setLikeCount((Integer) map.get("likeCount"));
            vo.setShareStatus((Integer) map.get("shareStatus"));
            vo.setNickname((String) map.get("nickname"));
            vo.setAvatar((String) map.get("avatar"));
            vo.setSpotName((String) map.get("spotName"));
            vo.setWaterType((Integer) map.get("waterType"));
            vo.setCreateTime((java.time.LocalDateTime) map.get("createTime"));
            return vo;
        }).collect(Collectors.toList());

        return new PageResult<>((long) total, voList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SpotShareVO getDetail(Long shareId) {
        log.debug("查询分享详情，shareId: {}", shareId);
        SpotShare share = spotShareMapper.selectById(shareId);
        if (share == null) {
            throw new BusinessException("分享不存在");
        }

        spotShareMapper.incrementViewCount(shareId);
        share.setViewCount(share.getViewCount() + 1);

        SpotShareVO vo = new SpotShareVO();
        BeanUtils.copyProperties(share, vo);

        FishingSpot spot = fishingSpotMapper.selectById(share.getSpotId());
        if (spot != null) {
            vo.setSpotName(spot.getSpotName());
            vo.setWaterType(spot.getWaterType());
        }

        SysUser user = sysUserMapper.selectById(share.getShareUserId());
        if (user != null) {
            vo.setNickname(user.getNickname());
            vo.setAvatar(user.getAvatar());
        }

        log.debug("分享详情浏览量+1，shareId: {}, viewCount: {}", shareId, vo.getViewCount());
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void like(Long userId, Long shareId) {
        log.debug("用户点赞分享，userId: {}, shareId: {}", userId, shareId);
        SpotShare share = spotShareMapper.selectById(shareId);
        if (share == null) {
            throw new BusinessException("分享不存在");
        }
        spotShareMapper.incrementLikeCount(shareId);
        log.debug("分享点赞完成，userId: {}, shareId: {}", userId, shareId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long userId, Long shareId) {
        log.debug("用户删除分享，userId: {}, shareId: {}", userId, shareId);
        SpotShare share = spotShareMapper.selectById(shareId);
        if (share == null) {
            throw new BusinessException("分享不存在");
        }
        if (!share.getShareUserId().equals(userId)) {
            throw new BusinessException("无权删除该分享");
        }

        spotShareMapper.deleteById(shareId);

        try {
            stringRedisTemplate.opsForGeo().remove(SHARE_GEO_KEY, shareId.toString());
            log.debug("分享已从 Redis GEO 移除，shareId: {}", shareId);
        } catch (Exception e) {
            log.warn("分享 Redis GEO 删除失败，shareId: {}", shareId, e);
        }

        LambdaQueryWrapper<SpotShare> checkWrapper = new LambdaQueryWrapper<>();
        checkWrapper.eq(SpotShare::getSpotId, share.getSpotId())
                .eq(SpotShare::getShareStatus, 1);
        Long remainCount = spotShareMapper.selectCount(checkWrapper);
        if (remainCount == 0) {
            LambdaUpdateWrapper<FishingSpot> spotWrapper = new LambdaUpdateWrapper<>();
            spotWrapper.eq(FishingSpot::getSpotId, share.getSpotId())
                    .set(FishingSpot::getIsShared, 0);
            fishingSpotMapper.update(null, spotWrapper);
            log.debug("该钓点无其他分享，is_shared 重置为 0，spotId: {}", share.getSpotId());
        }

        log.debug("分享删除完成，shareId: {}", shareId);
    }
}
