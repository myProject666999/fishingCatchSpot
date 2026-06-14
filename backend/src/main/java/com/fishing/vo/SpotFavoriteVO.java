package com.fishing.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SpotFavoriteVO {

    private Long favoriteId;

    private Long shareId;

    private Long spotId;

    private String spotName;

    private String createTime;

    private String shareTitle;

    private String shareContent;

    private BigDecimal blurredLng;

    private BigDecimal blurredLat;

    private String nickname;

    private String avatar;

    private Integer waterType;
}
