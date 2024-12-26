package com.sounganization.botanify.common.util;

public class GridUtil {

    public static String[] convertToGrid(double lon, double lat) {
        final double RE = 6371.00877;    // 지구 반경(km)
        final double GRID = 5.0;         // 격자 간격(km)
        final double SLAT1 = 30.0;       // 표준 위도1
        final double SLAT2 = 60.0;       // 표준 위도2
        final double OLON = 126.0;       // 기준점 경도
        final double OLAT = 38.0;        // 기준점 위도
        final double XO = 43;            // 기준점 X좌표
        final double YO = 136;           // 기준점 Y좌표

        final double DEGRAD = Math.PI / 180.0;
        final double re = RE / GRID;
        final double slat1Rad = SLAT1 * DEGRAD;
        final double slat2Rad = SLAT2 * DEGRAD;
        final double olonRad = OLON * DEGRAD;
        final double olatRad = OLAT * DEGRAD;

        // 중복 연산 변수화
        final double tanSlat1 = Math.tan(Math.PI * 0.25 + slat1Rad * 0.5);
        final double tanSlat2 = Math.tan(Math.PI * 0.25 + slat2Rad * 0.5);
        final double sn = Math.log(Math.cos(slat1Rad) / Math.cos(slat2Rad)) / Math.log(tanSlat2 / tanSlat1);

        final double sf = Math.pow(tanSlat1, sn) * Math.cos(slat1Rad) / sn;
        final double ro = re * sf / Math.pow(Math.tan(Math.PI * 0.25 + olatRad * 0.5), sn);

        double ra = re * sf / Math.pow(Math.tan(Math.PI * 0.25 + lat * DEGRAD * 0.5), sn);
        double theta = lon * DEGRAD - olonRad;

        if (theta > Math.PI) theta -= 2.0 * Math.PI;
        if (theta < -Math.PI) theta += 2.0 * Math.PI;
        theta *= sn;

        int x = (int) (ra * Math.sin(theta) + XO + 0.5);
        int y = (int) (ro - ra * Math.cos(theta) + YO + 0.5);

        return new String[]{String.valueOf(x), String.valueOf(y)};
    }
}
