package com.fishing.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class FishingSpotDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long spotId;

    @NotBlank(message = "钓点名称不能为空")
    private String spotName;

    @NotNull(message = "水域类型不能为空")
    private Integer waterType;

    @NotNull(message = "经度不能为空")
    private BigDecimal longitude;

    @NotNull(message = "纬度不能为空")
    private BigDecimal latitude;

    private String address;

    private String description;

    private List<String> images;
}
