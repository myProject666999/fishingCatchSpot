package com.fishing.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class SpotFavoriteDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "分享ID不能为空")
    private Long shareId;

    @NotNull(message = "钓点ID不能为空")
    private Long spotId;
}
