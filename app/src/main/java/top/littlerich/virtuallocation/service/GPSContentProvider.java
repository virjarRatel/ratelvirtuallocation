package top.littlerich.virtuallocation.service;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import top.littlerich.virtuallocation.common.AppApplication;
import top.littlerich.virtuallocation.model.Gps;

public class GPSContentProvider extends ContentProvider {
    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Nullable
    @Override
    public Bundle call(@NonNull String method, @Nullable String arg, @Nullable Bundle extras) {
        //return super.call(method, arg, extras);
        // method == invoke
        // arg = context.getPackageName() == com.Qunar
        // extras = null
        boolean appMockStatus = AppApplication.getAppMockStatus(arg);
        Bundle bundle = new Bundle();
        if (!appMockStatus) {
            return bundle;
        }
        Gps configGPS = AppApplication.getConfigGPS();
        bundle.putDouble("lat", configGPS.mLatitude);
        bundle.putDouble("lng", configGPS.mLongitude);
        bundle.putString("address", configGPS.address);
        return bundle;
    }
}
