package com.fishing.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class FishingRecordDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long recordId;

    @NotNull(message = "钓点ID不能为空")
    private Long spotId;

    @NotNull(message = "垂钓日期不能为空")
    private LocalDate fishingDate;

    private LocalTime startTime;

    private LocalTime endTime;

    @NotNull(message = "垂钓方式不能为空")
    private Integer fishingMethod;

    private String bait;

    private String weather;

    private String temperature;

    private String windDirection;

    private String windLevel;

    private String waterLevel;

    private String remark;

    private List<String> images;

    private List<CatchDetailDTO> catchDetails;
}
