package com.keep.alive;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class LocationUtils {
    private static final String TAG = "LocationUtils";
    private LocationManager locationManager;
    static LocationUtils locationUtils;

    public static LocationUtils getInstance() {
        if (locationUtils == null) {
            locationUtils = new LocationUtils();
        }
        return locationUtils;
    }


    public double[] getLocations(Context context) {
        String strLocation = "0,0";
        DecimalFormat df = new DecimalFormat("#####0.0000");
        if (!checkPermission(context, permission.ACCESS_COARSE_LOCATION)) {
            Toast.makeText(context, "定位权限关闭，无法获取地理位置", Toast.LENGTH_SHORT).show();
        }
        try {
            //获取系统的服务，
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            List<String> providers = locationManager.getProviders(true);
            Log.e(TAG, "" + providers.size());
            String provider;
            if (providers.contains(LocationManager.GPS_PROVIDER)) {
                //如果是GPS
                provider = LocationManager.GPS_PROVIDER;
            } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
                //如果是Network
                provider = LocationManager.NETWORK_PROVIDER;
            } else {
                Toast.makeText(context, "没有可用的位置提供器", Toast.LENGTH_SHORT).show();
                return null;
            }

            Log.i("Tobin", "Location Provider is " + provider);
            Location location = locationManager.getLastKnownLocation(provider);

            /**
             * 重要函数，监听数据测试
             * 位置提供器、监听位置变化的时间间隔（毫秒），监听位置变化的距离间隔（米），LocationListener监听器
             */
//           locationManager.requestLocationUpdates(provider, 0, 0, locationListener);
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    lm.removeUpdates(locationListener);
//                }
//            },2000);

            //第一次获得设备的位置

            if (location != null) {
                return new double[]{location.getLatitude(),location.getLongitude()};
                // 耗时操作
//                strLocation += " " + convertAddress(context, location.getLatitude(),location.getLongitude());

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    /**
     * LocationListern监听器
     * 参数：地理位置提供器、监听位置变化的时间间隔、位置变化的距离间隔、LocationListener监听器
     */
    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {

        }

        public void onProviderDisabled(String provider) {
            Log.i("Tobin", "Provider now is disabled..");
        }

        public void onProviderEnabled(String provider) {
            Log.i("Tobin", "Provider now is enabled..");
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };


    /**
     * @param latitude  经度
     * @param longitude 纬度
     * @return 详细位置信息 GeoCoder是基于后台backend的服务，因此这个方法不是对每台设备都适用。
     */
    public String convertAddress(Context context, double latitude, double longitude) {
        Geocoder mGeocoder = new Geocoder(context, Locale.getDefault());
        StringBuilder mStringBuilder = new StringBuilder();

        try {
            List<Address> mAddresses = mGeocoder.getFromLocation(latitude, longitude, 1);
            if (!mAddresses.isEmpty()) {
                Address address = mAddresses.get(0);
                mStringBuilder.append(address.getCountryName()).append(", ").append(address.getAdminArea()).append(", ").append(address.getLocality());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return mStringBuilder.toString();
    }

    private boolean checkPermission(Context context, permission permName) {
        int perm = context.checkCallingOrSelfPermission("android.permission." + permName.toString());
        return perm == PackageManager.PERMISSION_GRANTED;
    }

    private enum permission {
        ACCESS_COARSE_LOCATION,
        ACCESS_FINE_LOCATION
    }
}
