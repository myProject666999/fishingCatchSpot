package com.fishing.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SpotNearbyVO {

    private Long spotId;

    private Long userId;

    private String spotName;

    private Integer waterType;

    private String waterTypeName;

    private BigDecimal longitude;

    private BigDecimal latitude;

    private String address;

    private String description;

    private Double distance;

    private Integer isShared;

    private Integer recordCount;
}
