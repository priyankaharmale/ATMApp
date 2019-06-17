package com.hnweb.atmap.user.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.hnweb.atmap.R;
import com.hnweb.atmap.activity.ChooseUserActivity;
import com.hnweb.atmap.atm.fragment.NotificationFragment;
import com.hnweb.atmap.contants.AppConstant;
import com.hnweb.atmap.user.fragment.FavoriteListFragment;
import com.hnweb.atmap.user.fragment.MapViewFragment;
import com.hnweb.atmap.user.fragment.UserAddBankAccountFragment;
import com.hnweb.atmap.user.fragment.UserProfileFragment;
import com.hnweb.atmap.user.fragment.UserTransactionHistoryFragment;
import com.hnweb.atmap.utils.ConnectionDetector;
import com.hnweb.atmap.utils.LoadingDialog;
import com.hnweb.atmap.utils.NotificationUpdateModel;
import com.hnweb.atmap.utils.ProfileUpdateModel;
import com.hnweb.atmap.utils.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
/* * Created by Priyanka H on 1/04/2019.
 */

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ProfileUpdateModel.OnCustomStateListener, NotificationUpdateModel.OnCustomNotificationStateListener {

    LoadingDialog loadingDialog;
    DrawerLayout drawer;
    private static HomeActivity instance;

    private View navHeader;
    String profile_image, user_name, user_email, user_id;
    private static final int TIME_DELAY = 2000;
    private static long back_pressed;
    public Toolbar toolbar;
    String deviceToken = "";

    ConnectionDetector connectionDetector;
    ImageView imageViewProfile, imageViewClose, imageViewUpload;
    TextView textViewUserName, textViewAdrress;
    SharedPreferences prefs;
    FrameLayout ll_notification;
    ProgressBar progressBar;
    TextView textCartItemCount;
    String mCartItemCount = "";
    ImageView iv_notification;

    @Override
    protected void onResume() {
        super.onResume();
        try {
            stateChanged();
            getNotificationCount();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actvity_home);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        iv_notification = toolbar.findViewById(R.id.iv_notification);
        setSupportActionBar(toolbar);

        instance=this;
        getdeviceToken();
        connectionDetector = new ConnectionDetector(HomeActivity.this);

        loadingDialog = new LoadingDialog(HomeActivity.this);

        prefs = getApplicationContext().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);
        user_id = prefs.getString(AppConstant.KEY_ID, null);
        profile_image = prefs.getString(AppConstant.KEY_IMAGE, null);
        user_name = prefs.getString(AppConstant.KEY_NAME, null);
        user_email = prefs.getString(AppConstant.KEY_EMAIL, null);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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

        //    imageViewUpload.setVisibility(View.VISIBLE);


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
                drawer.closeDrawer(GravityCompat.START);
                Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
                startActivity(intent);
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

        if (savedInstanceState == null) {

            Fragment fragment = null;
            fragment = new MapViewFragment();
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.addToBackStack(null);
            transaction.replace(R.id.frame_layout, fragment);
            transaction.commit();
            //toolbarmiddletext.setText("Home");
            //getSupportActionBar().setTitle("Find Craves");
            //drawerFragment.closeDrawer(GravityCompat.START);
        }
        if (connectionDetector.isConnectingToInternet()) {
            getNotificationCount();
        } else {
            Toast.makeText(HomeActivity.this, "No Internet Connection, Please try Again!!", Toast.LENGTH_SHORT).show();

        }
        iv_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(HomeActivity.this, "Under Development!", Toast.LENGTH_SHORT).show();

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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
        textCartItemCount = (TextView) actionView.findViewById(R.id.cart_badge);
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });

        ll_notification = actionView.findViewById(R.id.ll_notification);
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });
        ll_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = null;
                fragment = new NotificationFragment();
                Bundle bundle = new Bundle();
                bundle.putString("callfrom", "1");
                fragment.setArguments(bundle);
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.addToBackStack(null);
                transaction.replace(R.id.frame_layout, fragment);
                transaction.commit();
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
            fragment = new MapViewFragment();
        } else if (id == R.id.nav_logout) {
            showLogoutAlert();
        } else if (id == R.id.nav_myprofile) {
            fragment = new UserProfileFragment();
        } else if (id == R.id.nav_trashistory) {
            fragment = new UserTransactionHistoryFragment();
        } else if (id == R.id.nav_favorite) {
            fragment = new FavoriteListFragment();
        } else if (id == R.id.nav_bankaccount) {
            fragment = new UserAddBankAccountFragment();
        }


        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            //fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fragmentManager.beginTransaction().replace(R.id.frame_layout, fragment).addToBackStack(null).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showLogoutAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name);
        builder.setMessage("Are you sure you want to Logout");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {

                prefs.edit().clear().apply();
                //   showLogoutAlert();
                // postTokenRemoved();


                Intent in = new Intent(HomeActivity.this, ChooseUserActivity.class);
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
        getNotificationCount();

        user_id = prefs.getString(AppConstant.KEY_ID, null);
        profile_image = prefs.getString(AppConstant.KEY_IMAGE, null);
        user_name = prefs.getString(AppConstant.KEY_NAME, null);
        user_email = prefs.getString(AppConstant.KEY_EMAIL, null);

        textViewUserName.setText(user_name);
        textViewAdrress.setText(user_email);

        if (profile_image.equals("") || profile_image == null || profile_image.equalsIgnoreCase("null")) {
            Glide.with(getApplicationContext())
                    .load(R.drawable.hotel_no_img)
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .dontAnimate()
                    .into(imageViewProfile);
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

        }
    }



    public void getNotificationCount() {

        StringRequest postRequest = new StringRequest(Request.Method.POST, AppConstant.API_GETNOTIFICATIONCOUNT,
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


    @Override
    public void notificationStateChanged() {
        getNotificationCount();
    }

    public static HomeActivity getInstance() {
        return instance;
    }

}
