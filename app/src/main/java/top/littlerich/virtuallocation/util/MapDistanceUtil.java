package top.littlerich.virtuallocation.util;


/**
 * MapDistanceUtil
 *
 * @author binghao.huang
 * @date 2020-03-04
 */
public class MapDistanceUtil {

    private static double PI = 3.14159265;

    public static double A = 6378245.0;
    public static double EE = 0.00669342162296594323;

    public static void main(String[] args) {
        // 116.456298,39.923568
        GeoPoint gcj02 = bd09ToGcj02(39.923568, 116.456298);
        GeoPoint gps = bd09ToGps84(39.923568, 116.456298);
        System.out.println(gcj02);
        System.out.println(gps);

        // 116.397036,39.917834
        GeoPoint bd = gcj02ToBd09(39.917834, 116.397036);
        gps = gcj02ToGps84(39.917834, 116.397036);
        System.out.println(bd);
        System.out.println(gps);

    }

    /**
     * gps84 to gcj02
     *
     * @param lat 纬度
     * @param lon 经度
     * @return gcj02坐标
     */
    public static GeoPoint gps84ToGcj02(double lat, double lon) {
        if (outOfChina(lat, lon)) {
            return null;
        }
        double dLat = transformLat(lon - 105.0, lat - 35.0);
        double dLon = transformLon(lon - 105.0, lat - 35.0);
        double radLat = lat / 180.0 * PI;
        double magic = Math.sin(radLat);
        magic = 1 - EE * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((A * (1 - EE)) / (magic * sqrtMagic) * PI);
        dLon = (dLon * 180.0) / (A / sqrtMagic * Math.cos(radLat) * PI);
        double mgLat = lat + dLat;
        double mgLon = lon + dLon;
        return new GeoPoint(mgLat, mgLon);
    }

    /**
     * gps84 to bd09
     *
     * @param lat 纬度
     * @param lon 经度
     * @return
     */
    public static GeoPoint gps84ToBd09(double lat, double lon) {
        GeoPoint gps = gps84ToGcj02(lat, lon);
        return gcj02ToBd09(gps.getLat(), gps.getLon());
    }

    /**
     * gcj02 to gps84
     *
     * @param lat 纬度
     * @param lon 经度
     * @return
     */
    public static GeoPoint gcj02ToGps84(double lat, double lon) {
        double[] gps = transform(lat, lon);
        double longitude = lon * 2 - gps[1];
        double latitude = lat * 2 - gps[0];
        return new GeoPoint(latitude, longitude);
    }

    /**
     * gcj02 to bd09
     *
     * @param lat 纬度
     * @param lon 经度
     * @return
     */
    public static GeoPoint gcj02ToBd09(double lat, double lon) {
        double x = lon, y = lat;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * PI);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * PI);
        double bdLon = z * Math.cos(theta) + 0.0065;
        double bdLat = z * Math.sin(theta) + 0.006;
        return new GeoPoint(bdLat, bdLon);
    }

    /**
     * bd04 to gps84
     *
     * @param lat 纬度
     * @param lon 经度
     * @return
     */
    public static GeoPoint bd09ToGps84(double lat, double lon) {
        GeoPoint gcj = bd09ToGcj02(lat, lon);
        return gcj02ToGps84(gcj.getLat(), gcj.getLon());
    }

    /**
     * bd09 to gcj02
     *
     * @param lat 纬度
     * @param lon 经度
     * @return
     */
    public static GeoPoint bd09ToGcj02(double lat, double lon) {
        double x = lon - 0.0065, y = lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * Math.PI);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * Math.PI);
        double tempLng = z * Math.cos(theta);
        double tempLat = z * Math.sin(theta);
        return new GeoPoint(tempLat, tempLng);
    }

    /**
     * 计算两点之间距离
     *
     * @param lng1
     * @param lat1
     * @param lng2
     * @param lat2
     * @return
     */
    public static long getDistance(double lng1, double lat1, double lng2, double lat2) {
        double RAD = Math.PI / 180.0;
        double radLat1 = lat1 * RAD;
        double radLat2 = lat2 * RAD;
        double a = radLat1 - radLat2;
        double b = (lng1 - lng2) * RAD;
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
                Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * 6378137;
        return Math.round(s * 10000) / 10000;
    }


    public static double[] transform(double lat, double lon) {
        double dLat = transformLat(lon - 105.0, lat - 35.0);
        double dLon = transformLon(lon - 105.0, lat - 35.0);
        double radLat = lat / 180.0 * PI;
        double magic = Math.sin(radLat);
        magic = 1 - EE * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((A * (1 - EE)) / (magic * sqrtMagic) * PI);
        dLon = (dLon * 180.0) / (A / sqrtMagic * Math.cos(radLat) * PI);
        double mgLat = lat + dLat;
        double mgLon = lon + dLon;
        return new double[]{mgLat, mgLon};
    }

    public static double transformLat(double x, double y) {
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y
                + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * PI) + 20.0 * Math.sin(2.0 * x * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * PI) + 40.0 * Math.sin(y / 3.0 * PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * PI) + 320 * Math.sin(y * PI / 30.0)) * 2.0 / 3.0;
        return ret;
    }


    public static double transformLon(double x, double y) {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1
                * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * PI) + 20.0 * Math.sin(2.0 * x * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * PI) + 40.0 * Math.sin(x / 3.0 * PI)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * PI) + 300.0 * Math.sin(x / 30.0
                * PI)) * 2.0 / 3.0;
        return ret;
    }

    /**
     * is or not outOfChina
     *
     * @param lat
     * @param lon
     * @return
     */
    public static boolean outOfChina(double lat, double lon) {
        if (lon < 72.004 || lon > 137.8347) {
            return true;
        }
        if (lat < 0.8293 || lat > 55.8271) {
            return true;
        }
        return false;
    }


    public static class GeoPoint {
        private double lat;
        private double lon;

        public GeoPoint(double lat, double lon) {
            this.lat = lat;
            this.lon = lon;
        }

        public double getLat() {
            return lat;
        }

        public double getLon() {
            return lon;
        }
    }

}
