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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hnweb.atmap.R;
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
    String address, businessName;
    String images;
    ArrayList personImages = new ArrayList<>(Arrays.asList(R.drawable.ten_usd, R.drawable.twenty_usd, R.drawable.forty_usd, R.drawable.sixty_usd, R.drawable.eighty_usd, R.drawable.hundred_usd, R.drawable.two_hundred_usd));
    ArrayList personNames = new ArrayList<>(Arrays.asList("10", "20", "40", "60", "80", "100", "200"));
    OnCallBack onCallBack;
    ImageView iv_fav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadingDialog = new LoadingDialog(this);

        setContentView(R.layout.activity_locationdetails);
        toolbar = ( Toolbar ) findViewById(R.id.toolbar);
        btn_strtwithdraw = findViewById(R.id.btn_strtwithdraw);
        iv_fav = findViewById(R.id.iv_fav);
        tv_hotelName = findViewById(R.id.tv_hotalName);
        iv_back = toolbar.findViewById(R.id.iv_back);
        tv_opentime = findViewById(R.id.tv_opentime);
        tv_closetime = findViewById(R.id.tv_closetime);
        tv_address = findViewById(R.id.tv_address);
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


    }


    private void getAgentDetails(final String agentId) {
        loadingDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_GET_AGENTDATA,
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
                                    businessName = jsonObject.getString("business_name");

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
        Toast.makeText(this, "Selected Amount " + amount + image, Toast.LENGTH_SHORT).show();
        stramount = amount;
        images = image;
    }

    @Override
    public void callbackYear(String count) {

    }

    @Override
    public void callbackMonthe(String month) {

    }
}
