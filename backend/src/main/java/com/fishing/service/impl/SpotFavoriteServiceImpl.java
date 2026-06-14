package com.fishing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fishing.common.BusinessException;
import com.fishing.common.PageQuery;
import com.fishing.common.PageResult;
import com.fishing.dto.SpotFavoriteDTO;
import com.fishing.entity.FishingSpot;
import com.fishing.entity.SpotFavorite;
import com.fishing.entity.SpotShare;
import com.fishing.entity.SysUser;
import com.fishing.mapper.FishingSpotMapper;
import com.fishing.mapper.SpotFavoriteMapper;
import com.fishing.mapper.SpotShareMapper;
import com.fishing.mapper.SysUserMapper;
import com.fishing.service.SpotFavoriteService;
import com.fishing.vo.SpotFavoriteVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SpotFavoriteServiceImpl implements SpotFavoriteService {

    @Autowired
    private SpotFavoriteMapper spotFavoriteMapper;

    @Autowired
    private SpotShareMapper spotShareMapper;

    @Autowired
    private FishingSpotMapper fishingSpotMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(Long userId, SpotFavoriteDTO dto) {
        log.debug("用户收藏分享钓点，userId: {}, shareId: {}, spotId: {}",
                userId, dto.getShareId(), dto.getSpotId());

        LambdaQueryWrapper<SpotFavorite> checkWrapper = new LambdaQueryWrapper<>();
        checkWrapper.eq(SpotFavorite::getUserId, userId)
                .eq(SpotFavorite::getShareId, dto.getShareId());
        Long existCount = spotFavoriteMapper.selectCount(checkWrapper);
        if (existCount > 0) {
            throw new BusinessException("该分享已收藏");
        }

        SpotShare share = spotShareMapper.selectById(dto.getShareId());
        if (share == null) {
            throw new BusinessException("分享不存在");
        }

        SpotFavorite favorite = new SpotFavorite();
        BeanUtils.copyProperties(dto, favorite);
        favorite.setUserId(userId);
        spotFavoriteMapper.insert(favorite);

        log.debug("收藏成功，favoriteId: {}", favorite.getFavoriteId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long userId, Long favoriteId) {
        log.debug("用户取消收藏，userId: {}, favoriteId: {}", userId, favoriteId);
        SpotFavorite favorite = spotFavoriteMapper.selectById(favoriteId);
        if (favorite == null) {
            throw new BusinessException("收藏不存在");
        }
        if (!favorite.getUserId().equals(userId)) {
            throw new BusinessException("无权取消该收藏");
        }
        spotFavoriteMapper.deleteById(favoriteId);
        log.debug("取消收藏成功，favoriteId: {}", favoriteId);
    }

    @Override
    public PageResult<SpotFavoriteVO> listByUser(Long userId, PageQuery query) {
        log.debug("分页查询用户收藏列表，userId: {}, pageNum: {}, pageSize: {}",
                userId, query.getPageNum(), query.getPageSize());
        Page<SpotFavorite> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<SpotFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SpotFavorite::getUserId, userId)
                .orderByDesc(SpotFavorite::getCreateTime);
        Page<SpotFavorite> result = spotFavoriteMapper.selectPage(page, wrapper);

        List<Long> shareIds = result.getRecords().stream()
                .map(SpotFavorite::getShareId)
                .distinct()
                .collect(Collectors.toList());
        List<Long> spotIds = result.getRecords().stream()
                .map(SpotFavorite::getSpotId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, SpotShare> shareMap = shareIds.isEmpty() ? Map.of() :
                spotShareMapper.selectBatchIds(shareIds).stream()
                        .collect(Collectors.toMap(SpotShare::getShareId, s -> s));
        Map<Long, FishingSpot> spotMap = spotIds.isEmpty() ? Map.of() :
                fishingSpotMapper.selectBatchIds(spotIds).stream()
                        .collect(Collectors.toMap(FishingSpot::getSpotId, s -> s));

        List<Long> userIds = shareMap.values().stream()
                .map(SpotShare::getShareUserId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, SysUser> userMap = userIds.isEmpty() ? Map.of() :
                sysUserMapper.selectBatchIds(userIds).stream()
                        .collect(Collectors.toMap(SysUser::getUserId, u -> u));

        List<SpotFavoriteVO> voList = result.getRecords().stream().map(favorite -> {
            SpotFavoriteVO vo = new SpotFavoriteVO();
            BeanUtils.copyProperties(favorite, vo);

            SpotShare share = shareMap.get(favorite.getShareId());
            if (share != null) {
                vo.setShareTitle(share.getShareTitle());
                vo.setShareContent(share.getShareContent());
                vo.setBlurredLng(share.getBlurredLng());
                vo.setBlurredLat(share.getBlurredLat());

                SysUser user = userMap.get(share.getShareUserId());
                if (user != null) {
                    vo.setNickname(user.getNickname());
                    vo.setAvatar(user.getAvatar());
                }
            }

            FishingSpot spot = spotMap.get(favorite.getSpotId());
            if (spot != null) {
                vo.setSpotName(spot.getSpotName());
                vo.setWaterType(spot.getWaterType());
            }

            return vo;
        }).collect(Collectors.toList());

        return new PageResult<>(result.getTotal(), voList);
    }
}
