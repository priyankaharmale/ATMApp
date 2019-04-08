package com.hnweb.atmap.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.hnweb.atmap.R;
import com.hnweb.atmap.contants.AppConstant;
import com.hnweb.atmap.utils.ConnectionDetector;
import com.hnweb.atmap.utils.LocationSet;
import com.hnweb.atmap.utils.MyLocationListener;
import com.hnweb.atmap.utils.PermissionUtility;
import com.hnweb.atmap.utils.PostDataTask;
import com.hnweb.atmap.utils.SharedPrefManager;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;

import static com.hnweb.atmap.utils.MainApplication.getContext;


/**
 * Created by Priyanka H on 06/03/2019.
 */
public class MainActivity extends AppCompatActivity {
    Button btn_proced;

    String useridUser, userType;
    ConnectionDetector connectionDetector;
    private PermissionUtility putility;
    ArrayList<String> permission_list;
    public static final MediaType FORM_DATA_TYPE = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    MyLocationListener myLocationListener;
    double latitude = 0.0d;
    double longitude = 0.0d;
    SharedPreferences pref;
    SharedPreferences.Editor editorUser;

    LocationSet locationSet = new LocationSet();
    //URL derived from form URL
    public static final String URL = "https://docs.google.com/forms/d/e/1FAIpQLSe0BCEcMTVr0dpJv1KzVxLHgl8I3Xy_jK5rsoPv00zAk9Yw0g/formResponse";

    //input element ids found from the live form page
    public static final String EMAIL_KEY = "entry.2045738017";
    private static final int PERMISSION_REQUEST_CODE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

        pref = getApplicationContext().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);
        editorUser = pref.edit();
        useridUser = pref.getString(AppConstant.KEY_ID, null);
        userType = pref.getString(AppConstant.KEY_USERTYPE, null);

        connectionDetector = new ConnectionDetector(MainActivity.this);
        PostDataTask postDataTask = new PostDataTask();
        postDataTask.execute(URL, deviceInfo());
        runTimePermission();
        myLocationListener = new MyLocationListener(this);
        if (locationSet.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, this, this)) {
            fetchLocationData();
        } else {
            locationSet.requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, PERMISSION_REQUEST_CODE_LOCATION, getContext(), this);
        }

        getdeviceToken();
        btn_proced = ( Button ) findViewById(R.id.btn_proced);
        btn_proced.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (connectionDetector.isConnectingToInternet()) {
                    if (useridUser == null || useridUser.equals("")) {
                        Intent intent = new Intent(MainActivity.this, ChooseUserActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();

                    } else {

                        if (userType.equalsIgnoreCase("1")) {
                            Intent intentLogin = new Intent(MainActivity.this, HomeActivity.class);
                            intentLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intentLogin);
                            finish();

                        } else {
                            Intent intentLogin = new Intent(MainActivity.this, AgentHomeActivity.class);
                            intentLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intentLogin);
                            finish();

                        }

                    }

                    // }
                } else {
                    Toast.makeText(MainActivity.this, "No Internet Connection, Please try Again!!", Toast.LENGTH_SHORT).show();
                }


            }
        });


    }

    private void runTimePermission() {

        putility = new PermissionUtility(this);
        permission_list = new ArrayList<String>();
        permission_list.add(Manifest.permission.INTERNET);
        permission_list.add(Manifest.permission.ACCESS_NETWORK_STATE);
        permission_list.add(Manifest.permission.WAKE_LOCK);
        permission_list.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permission_list.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permission_list.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permission_list.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permission_list.add(Manifest.permission.CAMERA);
        putility.setListner(new PermissionUtility.OnPermissionCallback() {
            @Override
            public void OnComplete(boolean is_granted) {
                Log.i("OnPermissionCallback", "is_granted = " + is_granted);
                if (is_granted) {

                } else {
                    putility.checkPermission(permission_list);
                }
            }
        });
        putility.checkPermission(permission_list);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (putility != null) {
            putility.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private void getdeviceToken() {
        try {
            String t = SharedPrefManager.getInstance(this).getDeviceToken();

            if (t.equals("") || t == null || t.equals("null")) {
                Log.d("Tokan", "t-NULL");
            } else {
                Log.d("Tokan", t);
            }
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }


    public String deviceInfo() {

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = pInfo.versionName;

        String s = "Debug-infos:";
        s += "\n OS Version: " + System.getProperty("os.version") + "(" + android.os.Build.VERSION.INCREMENTAL + ")";
        s += "\n OS API Level: " + android.os.Build.VERSION.SDK_INT;
        s += "\n Device: " + android.os.Build.DEVICE;
        s += "\n Model (and Product): " + android.os.Build.MODEL + " (" + android.os.Build.PRODUCT + ")";

        s += "\n RELEASE: " + android.os.Build.VERSION.RELEASE;
        s += "\n BRAND: " + android.os.Build.BRAND;
        s += "\n DISPLAY: " + android.os.Build.DISPLAY;
        s += "\n CPU_ABI: " + android.os.Build.CPU_ABI;
        s += "\n CPU_ABI2: " + android.os.Build.CPU_ABI2;
        s += "\n UNKNOWN: " + android.os.Build.UNKNOWN;
        s += "\n HARDWARE: " + android.os.Build.HARDWARE;
        s += "\n Build ID: " + android.os.Build.ID;
        s += "\n MANUFACTURER: " + android.os.Build.MANUFACTURER;
        s += "\n SERIAL: " + android.os.Build.SERIAL;
        s += "\n USER: " + android.os.Build.USER;
        s += "\n HOST: " + android.os.Build.HOST;
        s += "\n APK Version: " + version;

        return s;
    }

    private void fetchLocationData() {
        // check if myLocationListener enabled
        if (myLocationListener.canGetLocation()) {
            latitude = myLocationListener.getLatitude();
            longitude = myLocationListener.getLongitude();

            //  Toast.makeText(getActivity(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        } else {
            myLocationListener.showSettingsAlert();
            latitude = myLocationListener.getLatitude();
            longitude = myLocationListener.getLongitude();

            // Toast.makeText(getActivity(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();

        }

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        StringBuilder result = new StringBuilder();

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                result.append(address.getAddressLine(1)).append(" ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
