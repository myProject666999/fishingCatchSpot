package com.fishing.common;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class GeoUtil {

    private static final double EARTH_RADIUS = 6371.0;

    public static double[] blurCoordinate(double longitude, double latitude) {
        BigDecimal lng = BigDecimal.valueOf(longitude).setScale(2, RoundingMode.HALF_UP);
        BigDecimal lat = BigDecimal.valueOf(latitude).setScale(2, RoundingMode.HALF_UP);
        return new double[]{lng.doubleValue(), lat.doubleValue()};
    }

    public static double calculateDistance(double lon1, double lat1, double lon2, double lat2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double radLat1 = Math.toRadians(lat1);
        double radLat2 = Math.toRadians(lat2);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }
}
