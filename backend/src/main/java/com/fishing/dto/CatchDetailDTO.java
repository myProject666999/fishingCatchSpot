package com.fishing.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class CatchDetailDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "鱼种ID不能为空")
    private Long speciesId;

    private String speciesName;

    @NotNull(message = "渔获数量不能为空")
    private Integer catchCount;

    @NotNull(message = "总重量不能为空")
    private BigDecimal totalWeight;

    private BigDecimal maxWeight;

    private String remark;
}
