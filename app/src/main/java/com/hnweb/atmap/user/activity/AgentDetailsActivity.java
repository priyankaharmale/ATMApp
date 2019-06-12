package com.hnweb.atmap.user.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.hnweb.atmap.R;
import com.hnweb.atmap.atm.activity.AgentHomeActivity;
import com.hnweb.atmap.contants.AppConstant;
import com.hnweb.atmap.inteface.OnCallBack;
import com.hnweb.atmap.user.adaptor.ImagesAdaptor;
import com.hnweb.atmap.utils.AlertUtility;
import com.hnweb.atmap.utils.AppUtils;
import com.hnweb.atmap.utils.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AgentDetailsActivity extends AppCompatActivity implements OnCallBack {
    @SuppressLint("SetTextI18n")
    LoadingDialog loadingDialog;
    TextView tv_hotelName, tv_address, tv_opentime, tv_closetime;
    ImageView iv_back;
    Button btn_strtwithdraw;
    TextView tv_barcodeNo;
    Toolbar toolbar;
    SharedPreferences prefs;
    String user_id, agentId;
    String stramount = "";
    RatingBar ratting;
    String address, businessName, customer_profile_pic, like_status = "", ratings = "";
    String images;
    ArrayList personImages = new ArrayList<>(Arrays.asList(R.drawable.ten_usd, R.drawable.twenty_usd, R.drawable.forty_usd, R.drawable.sixty_usd, R.drawable.eighty_usd, R.drawable.hundred_usd, R.drawable.two_hundred_usd));
    ArrayList personNames = new ArrayList<>(Arrays.asList("10", "20", "40", "60", "80", "100", "200"));
    OnCallBack onCallBack;
    String isClick = "0";
    ImageView iv_fav, iv_share, iv_imgae;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadingDialog = new LoadingDialog(this);

        setContentView(R.layout.activity_locationdetails);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        btn_strtwithdraw = findViewById(R.id.btn_strtwithdraw);
        iv_fav = findViewById(R.id.iv_fav);
        tv_hotelName = findViewById(R.id.tv_hotalName);
        iv_back = toolbar.findViewById(R.id.iv_back);
        tv_opentime = findViewById(R.id.tv_opentime);
        tv_closetime = findViewById(R.id.tv_closetime);
        tv_address = findViewById(R.id.tv_address);
        iv_share = findViewById(R.id.iv_share);
        iv_imgae = findViewById(R.id.iv_imgae);
        ratting = findViewById(R.id.ratting);
        Intent intent = getIntent();
        agentId = intent.getStringExtra("agentId");
        prefs = getApplicationContext().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);
        user_id = prefs.getString(AppConstant.KEY_ID, null);
        onCallBack = this;
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        ImagesAdaptor customAdapter = new ImagesAdaptor(AgentDetailsActivity.this, personImages, personNames, onCallBack);
        recyclerView.setAdapter(customAdapter);
        getAgentDetails(agentId);
        btn_strtwithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (stramount.equalsIgnoreCase("")) {
                    Toast.makeText(AgentDetailsActivity.this, "Please Select the amount", Toast.LENGTH_SHORT).show();
                } else {
                    startwithdraw();
                }
            }
        });
        iv_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isClick.equalsIgnoreCase("0")) {
                    addToFav(agentId, user_id);
                } else if (isClick.equalsIgnoreCase("1")) {
                    removeToFav(agentId, user_id);
                }

            }
        });
        iv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareIt();
            }
        });
    }


    private void getAgentDetails(final String agentId) {
        loadingDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_GET_AGENTDATA,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        System.out.println("API_GET_AGENTDATA" + response);
                        try {
                            final JSONObject j = new JSONObject(response);
                            int message_code = j.getInt("message_code");
                            String message = j.getString("message");

                            if (loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }
                            if (message_code == 1) {

                                try {
                                    JSONObject jsonObject = j.getJSONObject("detail");

                                    String user_id = jsonObject.getString("customer_id");
                                    String user_name = jsonObject.getString("customer_name");
                                    address = jsonObject.getString("customer_address");
                                    String user_email = jsonObject.getString("customer_email");
                                    String user_phone = jsonObject.getString("customer_mobile");
                                    String user_type = jsonObject.getString("user_type");
                                    String opentime = jsonObject.getString("open_time");
                                    String cloasetime = jsonObject.getString("close_time");
                                    ratings = jsonObject.getString("ratting");
                                    businessName = jsonObject.getString("business_name");
                                    customer_profile_pic = jsonObject.getString("customer_profile_pic");
                                    like_status = jsonObject.getString("like_status");
                                    ratting.setRating(Float.parseFloat(ratings));

                                    if (like_status.equalsIgnoreCase("") || like_status.equalsIgnoreCase("0")) {
                                        iv_fav.setImageResource(R.drawable.unfavorite_icon);
                                        isClick = "0";
                                    } else if (like_status.equalsIgnoreCase("1")) {
                                        iv_fav.setImageResource(R.drawable.favorite_icon);
                                        isClick = "1";
                                    }

                                    if (customer_profile_pic == null || customer_profile_pic.equals("") || customer_profile_pic.equals("null")) {
                                        Glide.with(AgentDetailsActivity.this)
                                                .load(R.drawable.no_img)
                                                .into(iv_imgae);
                                    } else {
                                        Glide.with(AgentDetailsActivity.this)
                                                .load(customer_profile_pic)
                                                .placeholder(R.drawable.no_img)
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
                                                .into(iv_imgae);


                                        //  picUserDetails_iv  = (ImageView) findViewById(R.id.picUserDetails_iv);
                                    }
                                    tv_hotelName.setText(businessName);
                                    tv_closetime.setText(cloasetime);
                                    tv_opentime.setText(opentime);
                                    tv_address.setText(address);


                                } catch (JSONException e) {
                                    System.out.println("jsonexeption" + e.toString());
                                }

                            } else {
                                message = j.getString("message");
                                AlertDialog.Builder builder = new AlertDialog.Builder(AgentDetailsActivity.this);
                                builder.setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                        } catch (JSONException e) {
                            System.out.println("jsonexeption" + e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String reason = AppUtils.getVolleyError(AgentDetailsActivity.this, error);
                        AlertUtility.showAlert(AgentDetailsActivity.this, reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("agent_id", agentId);
                    params.put("user_id", user_id);

                } catch (Exception e) {
                    System.out.println("error" + e.toString());
                }
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        stringRequest.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }


    private void startwithdraw() {
        loadingDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_START_WITHDRAW,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        System.out.println("res_register" + response);
                        try {
                            final JSONObject j = new JSONObject(response);
                            int message_code = j.getInt("message_code");
                            String message = j.getString("message");

                            if (loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }
                       /*     AgentHomeActivity agentHomeActivity = new AgentHomeActivity();
                            agentHomeActivity.getNotificationCount();*/
                            if (message_code == 1) {

                                try {
                                    final JSONObject jsonObject = j.getJSONObject("detail");
                                    String barcode = jsonObject.getString("barcode_no");
                                    String request_id = jsonObject.getString("request_id");
                                    Intent intent = new Intent(AgentDetailsActivity.this, AgentBarcodeScanActivity.class);
                                    intent.putExtra("barcode", barcode);
                                    intent.putExtra("image", images);
                                    intent.putExtra("businessName", businessName);
                                    intent.putExtra("address", address);
                                    intent.putExtra("request_id", request_id);
                                    intent.putExtra("agentId", agentId);
                                    intent.putExtra("ratings", ratings);

                                    startActivity(intent);


                                } catch (JSONException e) {
                                    System.out.println("jsonexeption" + e.toString());
                                }

                            } else {
                                message = j.getString("message");
                                AlertDialog.Builder builder = new AlertDialog.Builder(AgentDetailsActivity.this);
                                builder.setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                        } catch (JSONException e) {
                            System.out.println("jsonexeption" + e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String reason = AppUtils.getVolleyError(AgentDetailsActivity.this, error);
                        AlertUtility.showAlert(AgentDetailsActivity.this, reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("agent_id", agentId);
                    params.put("id", user_id);
                    params.put("amount", stramount);

                } catch (Exception e) {
                    System.out.println("error" + e.toString());
                }
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        stringRequest.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }


    @Override
    public void selctedImge(String amount, String image) {
        //  Toast.makeText(this, "Selected Amount " + amount + image, Toast.LENGTH_SHORT).show();
        stramount = amount;
        images = image;
    }

    @Override
    public void callbackYear(String count) {

    }

    @Override
    public void callbackMonthe(String month) {

    }

    @Override
    public void refresh() {

    }


    private void shareIt() {
//sharing implementation here
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "ATM App");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Click here to Download the App \n https://drive.google.com/open?id=1blM2NwyD98U39apsGqIRdamJB0zTuykb");
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }


    private void addToFav(final String agentId, final String user_id) {
        loadingDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_ADD_TO_FAV,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        System.out.println("res_register" + response);
                        try {
                            final JSONObject j = new JSONObject(response);
                            int message_code = j.getInt("message_code");
                            String message = j.getString("message");

                            if (loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }
                            if (message_code == 1) {
                                message = j.getString("message");

                                AlertDialog.Builder builder = new AlertDialog.Builder(AgentDetailsActivity.this);
                                builder.setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                iv_fav.setImageResource(R.drawable.favorite_icon);
                                                isClick = "1";
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();

                            } else {
                                message = j.getString("message");
                                AlertDialog.Builder builder = new AlertDialog.Builder(AgentDetailsActivity.this);
                                builder.setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                        } catch (JSONException e) {
                            System.out.println("jsonexeption" + e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String reason = AppUtils.getVolleyError(AgentDetailsActivity.this, error);
                        AlertUtility.showAlert(AgentDetailsActivity.this, reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("agent_id", agentId);
                    params.put("user_id", user_id);

                } catch (Exception e) {
                    System.out.println("error" + e.toString());
                }
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        stringRequest.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void removeToFav(final String agentId, final String user_id) {
        loadingDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_REMOVE_TO_FAV,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        System.out.println("res_register" + response);
                        try {
                            final JSONObject j = new JSONObject(response);
                            int message_code = j.getInt("message_code");
                            String message = j.getString("message");

                            if (loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }
                            if (message_code == 1) {
                                message = j.getString("message");

                                AlertDialog.Builder builder = new AlertDialog.Builder(AgentDetailsActivity.this);
                                builder.setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                iv_fav.setImageResource(R.drawable.unfavorite_icon);
                                                isClick = "0";
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();

                            } else {
                                message = j.getString("message");
                                AlertDialog.Builder builder = new AlertDialog.Builder(AgentDetailsActivity.this);
                                builder.setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                        } catch (JSONException e) {
                            System.out.println("jsonexeption" + e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String reason = AppUtils.getVolleyError(AgentDetailsActivity.this, error);
                        AlertUtility.showAlert(AgentDetailsActivity.this, reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("agent_id", agentId);
                    params.put("user_id", user_id);

                } catch (Exception e) {
                    System.out.println("error" + e.toString());
                }
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        stringRequest.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

}
