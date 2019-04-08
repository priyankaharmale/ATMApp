package com.hnweb.atmap.fragment;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hnweb.atmap.R;
import com.hnweb.atmap.utils.LocationSet;
import com.hnweb.atmap.utils.MyLocationListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.LOCATION_SERVICE;


/**
 * Created by PC-21 on 07-May-18.
 */

public class MapFragment extends Fragment implements LocationListener, GoogleMap.OnMarkerClickListener, OnMapReadyCallback {
    LocationSet locationSet;
    public static final int MIN_TIME_BW_UPDATES = 10; // 10 meters;
    public static final int MIN_DISTANCE_CHANGE_FOR_UPDATES = 1000 * 60 * 1; // 1 minute;
    private static final String TAG = "MapsActivity";
    Boolean flag = false;
    List<Marker> markers;
    android.location.Location location;
    boolean isNetworkEnabled = false;
    boolean isGPSEnabled = false;
    Address returnedAddress;
    Double latitude;
    Double longitude;
    StringBuilder strReturnedAddress;
    MyLocationListener myLocationListener;
    GoogleMap googleMap;
    private static final int PERMISSION_REQUEST_CODE_LOCATION = 1;
    View rootView;
    SupportMapFragment mapFrag;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fargment_home, container, false);

        myLocationListener = new MyLocationListener(getActivity());
        locationSet = new LocationSet();
        if (locationSet.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, getActivity(), getActivity())) {
            fetchLocationData();
        } else {
            locationSet.requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, PERMISSION_REQUEST_CODE_LOCATION, getActivity(), getActivity());
        }

        MapsInitializer.initialize(getActivity());
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if (status != ConnectionResult.SUCCESS) {
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, getActivity(), requestCode);
            dialog.show();
        } else {
            mapFrag = (SupportMapFragment ) getChildFragmentManager().findFragmentById(R.id.map);
            mapFrag.getMapAsync(this);

            mapFrag.getView().setVisibility(View.VISIBLE);
            System.out.println("Map Initialise");

            System.out.println("SearchMap Initialise");

            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            }

            try {
                LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
                isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if (!isGPSEnabled && !isNetworkEnabled) {
                    // no network provider is enabled
                } else {
                    if (isNetworkEnabled) {
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("Network", "Network");
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            System.out.println("NEWRTWORK lOADED");
                            if (location != null) {
                                //googleMap.clear();
                                //onLocationChanged(location);
                               // getList();


                            }
                        }
                    }
                    if (isGPSEnabled) {
                        if (location == null) {
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                            Log.d("GPS Enabled", "GPS Enabled");
                            if (locationManager != null) {
                                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if (location != null) {
                                    //googleMap.clear();
                                    //onLocationChanged(location);
                                    //getList();


                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        return rootView;
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
            // Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLocationData();
                } else {
                    Toast.makeText(getActivity(), "Permission Denied, You cannot access location data.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }


    @Override
    public void onLocationChanged(android.location.Location location) {

        LatLng latLng = null;
        try {
            googleMap.clear();
        }catch (Exception e){
            Log.e("Exception",e.toString());

        }
        if (googleMap != null) {
            latLng = new LatLng(25.761681, -80.191788);
            MarkerOptions markerOptions = new MarkerOptions();
            googleMap.addMarker(markerOptions.title("Current Location")
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_icon))
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_RED))
            );

        }
        try {
            try {
                Geocoder geocoder = new Geocoder(getActivity(), Locale.ENGLISH);
                List<Address> addresses = geocoder.getFromLocation(25.761681, -80.191788, 1);
                if (addresses != null) {
                    returnedAddress = addresses.get(0);
                    strReturnedAddress = new StringBuilder("Address:\n");
                    for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                        strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                    }
                    // Toast.makeText(getApplicationContext(),"" +strReturnedAddress.toString(),Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "No Address returned!", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                Log.e("IOException",e.toString());
                e.printStackTrace();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(16));
        // Toast.makeText(MapsActivity.this, "  " + latitude + "  " + longitude, Toast.LENGTH_SHORT).show();

    }


    //*************************************** Marker Click Event Handling *********************************************************
    @Override
    public boolean onMarkerClick(final Marker marker) {
        marker.showInfoWindow();
        // latLng1 = marker.getPosition();
        //  googleMap.getUiSettings().setMapToolbarEnabled(false);
        //  googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        //    googleMap.getUiSettings().setCompassEnabled(true);
        // Toast.makeText(MapsActivity.this,"Hiii",Toast.LENGTH_SHORT).show();
        System.out.println("markers.size" + markers.size());

        if (flag == false) {


        } else {
            Log.i("Marker", "Hidden");

        }
        getActivity().findViewById(R.id.ic_navigation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://ditu.google.cn/maps?f=d&source=s_d" +
                                "&saddr=" + location.getLatitude() + " " + location.getLongitude() + "&daddr=" + marker.getPosition().latitude + " " + marker.getPosition().longitude + "&hl=zh&t=m&dirflg=d"));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(intent);


            }
        });
        return true;
    }




   @Override
    public void onMapReady(GoogleMap googleMap1) {
        googleMap = googleMap1;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMarkerClickListener(this);
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setAllGesturesEnabled(true);

        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);


    }
}
