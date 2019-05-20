package com.hnweb.atmap.atm.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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

import java.util.HashMap;
import java.util.Map;

public class RequestMoneyDetailsActivity extends AppCompatActivity {
    TextView tv_user_name, tv_requesttime, tv_requestdate;
    String name, requesttime, requestdate, request_id, user_id,request_user_id;
    Toolbar toolbar;
    ImageView iv_back;
    EditText et_barcode;
    LoadingDialog loadingDialog;
    Button btn_scan, btn_confirm;
    SharedPreferences prefs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requestmoney_details);
        tv_user_name = findViewById(R.id.tv_user_name);
        tv_requesttime = findViewById(R.id.tv_requesttime);
        tv_requestdate = findViewById(R.id.tv_requestdate);
        et_barcode = findViewById(R.id.et_barcode);
        btn_confirm = findViewById(R.id.btn_confirm);
        toolbar = findViewById(R.id.toolbar);
        iv_back = toolbar.findViewById(R.id.iv_back);
        btn_scan = findViewById(R.id.btn_scan);
        loadingDialog = new LoadingDialog(this);
        final Intent intent = getIntent();
        name = intent.getStringExtra("name");
        requesttime = intent.getStringExtra("requesttime");
        requestdate = intent.getStringExtra("requestdate");
        request_id = intent.getStringExtra("request_id");
        request_user_id=intent.getStringExtra("request_user_id");
        tv_user_name.setText(name);
        tv_requesttime.setText(requesttime);
        tv_requestdate.setText(requestdate);
        prefs = getApplicationContext().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);
        user_id = prefs.getString(AppConstant.KEY_ID, null);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(RequestMoneyDetailsActivity.this, ScannedBarcodeActivity.class);
                intent1.putExtra("request_id", request_id);
                intent1.putExtra("request_user_id",request_user_id);
                startActivity(intent1);
            }
        });
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (et_barcode.getText().toString().length() == 0) {
                    et_barcode.setError("Please Enter the barcode number");
                } else {
                    sendnBarcode(et_barcode.getText().toString());
                }
            }
        });

    }


    private void sendnBarcode(final String barcode) {
        loadingDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_SCANBARCODE,
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
                                AlertDialog.Builder builder = new AlertDialog.Builder(RequestMoneyDetailsActivity.this);
                                builder.setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                Intent intent = new Intent(RequestMoneyDetailsActivity.this, AgentHomeActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();

                            } else {
                                message = j.getString("message");
                                AlertDialog.Builder builder = new AlertDialog.Builder(RequestMoneyDetailsActivity.this);
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
                        String reason = AppUtils.getVolleyError(RequestMoneyDetailsActivity.this, error);
                        AlertUtility.showAlert(RequestMoneyDetailsActivity.this, reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("barcode_id", barcode);
                    params.put("request_id", request_id);
                    params.put("id", request_user_id);

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
