package top.littlerich.virtuallocation.listener;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;

import static top.littlerich.virtuallocation.activity.MainActivity.setCurrentMapLatLng;

/**
 * 定位结果回调
 * API: http://lbsyun.baidu.com/index.php?title=android-locsdk/guide/getloc
 * Created by xuqingfu on 2017/5/3.
 */

public class AsyncLocationResultListener implements BDLocationListener {

    private MapView mMapView;
    boolean isFirstLoc;// 是否首次定位
    //private double myGpslatitude, myGpslongitude;

    public AsyncLocationResultListener(MapView mapView, boolean isFirstLoc) {
        mMapView = mapView;
        this.isFirstLoc = isFirstLoc;
        //this.myGpslatitude = myGpslatitude;
        //  this.myGpslongitude = myGpslongitude;
    }

    @Override
    public void onReceiveLocation(BDLocation location) {
        // map view 销毁后不在处理新接收的位置
        if (location == null || mMapView == null) {
            return;
        }

        LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
        setCurrentMapLatLng(ll);
    }
}
