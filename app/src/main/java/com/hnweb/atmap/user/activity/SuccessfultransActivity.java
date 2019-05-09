package com.hnweb.atmap.user.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ParseException;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.hnweb.atmap.R;
import com.hnweb.atmap.contants.AppConstant;
import com.hnweb.atmap.utils.AlertUtility;
import com.hnweb.atmap.utils.AppUtils;
import com.hnweb.atmap.utils.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SuccessfultransActivity extends AppCompatActivity {
    @SuppressLint("NewApi")

    String request_id, user_id, agentId;
    SharedPreferences prefs;
    ImageView iv_doller, iv_back;
    Button btn_wantTowithdrwa;
    TextView tv_date, tv_date2, tv_time, tv_time2, tv_hotelname;
    LoadingDialog loadingDialog;
    Toolbar toolbar;
    Button btn_submit;
    RatingBar ratingBar;
    String rating = "";
    EditText et_comment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactionsuccess);
        loadingDialog = new LoadingDialog(this);
        tv_date = findViewById(R.id.tv_date);
        tv_date2 = findViewById(R.id.tv_date2);
        tv_time = findViewById(R.id.tv_time);
        tv_time2 = findViewById(R.id.tv_time2);
        btn_wantTowithdrwa = findViewById(R.id.btn_wantTowithdrwa);
        iv_doller = findViewById(R.id.iv_doller);
        tv_hotelname = findViewById(R.id.tv_hotelname);
        btn_submit = findViewById(R.id.btn_submit);
        ratingBar = findViewById(R.id.ratingBar);
        prefs = getApplicationContext().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);
        user_id = prefs.getString(AppConstant.KEY_ID, null);
        et_comment = findViewById(R.id.et_comment);
        final Intent intent = getIntent();
        agentId = intent.getStringExtra("agentId");
        request_id = intent.getStringExtra("request_id");

        toolbar = findViewById(R.id.toolbar);

        iv_back = toolbar.findViewById(R.id.iv_back);

        btn_wantTowithdrwa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(SuccessfultransActivity.this, HomeActivity.class);
                startActivity(intent1);
                finish();
            }
        });
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(SuccessfultransActivity.this, HomeActivity.class);
                startActivity(intent1);
                finish();
            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rating.equalsIgnoreCase("")) {
                    Toast.makeText(SuccessfultransActivity.this, "Please select Rating", Toast.LENGTH_SHORT).show();
                } else {
                    submitReviewRating();
                }
            }
        });
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

            // Called when the user swipes the RatingBar
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating1, boolean fromUser) {
                rating= String.valueOf(rating1);
            }
        });

        successTranscation(request_id);
    }

    private void successTranscation(final String request_id) {
        loadingDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_SUCCESS_TRANSACTION,
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
                                    message = j.getString("message");
                                    JSONObject jsonObject = j.getJSONObject("detail");
                                    String request_approve_time = jsonObject.getString("request_approve_time");
                                    String request_time = jsonObject.getString("request_time");
                                    String name = jsonObject.getString("agent_name");
                                    String request_amount = jsonObject.getString("request_amount");

                                    tv_hotelname.setText(name);
                                    if (request_amount.equalsIgnoreCase("10")) {
                                        iv_doller.setImageResource(R.drawable.ten_usd);

                                    } else if (request_amount.equalsIgnoreCase("20")) {
                                        iv_doller.setImageResource(R.drawable.twenty_usd);

                                    } else if (request_amount.equalsIgnoreCase("40")) {
                                        iv_doller.setImageResource(R.drawable.forty_usd);

                                    } else if (request_amount.equalsIgnoreCase("60")) {
                                        iv_doller.setImageResource(R.drawable.sixty_usd);

                                    } else if (request_amount.equalsIgnoreCase("80")) {
                                        iv_doller.setImageResource(R.drawable.eighty_usd);

                                    } else if (request_amount.equalsIgnoreCase("100")) {
                                        iv_doller.setImageResource(R.drawable.hundred_usd);

                                    } else if (request_amount.equalsIgnoreCase("200")) {
                                        iv_doller.setImageResource(R.drawable.two_hundred_usd);

                                    }

                                    try {
                                        DateFormat f = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                        Date d = null;
                                        Date d2 = null;
                                        try {
                                            d = f.parse(request_time);
                                            d2 = f.parse(request_approve_time);
                                        } catch (java.text.ParseException e) {
                                            e.printStackTrace();
                                        }
                                        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
                                        SimpleDateFormat time = new SimpleDateFormat(" hh:mm:ss");


                                        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
                                        Date dateu = null;
                                        Date dateu2 = null;
                                        try {
                                            dateu = date.parse(date.format(d));
                                            dateu2 = date.parse(date.format(d2));
                                            String requestdate = formatter.format(dateu);
                                            String requestdate2 = formatter.format(dateu2);
                                            System.out.println("str_startDate: " + requestdate);
                                            tv_date.setText(requestdate);
                                            tv_date2.setText(requestdate2);

                                        } catch (java.text.ParseException e) {
                                            e.printStackTrace();
                                        }

                                        try {
                                            SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");
                                            SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm a");
                                            Date time1 = sdf1.parse(time.format(d));
                                            Date time2 = sdf1.parse(time.format(d2));
                                            String requesttime = sdf2.format(time1);
                                            String requesttime2 = sdf2.format(time2);
                                            System.out.println("MYTIME" + sdf2.format(time1));

                                            tv_time.setText(requesttime);
                                            tv_time2.setText(requesttime2);
                                        } catch (java.text.ParseException e) {
                                            e.printStackTrace();
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }

                            } else {
                                message = j.getString("message");
                                AlertDialog.Builder builder = new AlertDialog.Builder(SuccessfultransActivity.this);
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
                        String reason = AppUtils.getVolleyError(SuccessfultransActivity.this, error);
                        AlertUtility.showAlert(SuccessfultransActivity.this, reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("request_id", request_id);
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

    private void submitReviewRating() {
        loadingDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_SUBREVIEWRATING,
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

                                AlertDialog.Builder builder = new AlertDialog.Builder(SuccessfultransActivity.this);
                                builder.setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                onBackPressed();
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();

                            } else {
                                message = j.getString("message");
                                AlertDialog.Builder builder = new AlertDialog.Builder(SuccessfultransActivity.this);
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
                        String reason = AppUtils.getVolleyError(SuccessfultransActivity.this, error);
                        AlertUtility.showAlert(SuccessfultransActivity.this, reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("user_id", user_id);
                    params.put("agent_id", agentId);
                    params.put("rating_given", rating);
                    params.put("rating_comment", et_comment.getText().toString());


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
