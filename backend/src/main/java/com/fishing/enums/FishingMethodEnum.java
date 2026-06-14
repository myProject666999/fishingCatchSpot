package com.fishing.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FishingMethodEnum {

    HAND_ROD(1, "手竿"),
    ROCK_ROD(2, "矶竿"),
    LURE(3, "路亚"),
    SEA_FISHING(4, "海钓");

    private final Integer code;
    private final String desc;

    public static String getDescByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (FishingMethodEnum e : FishingMethodEnum.values()) {
            if (e.getCode().equals(code)) {
                return e.getDesc();
            }
        }
        return null;
    }
}
