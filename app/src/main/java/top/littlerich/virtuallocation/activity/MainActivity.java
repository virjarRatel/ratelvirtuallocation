package top.littlerich.virtuallocation.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.lljjcoder.style.citylist.CityListSelectActivity;
import com.lljjcoder.style.citylist.bean.CityInfoBean;
import com.virjar.ratel.virtuallocation.R;

import java.util.ArrayList;

import top.littlerich.virtuallocation.adapter.PoiAdapter;
import top.littlerich.virtuallocation.base.BaseActivity;
import top.littlerich.virtuallocation.common.AppApplication;
import top.littlerich.virtuallocation.listener.AsyncLocationResultListener;
import top.littlerich.virtuallocation.listener.GeoCoderListener;
import top.littlerich.virtuallocation.listener.MapClickListener;
import top.littlerich.virtuallocation.listener.MarkerDragListener;
import top.littlerich.virtuallocation.model.Gps;
import top.littlerich.virtuallocation.util.PermissionUtil;
import top.littlerich.virtuallocation.view.TopBanner;


/**
 * Created by xuqingfu on 2017/4/15.
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {

    private String mMockProviderName = LocationManager.GPS_PROVIDER;
    private Button bt_Ok;
    private LocationManager locationManager;
    public static double latitude = 25.2358842413, longitude = 119.2035484314;

    private Thread thread;// ??????????????????????????????
    private Boolean RUN = true;
    private TextView tv_location;
    boolean isFirstLoc = true;// ??????????????????
    // ??????????????????
    private static LocationClient mLocClient;
    private MyLocationConfiguration.LocationMode mCurrentMode;// ????????????
    private BitmapDescriptor mCurrentMarker;// ????????????
    private MapView mMapView;
    private static BaiduMap mBaiduMap;
    // ??????????????? bitmap ???????????????????????? recycle
    private BitmapDescriptor bd = null;//= BitmapDescriptorFactory.fromResource(R.mipmap.icon_gcoding);
    private static Marker mMarker;
    private static LatLng curLatlng;
    private static GeoCoder mSearch;
    DrawerLayout mDrawerLayout;
    TopBanner mTopbanner;
    private TextView mAboutAuthor;
    private ImageView mCurrentLocation;
    private Animation mOperatingAnim;
    private TextView mPreciseLocation;
    private TextView mAddProcess;
    private ImageView mStopMock;

    private LinearLayout poiSearchContainer;
    private ImageView poiSearchCancelBtn;
    private EditText poiSearchEditText;
    private static PoiSearch mPoiSearch;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private PoiAdapter mPoiAdapter;


    @Override
    protected Object getContentViewId() {
        return R.layout.layout_schema;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PermissionUtil.requestPermissions(this,
                new String[]{
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION},
                99);

        if (PermissionUtil.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            bd = BitmapDescriptorFactory.fromResource(R.mipmap.icon_gcoding);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (PermissionUtil.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // ????????? SDK ???????????????????????? context ??????????????? ApplicationContext
            SDKInitializer.initialize(getApplicationContext());
            SDKInitializer.setCoordType(CoordType.GCJ02);
            bd = BitmapDescriptorFactory.fromResource(R.mipmap.icon_gcoding);
            initOverlay();
        } else {
            Toast.makeText(this, "???????????????????????????", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void IniView() {
        bt_Ok = findViewById(R.id.bt_Ok);
        tv_location = findViewById(R.id.tv_location);
        mDrawerLayout = findViewById(R.id.dl_left);
        mTopbanner = findViewById(R.id.topbanner);
        mAboutAuthor = findViewById(R.id.tv_about_me);
        mCurrentLocation = findViewById(R.id.iv_location);
        mStopMock = findViewById(R.id.iv_stop_location);
        mPreciseLocation = findViewById(R.id.tv_precise);
        mAddProcess = findViewById(R.id.tv_add_app);
        //??????????????????
        mOperatingAnim = AnimationUtils.loadAnimation(this, R.anim.spinloaing);
        LinearInterpolator lin = new LinearInterpolator();

        mOperatingAnim.setInterpolator(lin);
        // ???????????????
        mMapView = findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        // ??????????????????
        mBaiduMap.setMyLocationEnabled(true);

        //?????????????????????
        mMapView.showScaleControl(false);
        //????????????????????????
        mMapView.showZoomControls(false);
        mMapView.removeViewAt(1);
        // ???????????????
        mLocClient = new LocationClient(this);

        poiSearchContainer = findViewById(R.id.poi_search);
        poiSearchEditText = findViewById(R.id.poi_search_edit);
        //poiSearchCancelImage = findViewById(R.id.poi_search_img_cancel);
        //?????????????????????LayoutManager
        mRecyclerView = findViewById(R.id.poi_search_recycler_view);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        //????????????????????????item????????????????????????????????????????????????????????????
        mRecyclerView.setHasFixedSize(true);
        //???????????????Adapter
        mPoiAdapter = new PoiAdapter(new PoiAdapter.PoiClickListener() {

            @Override
            public void onPoiClick(PoiInfo poiInfo) {
                poiSearchContainer.setVisibility(View.GONE);
                setCurrentMapLatLng(poiInfo.location);
            }
        });
        mRecyclerView.setAdapter(mPoiAdapter);


        poiSearchContainer = findViewById(R.id.poi_search);
        poiSearchEditText = findViewById(R.id.poi_search_edit);
        poiSearchCancelBtn = findViewById(R.id.poi_search_btn_cancel);
        mRecyclerView = findViewById(R.id.poi_search_recycler_view);


    }

    @Override
    protected void IniLister() {
        bt_Ok.setOnClickListener(this);
        mLocClient.registerLocationListener(new AsyncLocationResultListener(mMapView, isFirstLoc));
        mBaiduMap.setOnMapClickListener(new MapClickListener(bt_Ok));
        mBaiduMap.setOnMarkerDragListener(new MarkerDragListener());

        // ??????????????????????????????????????????
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(new GeoCoderListener(MainActivity.this, tv_location));
        mTopbanner.setTopBannerListener(new TopBanner.OnTopBannerListener() {
            @Override
            public void leftClick(View v) {
                if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
                    mDrawerLayout.closeDrawer(Gravity.START);
                } else {
                    mDrawerLayout.openDrawer(Gravity.START);
                }
            }

            @Override
            public void rightClick(View v) {
                //mSearch.geocode()
//                mSearch.geocode()
                poiSearchContainer.setVisibility(View.VISIBLE);
                poiSearchEditText.requestFocus();
            }
        });
        mAboutAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutActivity.openActivity(MainActivity.this);
            }
        });
        mCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLocClient.start();
            }
        });
        mBaiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                new Handler().postDelayed(new Runnable() {

                    public void run() {
                        mCurrentLocation.clearAnimation();
                    }

                }, 1000 * 6);
            }
        });
        mPreciseLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PreciseLocationActivity.class);
                startActivityForResult(intent, PreciseLocationActivity.PRECISE_LOCATION_RESULT_FLAG);
            }
        });
        mAddProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppsActivity.openActivity(MainActivity.this);
            }
        });
        mStopMock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLocClient.stop();
                Gps configGPS = AppApplication.getConfigGPS();
                LatLng ll = new LatLng(configGPS.mLatitude, configGPS.mLongitude);
                setCurrentMapLatLng(ll);
                // bt_Ok.setText("????????????");
            }
        });

        View.OnClickListener cancelPoiSearch = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                poiSearchContainer.setVisibility(View.GONE);
            }
        };
        poiSearchCancelBtn.setOnClickListener(cancelPoiSearch);

        poiSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                beginSearch(editable.toString());
            }
        });

        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {

            @Override
            public void onGetPoiResult(PoiResult poiResult) {
                if (poiResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    Toast.makeText(MainActivity.this, "?????????:" + poiResult.error, Toast.LENGTH_SHORT).show();
                    return;
                }
                mPoiAdapter.setData(poiResult.getAllPoi());
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

            }

            @Override
            public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {

            }

            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

            }
        });


    }


    @Override
    protected void IniData() {
        iniMap();
        if (mOperatingAnim != null) {
            mCurrentLocation.startAnimation(mOperatingAnim);
        }

    }


    /**
     * iniMap ???????????????
     */
    private void iniMap() {
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// ??????gps
        option.setCoorType("gcj02"); // ??????????????????
        option.setScanSpan(3000);
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;

        // ??????
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(14.0f);
        mBaiduMap.setMapStatus(msu);

        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker));
        mLocClient.setLocOption(option);
        if (PermissionUtil.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            initOverlay();
        }

    }

    /**
     * initOverlay ????????????????????????????????????????????????
     */
    private void initOverlay() {
        Gps configGPS = AppApplication.getConfigGPS();
        final LatLng ll = new LatLng(configGPS.mLatitude, configGPS.mLongitude);
        OverlayOptions oo = new MarkerOptions().position(ll).icon(bd).zIndex(9)
                .draggable(true);
        mMarker = (Marker) (mBaiduMap.addOverlay(oo));

        new Handler(Looper.getMainLooper())
                .postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // search???????????????????????????????????????????????????POI????????????????????????
                        setCurrentMapLatLng(ll);
                    }
                }, 1500);
    }

    /**
     * setCurrentMapLatLng ??????????????????
     */
    public static void setCurrentMapLatLng(LatLng arg0) {
        // Debug.waitForDebugger();
        curLatlng = arg0;
        mMarker.setPosition(arg0);

        if (mLocClient.isStarted()) {
            mLocClient.stop();
        }

        // ????????????????????????????????????
        LatLng ll = new LatLng(arg0.latitude, arg0.longitude);
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
        mBaiduMap.animateMapStatus(u);

        // ????????????????????? ?????????????????????????????????onGetReverseGeoCodeResult???????????????
        mSearch.reverseGeoCode(
                new ReverseGeoCodeOption()
                        .newVersion(0)
                        .pageNum(0)
                        .location(arg0)
                        .radius(150)
        );
    }


    @Override
    protected void thisFinish() {
        AlertDialog.Builder build = new AlertDialog.Builder(this);
        build.setTitle("??????");
        build.setMessage("????????????????????????????????????????????????????????????");
        build.setPositiveButton("??????", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        build.setNeutralButton("?????????", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                moveTaskToBack(true);
            }
        });
        build.setNegativeButton("??????", null);
        build.show();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        }
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        thisFinish();
    }

    @Override
    protected void onDestroy() {
        RUN = false;
        thread = null;
        //????????????????????????
        mCurrentLocation.clearAnimation();

        // ?????????????????????
        mLocClient.stop();
        // ??????????????????
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        if (bd != null) {
            bd.recycle();
        }
        mSearch.destroy();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_Ok:
                latitude = curLatlng.latitude;
                longitude = curLatlng.longitude;
                try {
                    AppApplication.saveConfigGPS(longitude, latitude);
                    bt_Ok.setText("????????????");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CityListSelectActivity.CITY_SELECT_RESULT_FRAG) {
            if (resultCode == RESULT_OK) {
                if (data == null) {
                    return;
                }
                Bundle bundle = data.getExtras();

                CityInfoBean cityInfoBean = (CityInfoBean) bundle.getParcelable("cityinfo");

                if (null == cityInfoBean) {
                    return;
                }
                beginSearch(poiSearchEditText.getText().toString());
            }
        } else if (requestCode == PreciseLocationActivity.PRECISE_LOCATION_RESULT_FLAG) {
            if (data == null) {
                return;
            }
            Bundle bundle = data.getExtras();
            if (resultCode == RESULT_OK) {
                try {
                    double lat = bundle.getDouble("lat");
                    double lng = bundle.getDouble("lng");
                    LatLng latLng = new LatLng(lat, lng);
                    setCurrentMapLatLng(latLng);
                } catch (Exception e) {
                    //ignore
                }
            }
        }
    }

    private void beginSearch(String keyword) {
        if (TextUtils.isEmpty(keyword)) {
            mPoiAdapter.setData(new ArrayList<PoiInfo>());
            return;
        }
        Log.i(AppApplication.tag, "beginSearch: " + keyword);
        mPoiSearch.searchInCity(new PoiCitySearchOption()
                .cityLimit(false)
                .city("??????")
                .keyword(keyword)
        );
    }
}