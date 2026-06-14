package com.fishing.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
public class FishingRecordVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long recordId;

    private Long spotId;

    private Long userId;

    private String spotName;

    private LocalDate fishingDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private Integer fishingMethod;

    private String bait;

    private String weather;

    private String temperature;

    private String windDirection;

    private String windLevel;

    private String waterLevel;

    private Integer totalCount;

    private BigDecimal totalWeight;

    private String remark;

    private List<String> images;

    private List<CatchDetailVO> catchDetails;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
