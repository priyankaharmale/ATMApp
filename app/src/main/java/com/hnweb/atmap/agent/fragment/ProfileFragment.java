package com.hnweb.atmap.agent.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.model.LatLng;
import com.hnweb.atmap.R;
import com.hnweb.atmap.activity.LoginActivity;
import com.hnweb.atmap.contants.AppConstant;
import com.hnweb.atmap.utils.LoadingDialog;
import com.hnweb.atmap.utils.Utils;
import com.hnweb.atmap.utils.Validations;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.app.Activity.RESULT_CANCELED;
import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {

    String stringLatitude = "", stringLongitude = "";
    LatLng latLng;
    LoadingDialog loadingDialog;
    TimePickerDialog timePickerDialog;
    Calendar calendar;
    int currentHour;
    Button btn_update;
    int currentMinute;
    String amPm;
    String strAdd = "";
    String camImage, imagePath12;
    ImageView iv_profilePic, iv_edit;
    public static File destination;
    private int GALLERY = 1, CAMERA = 2;
    public static final int REQUEST_CAMERA = 5;
    protected static final int REQUEST_STORAGE_ACCESS_PERMISSION = 102;
    SharedPreferences prefs;
    EditText et_fullname, et_mobilno, et_email, et_bankname, et_accountNo, et_ssn, et_routerNo, et_businessnmae, et_opentime, et_closetime;
    String fullname, mobilno, email, bankname, accountNo, ssn, routerNo, businessnmae, opentime, closetime;
    String user_id;
    SupportPlaceAutocompleteFragment locationAutocompleteFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        et_opentime = view.findViewById(R.id.et_opentime);
        et_closetime = view.findViewById(R.id.et_closetime);
        et_fullname = view.findViewById(R.id.et_fullname);
        et_mobilno = view.findViewById(R.id.et_mobilno);
        et_email = view.findViewById(R.id.et_email);

        et_bankname = view.findViewById(R.id.et_bankname);
        et_accountNo = view.findViewById(R.id.et_accountNo);
        et_ssn = view.findViewById(R.id.et_ssn);
        et_routerNo = view.findViewById(R.id.et_routerNo);
        et_businessnmae = view.findViewById(R.id.et_businessnmae);
        iv_profilePic = view.findViewById(R.id.iv_profilePic);
        iv_edit = view.findViewById(R.id.profile_image_photoupload);
        btn_update = view.findViewById(R.id.btn_update);

        loadingDialog = new LoadingDialog(getActivity());

        prefs = getActivity().getApplicationContext().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);
        user_id = prefs.getString(AppConstant.KEY_ID, null);


        try {
            locationAutocompleteFragment = ( SupportPlaceAutocompleteFragment )
                    getChildFragmentManager()
                            .findFragmentById(R.id.place_autocomplete_fragment);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                locationAutocompleteFragment.getView().setAutofillHints("Select Church Address");
            }

            locationAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(Place place) {

                    String value = String.valueOf(latLng);
                    if (place != null) {
                        latLng = place.getLatLng();
                        stringLatitude = String.valueOf(latLng.latitude);
                        stringLongitude = String.valueOf(latLng.longitude);
                        Log.d("GETLocation", stringLatitude + " :: " + stringLongitude);

                        getCompleteAddressString(latLng.latitude, latLng.longitude);

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
            Log.v("exgfd", "" + ex.toString());

        }
        et_opentime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOpenTime();
            }
        });
        et_closetime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCloseTime();
            }
        });
        iv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPictureDialog();
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkValidation()) {

                    if (Utils.isNetworkAvailable(getActivity())) {
                        updateprofile();

                    } else {
                        Utils.myToast1(getActivity());
                    }
                }
            }
        });
        getUserDetails();
        return view;
    }

    private void getOpenTime() {
        calendar = Calendar.getInstance();
        currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        currentMinute = calendar.get(Calendar.MINUTE);

        timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                if (hourOfDay >= 12) {
                    amPm = "PM";
                } else {
                    amPm = "AM";
                }
                et_opentime.setText(String.format("%02d:%02d", hourOfDay, minutes) + " " + amPm);
            }
        }, currentHour, currentMinute, false);

        timePickerDialog.show();
    }

    private void getCloseTime() {
        calendar = Calendar.getInstance();
        currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        currentMinute = calendar.get(Calendar.MINUTE);

        timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                if (hourOfDay >= 12) {
                    amPm = "PM";
                } else {
                    amPm = "AM";
                }
                et_closetime.setText(String.format("%02d:%02d", hourOfDay, minutes) + " " + amPm);
            }
        }, currentHour, currentMinute, false);

        timePickerDialog.show();
    }

    private void showPictureDialog() {
        android.app.AlertDialog.Builder pictureDialog = new android.app.AlertDialog.Builder(getActivity());
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                isPermissionGrantedImageGallery();
                                break;
                            case 1:
                                isPermissionGrantedImage();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }


    public void isPermissionGrantedImageGallery() {

        System.out.println("Click Image");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN // Permission was added in API Level 16
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                    getString(R.string.mis_permission_rationale),
                    REQUEST_STORAGE_ACCESS_PERMISSION);
        } else {
            choosePhotoFromGallary();
        }

    }

    public void isPermissionGrantedImage() {
        System.out.println("Click Image");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN // Permission was added in API Level 16
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                    getString(R.string.mis_permission_rationale),
                    REQUEST_STORAGE_ACCESS_PERMISSION);
        } else {
            camerImage();
        }

    }

    private void requestPermission(final String permission, String rationale, final int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission)) {
            new android.app.AlertDialog.Builder(getActivity())
                    .setTitle(R.string.mis_permission_dialog_title)
                    .setMessage(rationale)
                    .setPositiveButton(R.string.mis_permission_dialog_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, requestCode);
                        }
                    })
                    .setNegativeButton(R.string.mis_permission_dialog_cancel, null)
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, requestCode);
        }
    }

    public void camerImage() {
        System.out.println("Click Image11");
        String name = AppConstant.dateToString(new Date(), "yyyy-MM-dd-hh-mm-ss");
        destination = new File(Environment.getExternalStorageDirectory(), name + ".jpg");


        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(destination));

        intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".my.package.name.provider", destination));
        startActivityForResult(intent, REQUEST_CAMERA);
    }


    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bm = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

                    FileOutputStream fo;
                    File destination = new File(Environment.getExternalStorageDirectory(),
                            System.currentTimeMillis() + ".jpg");
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                    imagePath12 = destination.getAbsolutePath();

                    Log.i("Path123", imagePath12);

                    camImage = imagePath12;

                    try {
                        Glide.with(getActivity())
                                .load(camImage)
                                .error(R.drawable.image_marie)
                                .centerCrop()
                                .crossFade()
                                .listener(new RequestListener<String, GlideDrawable>() {
                                    @Override
                                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                        //    progressBar.setVisibility(View.GONE);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                        // progressBar.setVisibility(View.GONE);
                                        return false;
                                    }
                                })
                                .into(iv_profilePic);
                    } catch (Exception e) {
                        Log.e("Exception", e.getMessage());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == REQUEST_CAMERA) {
            System.out.println("REQUEST_CAMERA");
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 10;
            String imagePath = destination.getAbsolutePath();
            Log.i("Path", imagePath);
            camImage = imagePath;
            //Toast.makeText(getActivity(), camImage, Toast.LENGTH_SHORT).show();
            try {
                Glide.with(getActivity())
                        .load(camImage)
                        .error(R.drawable.image_marie)
                        .centerCrop()
                        .crossFade()
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                //  progressBar.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                // progressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(iv_profilePic);
            } catch (Exception e) {
                Log.e("Exception", e.getMessage());
            }
        }
    }

    private boolean checkValidation() {
        boolean ret = true;

        if (!Validations.hasText(et_fullname, "Please Enter Full Name "))
            ret = false;
        if (!Validations.hasText(et_mobilno, "Please Enter Mobile number "))
            ret = false;
        if (!Validations.hasText(et_email, "Please Enter Email ID "))
            ret = false;
        if (!Validations.isEmailAddress(et_email, true, "Please Enter Valid Email ID"))
            ret = false;
        if (!Validations.hasText(et_bankname, "Please Enter Bank Name"))
            ret = false;
        if (!Validations.hasText(et_accountNo, "Please Enter Account Number"))
            ret = false;
        if (!Validations.hasText(et_ssn, "Please Enter Social Security Number"))
            ret = false;
        if (!Validations.hasText(et_routerNo, "Please Router Number"))
            ret = false;
        if (!Validations.hasText(et_businessnmae, "Please Enter Business Name"))
            ret = false;
        if (!Validations.hasText(et_opentime, "Please Select Open Time"))
            ret = false;
        if (!Validations.hasText(et_closetime, "Please Select Close Time"))
            ret = false;
        return ret;
    }


    private void updateprofile() {
        loadingDialog.show();

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = AppConstant.API_UPDATE_PROFILE;

        StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String object) {
                        System.out.println("updateprofile" + object);
                        try {
                            final JSONObject j = new JSONObject(object);
                            int message_code = j.getInt("message_code");
                            String message = j.getString("message");
                            if (loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }
                            if (message_code == 1) {
                                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
                                builder.setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

                                                getUserDetails();
                                               /* try {
                                                    JSONArray jsonArray = j.getJSONArray("details");

                                                    for (int i = 0; i <= jsonArray.length(); i++) {
                                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                                        String user_id = jsonObject.getString("rid");
                                                        String user_name = jsonObject.getString("full_name");
                                                        String user_email = jsonObject.getString("email");
                                                        String profile_photo = jsonObject.getString("profile_photo");
                                                        String user_type = jsonObject.getString(AppConstant.KEY_USERTYPE);

                                                  *//*      editorUser.putString(AppConstant.KEY_ID, user_id);
                                                        editorUser.putString(AppConstant.KEY_NAME, user_name);
                                                        editorUser.putString(AppConstant.KEY_EMAIL, user_email);
                                                        editorUser.putString(AppConstant.KEY_USERTYPE, user_type);
                                                        editorUser.putString(AppConstant.KEY_IMAGE, profile_photo);
                                                        editorUser.commit();*//*


                                                        dialog.dismiss();
                                                    }
                                                } catch (JSONException e) {
                                                    System.out.println("jsonexeption" + e.toString());
                                                }*/
                                            }
                                        });
                                android.support.v7.app.AlertDialog alert = builder.create();
                                alert.show();
                            } else {
                                message = j.getString("message");
                                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
                                builder.setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                            }
                                        });
                                android.support.v7.app.AlertDialog alert = builder.create();
                                alert.show();
                            }
                        } catch (JSONException e) {
                            System.out.println("jsonexeption" + e.toString());
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    }

                    Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();

                    loadingDialog.dismiss();

                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        String status = response.getString("status");
                        String message = response.getString("message");

                        Log.e("Error Status", status);
                        Log.e("Error Message", message);

                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = message + " Please login again";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message + " Check your inputs";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message + " Something is getting wrong";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();


                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> Stringparams = new HashMap<String, String>();


                fullname = et_fullname.getText().toString();
                bankname = et_bankname.getText().toString();
                accountNo = et_accountNo.getText().toString();
                routerNo = et_routerNo.getText().toString();
                ssn = et_ssn.getText().toString();
                email = et_email.getText().toString();
                mobilno = et_mobilno.getText().toString();
                businessnmae = et_businessnmae.getText().toString();
                opentime = et_opentime.getText().toString();
                closetime = et_closetime.getText().toString();


                if (camImage == null || camImage.equalsIgnoreCase("")) {
                } else {
                    Stringparams.put("img", String.valueOf(camImage));
                }
                Stringparams.put("Accept", "application/json");
                Stringparams.put("Content-Type", "application/json");
                Stringparams.put("name", fullname);
                Stringparams.put("address", strAdd);
                Stringparams.put("email", email);
                Stringparams.put("bank_name", bankname);
                Stringparams.put("bank_account_no", accountNo);
                Stringparams.put("cus_lat", stringLatitude);
                Stringparams.put("cus_long", stringLongitude);
                Stringparams.put("router_no", routerNo);
                Stringparams.put("ssn_no", ssn);
                Stringparams.put("mobile_no", mobilno);
                Stringparams.put("business_name", businessnmae);
                Stringparams.put("open_time", opentime);
                Stringparams.put("close_time", closetime);
                Stringparams.put("id", user_id);
                Log.e("params", Stringparams.toString());
                // System.out.println(params);
                return Stringparams;
            }
        };
        queue.add(strRequest);
    }


    private void getUserDetails() {


        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = AppConstant.API_GET_PROFILE;

        StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("responseprofile", response);
                        try {

                            JSONObject json = new JSONObject(response);

                            String message = json.getString("message");
                            int msg = json.getInt("message_code");

                            if (msg == 1) {
                                loadingDialog.dismiss();

                                JSONObject jsonObject = json.getJSONObject("detail");

                                String userPic_url = jsonObject.getString("customer_profile_pic");
                                String email = jsonObject.getString("customer_email");
                                String customer_mobile = jsonObject.getString("customer_mobile");
                                String customer_address = jsonObject.getString("customer_address");
                                String customer_name = jsonObject.getString("customer_name");
                                String customer_bank_name = jsonObject.getString("customer_bank_name");
                                String customer_acc_num = jsonObject.getString("customer_acc_num");
                                String customer_ssn = jsonObject.getString("customer_ssn");
                                String router_number = jsonObject.getString("router_number");
                                String business_name = jsonObject.getString("business_name");



                                et_bankname.setText(customer_bank_name);
                                et_accountNo.setText(customer_acc_num);
                                et_ssn.setText(customer_ssn);
                                et_routerNo.setText(router_number);
                                et_businessnmae.setText(business_name);
                                et_email.setText(email);
                                et_mobilno.setText(customer_mobile);
                                locationAutocompleteFragment.setText(customer_address);
                                et_fullname.setText(customer_name);

                                if (userPic_url == null || userPic_url.equals("") || userPic_url.equals("null") || userPic_url.equals("http://tech599.com/tech599.com/johnaks/FirstFruit/")) {
                                    Glide.with(getActivity())
                                            .load(R.drawable.image_marie)
                                            .into(iv_profilePic);
                                } else {
                                    Glide.with(getActivity())
                                            .load(userPic_url)
                                            //  .placeholder(R.mipmap.img_no_navigation)
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
                                            .into(iv_profilePic);


                                    //  picUserDetails_iv  = (ImageView) findViewById(R.id.picUserDetails_iv);
                                }
                            } else {
                                loadingDialog.dismiss();
                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";

                Log.i("Error", errorMessage);
                error.printStackTrace();

                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", user_id);
                return params;
            }
        };

        queue.add(strRequest);
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


}
