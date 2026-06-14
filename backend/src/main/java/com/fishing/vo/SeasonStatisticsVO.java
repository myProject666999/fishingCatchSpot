package com.fishing.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SeasonStatisticsVO {

    private String month;

    private List<SpeciesStatVO> speciesStats;

    private Long totalCount;

    private BigDecimal totalWeight;
}
