package com.hnweb.atmap.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.hnweb.atmap.R;
import com.hnweb.atmap.contants.AppConstant;
import com.hnweb.atmap.fragment.MapFragment;
import com.hnweb.atmap.fragment.ProfileFragment;
import com.hnweb.atmap.utils.ConnectionDetector;
import com.hnweb.atmap.utils.LoadingDialog;
import com.hnweb.atmap.utils.ProfileUpdateModel;
import com.hnweb.atmap.utils.SharedPrefManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
/* * Created by Priyanka H on 01/04/2019.
 */

public class AgentHomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ProfileUpdateModel.OnCustomStateListener {

    LoadingDialog loadingDialog;
    DrawerLayout drawer;
    private View navHeader;
    private int GALLERY = 1, CAMERA = 2;
    public static final int REQUEST_CAMERA = 5;
    public static File destination;
    public MenuItem liveitemList, liveitemMap;
    String profile_image, user_name, user_email, user_id;
    private static final int TIME_DELAY = 2000;
    private static long back_pressed;
    public Toolbar toolbar;
    String deviceToken = "";
    ConnectionDetector connectionDetector;
    ImageView imageViewProfile, imageViewClose, imageViewUpload;
    TextView textViewUserName, textViewAdrress;
    SharedPreferences prefs;
    ProgressBar progressBar;
    TextView textCartItemCount;
    String mCartItemCount = "";
    ImageView iv_notification;
    protected static final int REQUEST_STORAGE_ACCESS_PERMISSION = 102;
    String camImage, imagePath12;

