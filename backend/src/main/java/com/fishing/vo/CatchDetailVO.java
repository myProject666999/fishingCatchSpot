package com.fishing.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class CatchDetailVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long detailId;

    private Long recordId;

    private Long speciesId;

    private String speciesName;

    private Integer catchCount;

    private BigDecimal totalWeight;

    private BigDecimal maxWeight;

    private String remark;
}
