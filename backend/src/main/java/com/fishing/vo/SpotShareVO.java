package com.fishing.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SpotShareVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long shareId;

    private Long spotId;

    private Long shareUserId;

    private String nickname;

    private String avatar;

    private String shareTitle;

    private String shareContent;

    private BigDecimal blurredLng;

    private BigDecimal blurredLat;

    private Integer viewCount;

    private Integer likeCount;

    private Integer shareStatus;

    private String spotName;

    private Integer waterType;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
