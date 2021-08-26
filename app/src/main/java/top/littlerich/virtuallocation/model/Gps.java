package top.littlerich.virtuallocation.model;

/**
 * Created by xuqingfu on 2017/5/26.
 */

public class Gps {
    public double mLatitude;
    public double mLongitude;
    public String address;

    public Gps(double latitude, double longitude) {
        mLatitude = latitude;
        mLongitude = longitude;
    }

    public Gps(double latitude, double longitude, String address) {
        this(latitude, longitude);
        this.address = address;
    }


}
