package com.fishing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fishing.entity.CatchDetail;
import com.fishing.mapper.CatchDetailMapper;
import com.fishing.service.CatchDetailService;
import com.fishing.vo.CatchDetailVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CatchDetailServiceImpl implements CatchDetailService {

    @Autowired
    private CatchDetailMapper catchDetailMapper;

    @Override
    public List<CatchDetailVO> getByRecordId(Long recordId) {
        log.debug("根据记录ID查询渔获明细，recordId: {}", recordId);
        LambdaQueryWrapper<CatchDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CatchDetail::getRecordId, recordId);
        List<CatchDetail> details = catchDetailMapper.selectList(wrapper);
        return details.stream().map(detail -> {
            CatchDetailVO vo = new CatchDetailVO();
            BeanUtils.copyProperties(detail, vo);
            return vo;
        }).collect(Collectors.toList());
    }
}
