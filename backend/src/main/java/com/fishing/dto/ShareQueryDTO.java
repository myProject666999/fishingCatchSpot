package com.fishing.dto;

import com.fishing.common.PageQuery;
import lombok.Data;

import java.io.Serializable;

@Data
public class ShareQueryDTO extends PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    private String keyword;

    private Integer waterType;
}
