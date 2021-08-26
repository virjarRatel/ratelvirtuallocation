package top.littlerich.virtuallocation.common;

import android.Manifest;
import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.lljjcoder.style.citylist.utils.CityListLoader;

import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import top.littlerich.virtuallocation.model.Gps;
import top.littlerich.virtuallocation.service.LocationService;
import top.littlerich.virtuallocation.util.FileUtils;
import top.littlerich.virtuallocation.util.PermissionUtil;

/**
 * Created by xuqingfu on 2017/4/15.
 */
public class AppApplication extends Application {
    public static final String tag = "RVLocation";
    public LocationService locationService;
    public Vibrator mVibrator;

    private static AppApplication appApplication;

    public static Gps mockGPS = null;

    @Override
    public void onCreate() {
        super.onCreate();
        appApplication = this;
        /***
         * 初始化定位sdk，建议在Application中创建
         */
        locationService = new LocationService(getApplicationContext());
        mVibrator = (Vibrator) getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);

        if (PermissionUtil.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
            SDKInitializer.initialize(this);
            SDKInitializer.setCoordType(CoordType.GCJ02);
        }

        /**
         * 预先加载一级列表显示 全国所有城市市的数据
         */
        CityListLoader.getInstance().loadCityData(this);

    }

    public static void setAppMock(String pkg, boolean isChecked) {
        File configFile = new File(appApplication.getFilesDir(), pkg + "_status.conf");
        if (isChecked) {
            try {
                FileWriter fileWriter = new FileWriter(configFile);
                fileWriter.write("mock");
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (configFile.exists()) {
            configFile.delete();
        }
    }

    public static boolean getAppMockStatus(String pkg) {
        File configFile = new File(appApplication.getFilesDir(), pkg + "_status.conf");
        return configFile.exists();
    }


    public static Gps getConfigGPS() {
        File configFile = new File(appApplication.getFilesDir(), "mockGPS.json");
        if (!configFile.exists()) {
            return Config.COMPANY;
        }

        try {
            String config = FileUtils.readLine(configFile);
            JSONObject jsonObject = new JSONObject(config);
            double longitude = jsonObject.optDouble("longitude");
            double latitude = jsonObject.optDouble("latitude");
            return new Gps(latitude, longitude, getAddress());
        } catch (Exception e) {
            e.printStackTrace();
            return Config.COMPANY;
        }
    }

    public static void saveConfigGPS(double longitude, double latitude) {
        File configFile = new File(appApplication.getFilesDir(), "mockGPS.json");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("longitude", longitude);
            jsonObject.put("latitude", latitude);
            FileUtils.writeLine(configFile, jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void storeAddress(String address) {
        File configFile = new File(appApplication.getFilesDir(), "mockAddress.txt");
        try {
            FileUtils.writeLine(configFile, address);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getAddress() {
        File configFile = new File(appApplication.getFilesDir(), "mockAddress.txt");
        if (!configFile.exists()) {
            return "";
        }
        try {
            return FileUtils.readLine(configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void queryMockGPS(Context context) {
        try {
            Bundle bundle = context.getContentResolver()
                    .call(Uri.parse("content://com.virjar.ratel.virtuallocation"),
                            "invoke", context.getPackageName(), null);
            if (bundle == null) {
                return;
            }
            double lat = bundle.getDouble("lat", -1);
            double lng = bundle.getDouble("lng", -1);
            String address = bundle.getString("address");
            Log.i(AppApplication.tag, "queryMockGPS: " + lng + "," + lat + " " + address);
            if (lat == -1 && lng == -1) {
                return;
            }
            Random random = new Random();
            lat += (random.nextDouble() - 0.5) * 0.0012;
            lng += (random.nextDouble() - 0.5) * 0.001;
            mockGPS = new Gps(lat, lng, address);
        } catch (Exception e) {
            Log.e(AppApplication.tag, "error for queryMockGPS", e);
        }

//        mockGPS = AppApplication.getConfigGPS();
    }

}