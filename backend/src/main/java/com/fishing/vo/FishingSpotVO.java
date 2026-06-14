package com.fishing.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class FishingSpotVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long spotId;

    private Long userId;

    private String spotName;

    private Integer waterType;

    private BigDecimal longitude;

    private BigDecimal latitude;

    private String address;

    private String description;

    private List<String> images;

    private Integer isShared;

    private Integer isFavorite;

    private Integer recordCount;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
