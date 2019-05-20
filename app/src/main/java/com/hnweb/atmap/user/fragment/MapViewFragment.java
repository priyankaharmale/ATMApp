package com.hnweb.atmap.user.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hnweb.atmap.R;
import com.hnweb.atmap.user.activity.AgentDetailsActivity;
import com.hnweb.atmap.user.bo.Agent;
import com.hnweb.atmap.contants.AppConstant;
import com.hnweb.atmap.utils.ConnectionDetector;
import com.hnweb.atmap.utils.LoadingDialog;
import com.hnweb.atmap.utils.LocationSet;
import com.hnweb.atmap.utils.MapWrapperLayout;
import com.hnweb.atmap.utils.MyLocationListener;
import com.hnweb.atmap.utils.OnInfoWindowElemTouchListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by PC-21 on 03-Apr-18.
 */

@SuppressLint("ValidFragment")
public class MapViewFragment extends Fragment implements OnMarkerClickListener, OnMapReadyCallback {

    MapView mMapView;
    private GoogleMap googleMap;
    private SharedPreferences prefs;
    String user_id;
    Boolean flag = false;
    ConnectionDetector connectionDetector;
    LoadingDialog loadingDialog;
    ArrayList<Agent> agentArrayList;
    private Circle mCircle;
    private Marker mMarker;
    protected Context context;
    MyLocationListener myLocationListener;
    private static final int PERMISSION_REQUEST_CODE_LOCATION = 1;
    double latitude = 0.0d;
    double longitude = 0.0d;
    String getCurrentLocation = "0";
    String strAdd = "";
    FragmentManager fragmentManager;
    List<Marker> markers;
    private OnInfoWindowElemTouchListener infoButtonListener;
    Agent infoWindowData;
    private ViewGroup infoWindow;
    private TextView infoTitle;
    private TextView infoSnippet;
    private Button infoButton1, infoButton2;
    MapWrapperLayout mapWrapperLayout;
    private ImageView iv_cancle;
    LatLng latLng;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fargment_home, container, false);
        prefs = getActivity().getApplicationContext().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);
        user_id = prefs.getString(AppConstant.KEY_ID, null);
        myLocationListener = new MyLocationListener(getActivity());
        if (LocationSet.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, getActivity(), getActivity())) {
            //fetchLocationData();
        } else {
            LocationSet.requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, PERMISSION_REQUEST_CODE_LOCATION, getContext(), getActivity());
        }
        fragmentManager = getFragmentManager();
        getCurrentLocation = "0";

        mapWrapperLayout = (MapWrapperLayout) view.findViewById(R.id.map_relative_layout);

        loadingDialog = new LoadingDialog(getActivity());

        mMapView = view.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

