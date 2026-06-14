package com.fishing.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WaterTypeEnum {

    RESERVOIR(1, "水库"),
    LAKE(2, "湖"),
    RIVER(3, "河"),
    SEA(4, "海");

    private final Integer code;
    private final String desc;

    public static String getDescByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (WaterTypeEnum e : WaterTypeEnum.values()) {
            if (e.getCode().equals(code)) {
                return e.getDesc();
            }
        }
        return null;
    }
}
