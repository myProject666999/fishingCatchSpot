package com.fishing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fishing.entity.FishSpecies;
import com.fishing.mapper.FishSpeciesMapper;
import com.fishing.service.FishSpeciesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class FishSpeciesServiceImpl implements FishSpeciesService {

    @Autowired
    private FishSpeciesMapper fishSpeciesMapper;

    @Override
    public List<FishSpecies> listAll() {
        log.debug("查询全部鱼种列表");
        LambdaQueryWrapper<FishSpecies> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(FishSpecies::getSortOrder);
        return fishSpeciesMapper.selectList(wrapper);
    }

    @Override
    public FishSpecies getById(Long id) {
        log.debug("按ID查询鱼种，id: {}", id);
        return fishSpeciesMapper.selectById(id);
    }
}
