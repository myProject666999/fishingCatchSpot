package com.fishing.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SpeciesStatVO {

    private Long speciesId;

    private String speciesName;

    private Long totalCount;

    private BigDecimal totalWeight;

    private BigDecimal rate;
}