//        googleMap.setOnMarkerClickListener(this);
        try {
            mMapView.onResume(); // needed to get the map to display immediately
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            // Get the button view
            View locationButton = ((View) mMapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            // position on right bottom
            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            rlp.setMargins(0, 180, 180, 120);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
        initViewById(view);
        fetchLocationData();

        connectionDetector = new ConnectionDetector(getActivity());
        if (connectionDetector.isConnectingToInternet()) {
         getMapList(latitude,longitude);
        } else {
            Toast.makeText(getActivity(), "No Internet Connection, Please try Again!!", Toast.LENGTH_SHORT).show();
        }


        try {

            SupportPlaceAutocompleteFragment autocompleteFragment = (SupportPlaceAutocompleteFragment) getChildFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
          //  autocompleteFragment.setText(current_address);
            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(Place place) {

                    String value = String.valueOf(latLng);
                    if (place != null) {
                        latLng = place.getLatLng();
                        Log.d("GETLocation", latLng.latitude + " :: " + latLng.longitude);
                        Log.d("LatitudeLongitude", String.valueOf(latLng));
                        getCurrentLocation = "1";
                        getMapList(latLng.latitude, latLng.longitude);
                      /*  latitude = Double.parseDouble(mStringLatitude);
                        longitude = Double.parseDouble(mStringLongitude);

                      */  //Toast.makeText(getActivity(), mStringLatitude + "::" + "\n" + mStringLongitude, Toast.LENGTH_SHORT).show();
                    }
                }


                @Override
                public void onError(Status status) {
                    Toast.makeText(getActivity(), status.toString(), Toast.LENGTH_SHORT).show();
                    Log.d("ErrorLocation", status.toString());

                }
            });
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }


        return view;
    }


    @SuppressLint("LongLogTag")
    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {

        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Current loction address", strReturnedAddress.toString());
            } else {
                Log.w("My Current loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current loction address", "Canont get Address!");
        }
        return strAdd;
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("SETTINGS");
        alertDialog.setMessage("Enable Location Provider! Go to settings menu?");
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        getActivity().startActivity(intent);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }


    @SuppressLint("HandlerLeak")
    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            //tvAddress.setText(locationAddress);
            Log.e("ArrayList", locationAddress);
        }
    }

    private void fetchLocationData() {
        // check if myLocationListener enabled
        if (myLocationListener.canGetLocation()) {
            latitude = myLocationListener.getLatitude();
            longitude = myLocationListener.getLongitude();
            Toast.makeText(getActivity(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();


        } else {
            myLocationListener.showSettingsAlert();
            latitude = myLocationListener.getLatitude();
            longitude = myLocationListener.getLongitude();
            Toast.makeText(getActivity(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        }

        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
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


    private void initViewById(View view) {


    }


    private void  getMapList(final double latitude, final double longitude) {

        StringRequest postRequest = new StringRequest(Request.Method.POST, AppConstant.API_GET_AGENTLIST,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        if (loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }

                        Log.i("Response", "ServiceList= " + response);

                        try {
                            JSONObject j = new JSONObject(response);
                            int message_code = j.getInt("message_code");
                            String message = j.getString("message");
                            if (message_code == 1) {
                                final JSONArray jsonArrayRow = j.getJSONArray("detail");
                                loadingDialog.dismiss();
                                agentArrayList = new ArrayList<Agent>();
                                try {
                                    for (int k = 0; k < jsonArrayRow.length(); k++) {
                                        Agent agent = new Agent();
                                        JSONObject jsonObjectpostion = jsonArrayRow.getJSONObject(k);
                                        agent.setCustomer_lat(jsonObjectpostion.getString("customer_lat"));
                                        agent.setCustomer_id(jsonObjectpostion.getString("customer_id"));
                                        agent.setCustomer_long(jsonObjectpostion.getString("customer_long"));
                                        agent.setBusiness_name(jsonObjectpostion.getString("business_name"));
                                        agent.setOpen_time(jsonObjectpostion.getString("open_time"));
                                        agent.setClose_time(jsonObjectpostion.getString("close_time"));
                                        agent.setRatting(jsonObjectpostion.getString("ratting"));
                                        agent.setCustomer_profile_pic(jsonObjectpostion.getString("customer_profile_pic"));
                                        agentArrayList.add(agent);

                                    }
                                    System.out.println("jsonobk" + jsonArrayRow);
                                    System.out.println("agentArrayList size." + agentArrayList.size());

                                    googleMapView();
                                } catch (JSONException e) {
                                    System.out.println("jsonexeption" + e.toString());
                                } catch (Exception e) {

                                }
                            } else {
                                message = j.getString("message");
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                            if (loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }


                        } catch (JSONException e) {
                            System.out.println("jsonexeption" + e.toString());
                        } catch (Exception e) {

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                        VolleyLog.d("VolleyResponse", "Error: " + error.getMessage());
                        error.printStackTrace();
                    }
                }
        ) {
            @SuppressLint("LongLogTag")
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // the POST parameters:
                params.put("id", user_id);
                params.put("user_lat", String.valueOf(latitude));
                params.put("user_long", String.valueOf(longitude));


                Log.e("Params", params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        postRequest.setShouldCache(false);
        requestQueue.add(postRequest);

    }

    private void googleMapView() {


        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                mMap.clear();
                googleMap.clear();
                // mMap.setOnMarkerClickListener((OnMarkerClickListener ) getActivity());

                // For showing a move to my location button
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                googleMap.setMyLocationEnabled(true);
                // googleMap.setOnMarkerClickListener(this);
                //    googleMap.setOnMarkerClickListener(this);
                if (googleMap != null) {
                    mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

                        @Override
                        public void onMyLocationChange(Location arg0) {
                            // TODO Auto-generated method stub

                            LatLng latLng = new LatLng(arg0.getLatitude(), arg0.getLongitude());
                            googleMap.addMarker(new MarkerOptions().position(new LatLng(arg0.getLatitude(), arg0.getLongitude())).title("It's Me!"));
                            if (mCircle == null || mMarker == null) {
                                drawMarkerWithCircle(latLng);
                            } else {
                                updateMarkerWithCircle(latLng);
                            }
                        }
                    });
                }

/*
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng arg0) {
                        googleMap.clear();

                        MarkerInfoWindowAdapter markerInfoWindowAdapter = new MarkerInfoWindowAdapter(getActivity());
                        googleMap.setInfoWindowAdapter(markerInfoWindowAdapter);

                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(arg0);
                        googleMap.animateCamera(CameraUpdateFactory.newLatLng(arg0));
                        Marker marker = googleMap.addMarker(markerOptions);
                        marker.showInfoWindow();
                    }
                });*/
                markers = new ArrayList<Marker>();
                for (int i = 0; i < agentArrayList.size(); i++) {

                    if (agentArrayList.get(i).getCustomer_lat().equalsIgnoreCase("") || agentArrayList.get(i).getCustomer_lat() == null
                            || agentArrayList.get(i).getCustomer_lat().equalsIgnoreCase("null")) {

                    } else {
                        Double latitude = Double.valueOf(agentArrayList.get(i).getCustomer_lat());
                        Double longitude = Double.valueOf(agentArrayList.get(i).getCustomer_long());

                        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.location_icon);
                        LatLng lati_long_position = new LatLng(latitude, longitude);

                        Marker marker = googleMap.addMarker(new MarkerOptions()
                                .position(lati_long_position)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_icon))
                        );


                        markers.add(marker);
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(lati_long_position).zoom(100).build();
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }

                    //  googleMap.addMarker(new MarkerOptions().position(lati_long_position).title(agentArrayList.get(i).getBusiness_name())).setIcon(icon);
                    // For zooming automatically to the location of the marker

                }


            }
        });


    }


    private void updateMarkerWithCircle(LatLng latLng) {
        mCircle.setCenter(latLng);
        mMarker.setPosition(latLng);
    }

    private void drawMarkerWithCircle(LatLng latLng) {
        double radiusInMeters = 3000.0;
        int strokeColor = 0xffff0000; //red outline
        int shadeColor = 0x44ff0000; //opaque red fill

        CircleOptions circleOptions = new CircleOptions().center(latLng).radius(radiusInMeters).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(8);
        mCircle = googleMap.addCircle(circleOptions);

        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        mMarker = googleMap.addMarker(markerOptions);
    }

    private void changeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_layout, fragment).addToBackStack(null).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       /* if (id == R.id.action_settings) {
            return true;
        }*/

        /*if (id == R.id.action_list) {
            liveitemList.setVisible(false);
            liveitemMap.setVisible(true);
            linearLayoutMap.setVisibility(View.GONE);
            linearLayoutList.setVisibility(View.VISIBLE);

            return true;
        }*/


        return super.

                onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        /*FragmentManager fm = getActivity().getSupportFragmentManager();
        Fragment fragment = (fm.findFragmentById(R.id.mapView));
        FragmentTransaction ft = fm.beginTransaction();
        ft.remove(fragment);
        ft.commit();*/

       /* PlaceAutocompleteFragment fragmentPlace = (PlaceAutocompleteFragment) getActivity().getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        if (fragmentPlace != null)
            getActivity().getFragmentManager().beginTransaction().remove(fragmentPlace).commit();*/
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
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


        mapWrapperLayout.init(googleMap, getPixelsFromDp(getActivity(), 39 + 20));
        // We want to reuse the info window for all the markers,
        // so let's create only one class member instance
        this.infoWindow = (ViewGroup) getLayoutInflater().inflate(R.layout.snippet_map, null);

        this.infoTitle = (TextView) infoWindow.findViewById(R.id.tv_hotelNamefull);
        final TextView tv_churchname = infoWindow.findViewById(R.id.tv_hotelname);
        final TextView tv_opentime = infoWindow.findViewById(R.id.tv_opentime);
        final ImageView iv_profile = infoWindow.findViewById(R.id.iv_profile);
        final TextView tv_hotelNamefull = infoWindow.findViewById(R.id.tv_hotelNamefull);
        this.iv_cancle = infoWindow.findViewById(R.id.iv_cancle);
        final TextView tv_closetime = infoWindow.findViewById(R.id.tv_closetime);
        final RatingBar ratingBar = infoWindow.findViewById(R.id.ratingBar);
        //    this.infoSnippet = (TextView)infoWindow.findViewById(R.id.addressTxt);
        this.infoButton1 = (Button) infoWindow.findViewById(R.id.btn_getdirection);
        this.infoButton2 = (Button) infoWindow.findViewById(R.id.btn_widraw);

        // Setting custom OnTouchListener which deals with the pressed state
        // so it shows up
        this.iv_cancle.setOnTouchListener(infoButtonListener);

        this.infoButtonListener = new OnInfoWindowElemTouchListener(iv_cancle, getResources().getDrawable(R.drawable.cancel_icon), getResources().getDrawable(R.drawable.cancel_icon)) {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                // Here we can perform some action triggered after clicking the button
                infoWindow.setVisibility(View.GONE);

                Toast.makeText(getActivity(), "close", Toast.LENGTH_SHORT).show();
            }
        };


        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Setting up the infoWindow with current's marker info

                try {
                    infoWindowData = agentArrayList.get(markers.indexOf(marker));

                    if (infoWindowData.getCustomer_profile_pic() == null || infoWindowData.getCustomer_profile_pic().equals("") || infoWindowData.getCustomer_profile_pic().equals("null")) {
                        Glide.with(getActivity())
                                .load(R.drawable.hotel_no_img)
                                .into(iv_profile);
                    } else {
                        Glide.with(getActivity())
                                .load(infoWindowData.getCustomer_profile_pic())
                                .placeholder(R.drawable.hotel_no_img)
                                .listener(new RequestListener<String, GlideDrawable>() {
                                    @Override
                                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                        return false;
                                    }
                                })
                                .into(iv_profile);


                        //  picUserDetails_iv  = (ImageView) findViewById(R.id.picUserDetails_iv);
                    }


                    tv_churchname.setText(infoWindowData.getBusiness_name());
                    tv_opentime.setText(infoWindowData.getOpen_time());
                    tv_hotelNamefull.setText(infoWindowData.getBusiness_name());
                    tv_closetime.setText(infoWindowData.getClose_time());

                    ratingBar.setRating(Float.parseFloat(infoWindowData.getRatting()));

                } catch (ArrayIndexOutOfBoundsException e) {
                    Log.e("Exception", e.getMessage());

                }

                infoButtonListener.setMarker(marker);

                // We must call this to set the current marker and infoWindow references
                // to the MapWrapperLayout
                mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);
                return infoWindow;
            }
        });

        infoButtonListener = new OnInfoWindowElemTouchListener(infoButton2, getResources().getDrawable(R.drawable.withdraw_money_button), getResources().getDrawable(R.drawable.withdraw_money_button)) {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                Intent intent = new Intent(getActivity(), AgentDetailsActivity.class);
                intent.putExtra("agentId", infoWindowData.getCustomer_id());
                startActivity(intent);
            }
        };
        infoButton2.setOnTouchListener(infoButtonListener);


        this.infoButtonListener = new OnInfoWindowElemTouchListener(infoButton1, getResources().getDrawable(R.drawable.get_direction_button_bg), getResources().getDrawable(R.drawable.get_direction_button_bg)) {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
               /* Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://ditu.google.cn/maps?f=d&source=s_d" +
                                "&saddr=" + "&daddr=" + marker.getPosition().latitude + " " + marker.getPosition().longitude + "&hl=zh&t=m&dirflg=d"));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(intent);*/


                String desLocation = "&daddr=" + infoWindowData.getCustomer_lat() + "," + infoWindowData.getCustomer_long();
                Log.e("desLocation", desLocation);
                String currLocation = "saddr=" + 25.7617 + "," + -80.1918;
                Log.e("currLocation", currLocation);
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?" + currLocation + desLocation + "&dirflg=d"));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(intent);
                       /* String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)",  marker.getPosition().latitude, marker.getPosition().longitude, "Where the party is at");
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        intent.setPackage("com.google.android.apps.maps");
                        startActivity(intent);*/
            }
        };

        this.infoButton1.setOnTouchListener(infoButtonListener);


        return true;
    }


    @Override
    public void onMapReady(GoogleMap googleMap1) {
        googleMap = googleMap1;
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMarkerClickListener(this);
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setAllGesturesEnabled(true);

        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);

        googleMap.getUiSettings().setMapToolbarEnabled(true);


    }

    public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }


}