    @Override
    protected void onResume() {
        super.onResume();
        try {
            stateChanged();
            //getNotificationCount();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actvity_agenthome);
        toolbar = ( Toolbar ) findViewById(R.id.toolbar);
        iv_notification = toolbar.findViewById(R.id.iv_notification);
        setSupportActionBar(toolbar);


        getdeviceToken();
        connectionDetector = new ConnectionDetector(AgentHomeActivity.this);

        loadingDialog = new LoadingDialog(AgentHomeActivity.this);

        prefs = getApplicationContext().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);
        user_id = prefs.getString(AppConstant.KEY_ID, null);
        profile_image = prefs.getString(AppConstant.KEY_IMAGE, null);
        user_name = prefs.getString(AppConstant.KEY_NAME, null);
        user_email = prefs.getString(AppConstant.KEY_EMAIL, null);

        drawer = ( DrawerLayout ) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navHeader = navigationView.getHeaderView(0);
        textViewUserName = navHeader.findViewById(R.id.tv_user_name);
        textViewAdrress = navHeader.findViewById(R.id.textView_address);
        progressBar = navHeader.findViewById(R.id.progress_bar_nav);

        imageViewProfile = navHeader.findViewById(R.id.profile_image);
        imageViewClose = navHeader.findViewById(R.id.imageView_close);
        imageViewUpload = navHeader.findViewById(R.id.profile_image_photoupload);

        imageViewUpload.setVisibility(View.VISIBLE);


        textViewUserName.setText(user_name);
        textViewAdrress.setText(user_email);

        imageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        imageViewUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // drawer.closeDrawer(GravityCompat.START);
                showPictureDialog();
            }
        });
        progressBar = navHeader.findViewById(R.id.progress_bar_nav);
       /* if (profile_image.equals("") || profile_image == null) {
            Glide.with(getApplicationContext())
                    .load(R.drawable.img_no_pic_navigation)
                    .into(imageViewProfile);
            //Glide.with(getApplicationContext()).load(R.drawable.user_register).into(imageViewProfile);
        } else {

            progressBar.setVisibility(View.VISIBLE);
            Glide.with(getApplicationContext())
                    .load(profile_image)
                    .placeholder(R.drawable.img_no_pic_navigation)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(imageViewProfile);
            // Glide.with(getApplicationContext()).load(profile_image).into(imageViewProfile);
        }*/

       /* if (savedInstanceState == null) {

            Fragment fragment = null;
            fragment = new MapFragment();
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.addToBackStack(null);
            transaction.replace(R.id.frame_layout, fragment);
            transaction.commit();
            //toolbarmiddletext.setText("Home");
            //getSupportActionBar().setTitle("Find Craves");
            //drawerFragment.closeDrawer(GravityCompat.START);
        }*/
        if (connectionDetector.isConnectingToInternet()) {
            //getNotificationCount();
        } else {
            Toast.makeText(AgentHomeActivity.this, "No Internet Connection, Please try Again!!", Toast.LENGTH_SHORT).show();

        }
        iv_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AgentHomeActivity.this, "Under Development!", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void getdeviceToken() {
        try {
            String token = SharedPrefManager.getInstance(this).getDeviceToken();

            if (token.equals("")) {
                Log.d("Tokan", "t-NULL");
                deviceToken = token;
            } else {
                Log.d("Tokan", token);
                deviceToken = token;
            }

            if (token.equals("")) {

            } else {
                updateDeviceToken(deviceToken);
            }

        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }


    }

    private void updateDeviceToken(String deviceToken) {

        /*//loadingDialog.show();
        String get_user_id = prefs.getString("user_id_user", null);
        Map<String, String> params = new HashMap<>();
        params.put("u_id", get_user_id);
        params.put("devicetoken", deviceToken);
        params.put("device_type", "Android");
        Log.e("Params", params.toString());

        RequestInfo request_info = new RequestInfo();
        request_info.setMethod(RequestInfo.METHOD_POST);
        request_info.setRequestTag("login");
        request_info.setUrl(WebsServiceURLUser.USER_UPDATE_TOKEN_DEVICE);
        request_info.setParams(params);

        DataUtility.submitRequest(loadingDialog, MainActivityUser.this, request_info, false, new DataUtility.OnDataCallbackListner() {
            @Override
            public void OnDataReceived(String data) {
                if (loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
                Log.i("Response", "MainActivityUser" + data);

                try {
                    JSONObject jobj = new JSONObject(data);
                    int message_code = jobj.getInt("message_code");

                    String msg = jobj.getString("message");
                    Log.e("FLag", message_code + " :: " + msg);
                    if (message_code == 1) {
                        Toast.makeText(MainActivityUser.this, msg, Toast.LENGTH_SHORT).show();
                    } else {
                        //Utils.AlertDialog(MainActivityUser.this, msg);

                    }
                } catch (JSONException e) {
                    System.out.println("jsonexeption" + e.toString());
                }
            }

            @Override
            public void OnError(String message) {
                if (loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }

                AlertUtility.showAlert(MainActivityUser.this, false, "Network Error,Please Check Internet Connection");
            }
        });*/
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = ( DrawerLayout ) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            int fragments = getSupportFragmentManager().getBackStackEntryCount();
            if (fragments == 1) {
                if (back_pressed + TIME_DELAY > System.currentTimeMillis()) {
                    //super.onBackPressed();
                    finish();
                } else {
                   /* Snackbar snackbar = Snackbar.make(coordinatorLayout, "Press once again to exit!!", Snackbar.LENGTH_LONG);

                    snackbar.show();*/
                    Toast.makeText(getBaseContext(), "Press once again to exit!", Toast.LENGTH_SHORT).show();
                }
                back_pressed = System.currentTimeMillis();
            } else {
                if (getFragmentManager().getBackStackEntryCount() > 1) {
                    getFragmentManager().popBackStack();

                    //getFragmentManager().popBackStack(getSupportFragmentManager().getBackStackEntryAt(0).getId(), getSupportFragmentManager().POP_BACK_STACK_INCLUSIVE);
                } else {
                    super.onBackPressed();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.vendor_main, menu);


        final MenuItem menuItem = menu.findItem(R.id.action_notification);

        View actionView = MenuItemCompat.getActionView(menuItem);
        textCartItemCount = ( TextView ) actionView.findViewById(R.id.cart_badge);
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_notification) {
           /* Fragment fragment = new NotificationFragment();
            if (fragment != null) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                //fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fragmentManager.beginTransaction().replace(R.id.frame_layout, fragment).addToBackStack(null).commit();
            }*/
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;

        if (id == R.id.nav_home) {
            // Handle the camera action
            // fragment = new MapFragment();
        } else if (id == R.id.nav_logout) {
            showLogoutAlert();
        } else if (id == R.id.nav_myprofile) {
            //   showLogoutAlert();
            fragment = new ProfileFragment();
        }


        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            //fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fragmentManager.beginTransaction().replace(R.id.frame_layout, fragment).addToBackStack(null).commit();
        }

        DrawerLayout drawer = ( DrawerLayout ) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showLogoutAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name);
        builder.setMessage("Are you Sure want to Logout");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {

                prefs.edit().clear().apply();
                //   showLogoutAlert();
                // postTokenRemoved();


                Intent in = new Intent(AgentHomeActivity.this, LoginActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(in);
                finish();
                dialog.cancel();


            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void stateChanged() {
        //getNotificationCount();

       /* user_id = prefs.getString(AppConstant.KEY_ID, null);
        profile_image = prefs.getString(AppConstant.KEY_IMAGE, null);
        user_name = prefs.getString(AppConstant.KEY_NAME, null);
        user_email = prefs.getString(AppConstant.KEY_EMAIL, null);

        textViewUserName.setText(user_name);
        textViewAdrress.setText(user_email);

        if (profile_image.equals("") || profile_image == null) {
            Glide.with(getApplicationContext())
                    .load(R.drawable.img_no_pic_navigation)
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .dontAnimate()
                    .into(imageViewProfile);
            //Glide.with(getApplicationContext()).load(R.drawable.user_register).into(imageViewProfile);
        } else {
            progressBar.setVisibility(View.VISIBLE);
            Glide.with(getApplicationContext())
                    .load(profile_image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .fitCenter()
                    .dontAnimate()
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            imageViewProfile.setVisibility(View.VISIBLE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            // linearLayoutBar.setVisibility(View.GONE);
                            imageViewProfile.setVisibility(View.VISIBLE);
                            return false;
                        }
                    })
                    .into(imageViewProfile);
            // Glide.with(getApplicationContext()).load(profile_image).into(imageViewProfile);


            // Toast.makeText(this, "Notification call", Toast.LENGTH_SHORT).show();

        }*/
    }


/*
    private void getNotificationCount() {

        StringRequest postRequest = new StringRequest(Request.Method.POST, AppConstant.API_NOTIFICATIONCOUNT,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d("NotificationResponse", response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int message_code = jsonObject.getInt("message_code");
                            String msg = jsonObject.getString("message");
                            String notification_count = jsonObject.getString("count");
                            Log.d("Notication:-", String.valueOf(message_code));
                            if (message_code == 1) {
                                //Utils.AlertDialog(MainActivityUser.this, msg);
                                mCartItemCount = "";
                                if (notification_count.equals("0")) {
                                    mCartItemCount = "0";
                                    setupBadge();
                                } else {
                                    mCartItemCount = notification_count;
                                    setupBadge();
                                }

                            } else {
                                mCartItemCount = "0";
                                setupBadge();
                                // Utils.AlertDialog(MainActivityUser.this, msg);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

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
                params.put("user_id", user_id);
                Log.e("NotificationCount ", params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        postRequest.setShouldCache(false);
        requestQueue.add(postRequest);
    }
*/


    private void setupBadge() {

        if (textCartItemCount != null) {
            if (mCartItemCount.equals("0")) {
                if (textCartItemCount.getVisibility() != View.GONE) {
                    textCartItemCount.setVisibility(View.GONE);
                }
            } else {
                textCartItemCount.setText(String.valueOf(Math.min(Integer.parseInt(mCartItemCount), 99)));
                if (textCartItemCount.getVisibility() != View.VISIBLE) {
                    textCartItemCount.setVisibility(View.VISIBLE);
                }
            }
        }

    }

    private void showPictureDialog() {
        android.app.AlertDialog.Builder pictureDialog = new android.app.AlertDialog.Builder(AgentHomeActivity.this);
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
                && ActivityCompat.checkSelfPermission(AgentHomeActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
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
                && ActivityCompat.checkSelfPermission(AgentHomeActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                    getString(R.string.mis_permission_rationale),
                    REQUEST_STORAGE_ACCESS_PERMISSION);
        } else {
            camerImage();
        }

    }

    private void requestPermission(final String permission, String rationale, final int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(AgentHomeActivity.this, permission)) {
            new android.app.AlertDialog.Builder(AgentHomeActivity.this)
                    .setTitle(R.string.mis_permission_dialog_title)
                    .setMessage(rationale)
                    .setPositiveButton(R.string.mis_permission_dialog_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(AgentHomeActivity.this, new String[]{permission}, requestCode);
                        }
                    })
                    .setNegativeButton(R.string.mis_permission_dialog_cancel, null)
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(AgentHomeActivity.this, new String[]{permission}, requestCode);
        }
    }

    public void camerImage() {
        System.out.println("Click Image11");
        String name = AppConstant.dateToString(new Date(), "yyyy-MM-dd-hh-mm-ss");
        destination = new File(Environment.getExternalStorageDirectory(), name + ".jpg");


        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(destination));

        intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(AgentHomeActivity.this, AgentHomeActivity.this.getApplicationContext().getPackageName() + ".my.package.name.provider", destination));
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
                    Bitmap bm = MediaStore.Images.Media.getBitmap(AgentHomeActivity.this.getContentResolver(), data.getData());
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
                        Glide.with(AgentHomeActivity.this)
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
                                .into(imageViewProfile);
                    } catch (Exception e) {
                        Log.e("Exception", e.getMessage());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(AgentHomeActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == REQUEST_CAMERA) {
            System.out.println("REQUEST_CAMERA");
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 10;
            String imagePath = destination.getAbsolutePath();
            Log.i("Path", imagePath);
            camImage = imagePath;
            //Toast.makeText(AgentHomeActivity.this, camImage, Toast.LENGTH_SHORT).show();
            try {
                Glide.with(AgentHomeActivity.this)
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
                        .into(imageViewProfile);
            } catch (Exception e) {
                Log.e("Exception", e.getMessage());
            }
        }
    }
}
