package com.fishing.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fishing.common.BusinessException;
import com.fishing.common.PageQuery;
import com.fishing.common.PageResult;
import com.fishing.dto.CatchDetailDTO;
import com.fishing.dto.FishingRecordDTO;
import com.fishing.dto.StatisticsQueryDTO;
import com.fishing.entity.CatchDetail;
import com.fishing.entity.FishingRecord;
import com.fishing.entity.FishingSpot;
import com.fishing.mapper.CatchDetailMapper;
import com.fishing.mapper.FishingRecordMapper;
import com.fishing.mapper.FishingSpotMapper;
import com.fishing.service.FishingRecordService;
import com.fishing.vo.CatchDetailVO;
import com.fishing.vo.FishingRecordVO;
import com.fishing.vo.SeasonStatisticsVO;
import com.fishing.vo.SpeciesStatVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FishingRecordServiceImpl implements FishingRecordService {

    @Autowired
    private FishingRecordMapper fishingRecordMapper;

    @Autowired
    private CatchDetailMapper catchDetailMapper;

    @Autowired
    private FishingSpotMapper fishingSpotMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(Long userId, FishingRecordDTO dto) {
        log.debug("用户新增出钓记录，userId: {}, spotId: {}", userId, dto.getSpotId());
        FishingSpot spot = fishingSpotMapper.selectById(dto.getSpotId());
        if (spot == null) {
            throw new BusinessException("钓点不存在");
        }
        if (!spot.getUserId().equals(userId)) {
            throw new BusinessException("无权在该钓点添加记录");
        }

        FishingRecord record = new FishingRecord();
        BeanUtils.copyProperties(dto, record);
        record.setUserId(userId);
        record.setImages(JSON.toJSONString(dto.getImages()));

        int totalCount = 0;
        BigDecimal totalWeight = BigDecimal.ZERO;
        if (dto.getCatchDetails() != null) {
            for (CatchDetailDTO detail : dto.getCatchDetails()) {
                totalCount += detail.getCatchCount() != null ? detail.getCatchCount() : 0;
                totalWeight = totalWeight.add(detail.getTotalWeight() != null ? detail.getTotalWeight() : BigDecimal.ZERO);
            }
        }
        record.setTotalCount(totalCount);
        record.setTotalWeight(totalWeight);

        fishingRecordMapper.insert(record);

        if (dto.getCatchDetails() != null && !dto.getCatchDetails().isEmpty()) {
            for (CatchDetailDTO detailDTO : dto.getCatchDetails()) {
                CatchDetail detail = new CatchDetail();
                BeanUtils.copyProperties(detailDTO, detail);
                detail.setRecordId(record.getRecordId());
                catchDetailMapper.insert(detail);
            }
        }

        LambdaUpdateWrapper<FishingSpot> spotWrapper = new LambdaUpdateWrapper<>();
        spotWrapper.eq(FishingSpot::getSpotId, dto.getSpotId())
                .setSql("record_count = record_count + 1");
        fishingSpotMapper.update(null, spotWrapper);

        log.debug("出钓记录新增完成，recordId: {}, spot.record_count 已+1", record.getRecordId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long userId, FishingRecordDTO dto) {
        log.debug("用户更新出钓记录，userId: {}, recordId: {}", userId, dto.getRecordId());
        FishingRecord record = fishingRecordMapper.selectById(dto.getRecordId());
        if (record == null) {
            throw new BusinessException("记录不存在");
        }
        if (!record.getUserId().equals(userId)) {
            throw new BusinessException("无权修改该记录");
        }

        BeanUtils.copyProperties(dto, record);
        record.setImages(JSON.toJSONString(dto.getImages()));

        int totalCount = 0;
        BigDecimal totalWeight = BigDecimal.ZERO;
        if (dto.getCatchDetails() != null) {
            for (CatchDetailDTO detail : dto.getCatchDetails()) {
                totalCount += detail.getCatchCount() != null ? detail.getCatchCount() : 0;
                totalWeight = totalWeight.add(detail.getTotalWeight() != null ? detail.getTotalWeight() : BigDecimal.ZERO);
            }
        }
        record.setTotalCount(totalCount);
        record.setTotalWeight(totalWeight);

        fishingRecordMapper.updateById(record);

        LambdaQueryWrapper<CatchDetail> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(CatchDetail::getRecordId, dto.getRecordId());
        catchDetailMapper.delete(deleteWrapper);

        if (dto.getCatchDetails() != null && !dto.getCatchDetails().isEmpty()) {
            for (CatchDetailDTO detailDTO : dto.getCatchDetails()) {
                CatchDetail detail = new CatchDetail();
                BeanUtils.copyProperties(detailDTO, detail);
                detail.setRecordId(dto.getRecordId());
                catchDetailMapper.insert(detail);
            }
        }

        log.debug("出钓记录更新完成，recordId: {}", dto.getRecordId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long userId, Long recordId) {
        log.debug("用户删除出钓记录，userId: {}, recordId: {}", userId, recordId);
        FishingRecord record = fishingRecordMapper.selectById(recordId);
        if (record == null) {
            throw new BusinessException("记录不存在");
        }
        if (!record.getUserId().equals(userId)) {
            throw new BusinessException("无权删除该记录");
        }

        fishingRecordMapper.deleteById(recordId);

        LambdaUpdateWrapper<FishingSpot> spotWrapper = new LambdaUpdateWrapper<>();
        spotWrapper.eq(FishingSpot::getSpotId, record.getSpotId())
                .setSql("record_count = GREATEST(record_count - 1, 0)");
        fishingSpotMapper.update(null, spotWrapper);

        log.debug("出钓记录删除完成，recordId: {}, spot.record_count 已-1", recordId);
    }

    @Override
    public FishingRecordVO getDetail(Long userId, Long recordId) {
        log.debug("查询出钓记录详情，userId: {}, recordId: {}", userId, recordId);
        FishingRecord record = fishingRecordMapper.selectById(recordId);
        if (record == null) {
            throw new BusinessException("记录不存在");
        }
        if (!record.getUserId().equals(userId)) {
            throw new BusinessException("无权查看该记录");
        }

        FishingRecordVO vo = new FishingRecordVO();
        BeanUtils.copyProperties(record, vo);
        if (record.getImages() != null) {
            vo.setImages(JSON.parseArray(record.getImages(), String.class));
        }

        FishingSpot spot = fishingSpotMapper.selectById(record.getSpotId());
        if (spot != null) {
            vo.setSpotName(spot.getSpotName());
        }

        LambdaQueryWrapper<CatchDetail> detailWrapper = new LambdaQueryWrapper<>();
        detailWrapper.eq(CatchDetail::getRecordId, recordId);
        List<CatchDetail> details = catchDetailMapper.selectList(detailWrapper);
        List<CatchDetailVO> detailVOList = details.stream().map(detail -> {
            CatchDetailVO detailVO = new CatchDetailVO();
            BeanUtils.copyProperties(detail, detailVO);
            return detailVO;
        }).collect(Collectors.toList());
        vo.setCatchDetails(detailVOList);

        return vo;
    }

    @Override
    public PageResult<FishingRecordVO> listBySpot(Long userId, Long spotId, PageQuery query) {
        log.debug("分页查询钓点下的记录列表，userId: {}, spotId: {}, pageNum: {}, pageSize: {}",
                userId, spotId, query.getPageNum(), query.getPageSize());
        FishingSpot spot = fishingSpotMapper.selectById(spotId);
        if (spot == null) {
            throw new BusinessException("钓点不存在");
        }
        if (!spot.getUserId().equals(userId)) {
            throw new BusinessException("无权查看该钓点的记录");
        }

        Page<FishingRecord> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<FishingRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FishingRecord::getSpotId, spotId)
                .orderByDesc(FishingRecord::getFishingDate, FishingRecord::getCreateTime);
        Page<FishingRecord> result = fishingRecordMapper.selectPage(page, wrapper);

        List<FishingRecordVO> voList = convertToVOList(result.getRecords(), spot.getSpotName());
        return new PageResult<>(result.getTotal(), voList);
    }

    @Override
    public PageResult<FishingRecordVO> listByUser(Long userId, PageQuery query) {
        log.debug("分页查询用户的记录列表，userId: {}, pageNum: {}, pageSize: {}",
                userId, query.getPageNum(), query.getPageSize());
        Page<FishingRecord> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<FishingRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FishingRecord::getUserId, userId)
                .orderByDesc(FishingRecord::getFishingDate, FishingRecord::getCreateTime);
        Page<FishingRecord> result = fishingRecordMapper.selectPage(page, wrapper);

        List<Long> spotIds = result.getRecords().stream()
                .map(FishingRecord::getSpotId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, String> spotNameMap = fishingSpotMapper.selectBatchIds(spotIds).stream()
                .collect(Collectors.toMap(FishingSpot::getSpotId, FishingSpot::getSpotName));

        List<FishingRecordVO> voList = result.getRecords().stream().map(record -> {
            FishingRecordVO vo = new FishingRecordVO();
            BeanUtils.copyProperties(record, vo);
            if (record.getImages() != null) {
                vo.setImages(JSON.parseArray(record.getImages(), String.class));
            }
            vo.setSpotName(spotNameMap.get(record.getSpotId()));
            return vo;
        }).collect(Collectors.toList());

        return new PageResult<>(result.getTotal(), voList);
    }

    @Override
    public List<SeasonStatisticsVO> getSeasonStatistics(Long userId, StatisticsQueryDTO dto) {
        log.debug("查询季节统计，userId: {}, startMonth: {}, endMonth: {}",
                userId, dto.getStartMonth(), dto.getEndMonth());
        List<SeasonStatisticsVO> result = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        YearMonth start = YearMonth.parse(dto.getStartMonth(), formatter);
        YearMonth end = YearMonth.parse(dto.getEndMonth(), formatter);

        YearMonth current = start;
        while (!current.isAfter(end)) {
            String month = current.format(formatter);
            SeasonStatisticsVO monthStat = new SeasonStatisticsVO();
            monthStat.setMonth(month);

            List<Map<String, Object>> speciesStats = catchDetailMapper.selectSpeciesMonthlyStat(
                    userId, month, dto.getWaterType(), dto.getFishingMethod());

            int monthTotalCount = 0;
            BigDecimal monthTotalWeight = BigDecimal.ZERO;
            List<SpeciesStatVO> speciesStatVOList = new ArrayList<>();

            for (Map<String, Object> stat : speciesStats) {
                SpeciesStatVO speciesVO = new SpeciesStatVO();
                speciesVO.setSpeciesId(((Number) stat.get("speciesId")).longValue());
                speciesVO.setSpeciesName((String) stat.get("speciesName"));
                Integer catchCount = stat.get("totalCount") != null ? ((Number) stat.get("totalCount")).intValue() : 0;
                BigDecimal totalWeight = stat.get("totalWeight") != null ? (BigDecimal) stat.get("totalWeight") : BigDecimal.ZERO;
                speciesVO.setTotalCount(catchCount.longValue());
                speciesVO.setTotalWeight(totalWeight);
                monthTotalCount += catchCount;
                monthTotalWeight = monthTotalWeight.add(totalWeight);
                speciesStatVOList.add(speciesVO);
            }

            monthStat.setTotalCount((long) monthTotalCount);
            monthStat.setTotalWeight(monthTotalWeight);

            if (monthTotalCount > 0) {
                for (SpeciesStatVO speciesVO : speciesStatVOList) {
                    BigDecimal rate = BigDecimal.valueOf(speciesVO.getTotalCount())
                            .multiply(BigDecimal.valueOf(100))
                            .divide(BigDecimal.valueOf(monthTotalCount), 2, RoundingMode.HALF_UP);
                    speciesVO.setRate(rate);
                }
            } else {
                for (SpeciesStatVO speciesVO : speciesStatVOList) {
                    speciesVO.setRate(BigDecimal.ZERO);
                }
            }

            monthStat.setSpeciesStats(speciesStatVOList);
            result.add(monthStat);

            current = current.plusMonths(1);
        }

        log.debug("季节统计查询完成，共 {} 个月数据", result.size());
        return result;
    }

    private List<FishingRecordVO> convertToVOList(List<FishingRecord> records, String spotName) {
        return records.stream().map(record -> {
            FishingRecordVO vo = new FishingRecordVO();
            BeanUtils.copyProperties(record, vo);
            if (record.getImages() != null) {
                vo.setImages(JSON.parseArray(record.getImages(), String.class));
            }
            vo.setSpotName(spotName);
            return vo;
        }).collect(Collectors.toList());
    }
}
