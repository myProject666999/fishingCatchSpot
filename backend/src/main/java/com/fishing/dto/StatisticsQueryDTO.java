package com.fishing.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class StatisticsQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long userId;

    private String startMonth;

    private String endMonth;

    private Integer waterType;

    private Integer fishingMethod;
}
