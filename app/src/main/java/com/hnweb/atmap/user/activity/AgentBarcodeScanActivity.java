package com.hnweb.atmap.user.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.hnweb.atmap.R;
import com.hnweb.atmap.contants.AppConstant;
import com.hnweb.atmap.utils.AlertUtility;
import com.hnweb.atmap.utils.AppUtils;
import com.hnweb.atmap.utils.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AgentBarcodeScanActivity extends AppCompatActivity {
    ImageView iv_barcode, iv_dollor, iv_share, iv_back;
    String barcode, businessName, address, request_id, agentId;
    String image, user_id;
    Bitmap ImageBitmap;
    Button btn_cancletrans;
    LoadingDialog loadingDialog;
    TextView tv_hotelname, tv_address, tv_barcodeNo;
    SharedPreferences prefs;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startwithdraw);
        prefs = getApplicationContext().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);
        user_id = prefs.getString(AppConstant.KEY_ID, null);

        loadingDialog = new LoadingDialog(this);
        Intent intent = getIntent();
        barcode = intent.getStringExtra("barcode");
        image = intent.getStringExtra("image");
        address = intent.getStringExtra("address");
        businessName = intent.getStringExtra("businessName");
        request_id = intent.getStringExtra("request_id");
        agentId = intent.getStringExtra("agentId");
        iv_share = findViewById(R.id.iv_share);
        iv_barcode = findViewById(R.id.iv_barcode);
        iv_dollor = findViewById(R.id.iv_dollor);
        tv_address = findViewById(R.id.tv_address);
        tv_barcodeNo = findViewById(R.id.tv_barcodeNo);
        tv_hotelname = findViewById(R.id.tv_hotelname);
        btn_cancletrans = findViewById(R.id.btn_cancletrans);
        iv_dollor.setImageResource(Integer.parseInt(image));
        iv_back = findViewById(R.id.iv_back);

        tv_barcodeNo.setText(barcode);
        tv_address.setText(address);
        tv_hotelname.setText(businessName);

        iv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareIt();
            }
        });

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(AgentBarcodeScanActivity.this);
                builder.setMessage("Are you sure you want to cancle the transaction?")
                        .setCancelable(false).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                cancelTranscation(request_id);


                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        final Handler refreshHandler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                refreshHandler.postDelayed(this, 1800 * 1000);
                if (!(AgentBarcodeScanActivity.this).isFinishing()) {
                    cancelTranscation(request_id);
                }

            }
        };
        refreshHandler.postDelayed(runnable, 1800 * 1000);

        final Handler refreshHandler2 = new Handler();
        Runnable runnable1 = new Runnable() {
            @Override
            public void run() {
                refreshHandler2.postDelayed(this, 20 * 1000);
                if (!(AgentBarcodeScanActivity.this).isFinishing()) {
                    successTranscation(request_id);
                }

            }
        };
        refreshHandler2.postDelayed(runnable1, 20 * 1000);

        generate(barcode, iv_barcode);
    }

    private void generate(String palate_id, ImageView qrcode) {
        MultiFormatWriter writer = new MultiFormatWriter();
        String finaldata = Uri.encode(palate_id, "utf-8");

        BitMatrix bm = null;
        try {
            bm = writer.encode(finaldata, BarcodeFormat.CODE_128, 150, 150);

        } catch (WriterException e) {
            e.printStackTrace();
        }
        ImageBitmap = Bitmap.createBitmap(180, 60, Bitmap.Config.ARGB_8888);

        for (int i = 0; i < 180; i++) {//width
            for (int j = 0; j < 60; j++) {//height
                ImageBitmap.setPixel(i, j, bm.get(i, j) ? Color.BLACK : Color.WHITE);
            }
        }

        if (ImageBitmap != null) {
            qrcode.setImageBitmap(ImageBitmap);
        } else {
            Toast.makeText(this, "Error",
                    Toast.LENGTH_SHORT).show();
        }

        btn_cancletrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AgentBarcodeScanActivity.this);
                builder.setMessage("Are you sure you want to cancle the transaction?")
                        .setCancelable(false).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                cancelTranscation(request_id);


                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        iv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }


    private void cancelTranscation(final String request_id) {
        loadingDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_CANCLE_WITHDRAW,
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

                                AlertDialog.Builder builder = new AlertDialog.Builder(AgentBarcodeScanActivity.this);
                                builder.setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                //onBackPressed();
                                                Intent intent = new Intent(AgentBarcodeScanActivity.this, AgentDetailsActivity.class);
                                                intent.putExtra("agentId", agentId);
                                                startActivity(intent);
                                                finish();
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();

                            } else {
                                message = j.getString("message");
                                AlertDialog.Builder builder = new AlertDialog.Builder(AgentBarcodeScanActivity.this);
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
                        String reason = AppUtils.getVolleyError(AgentBarcodeScanActivity.this, error);
                        AlertUtility.showAlert(AgentBarcodeScanActivity.this, reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("request_id", request_id);

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
                                message = j.getString("message");
                                Intent intent = new Intent(AgentBarcodeScanActivity.this, SuccessfultransActivity.class);
                                intent.putExtra("request_id", request_id);
                                intent.putExtra("agentId", agentId);
                                startActivity(intent);
                                finish();
                              /*  AlertDialog.Builder builder = new AlertDialog.Builder(AgentBarcodeScanActivity.this);
                                builder.setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();*/

                            } else {
                                message = j.getString("message");
                                AlertDialog.Builder builder = new AlertDialog.Builder(AgentBarcodeScanActivity.this);
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
                        String reason = AppUtils.getVolleyError(AgentBarcodeScanActivity.this, error);
                        AlertUtility.showAlert(AgentBarcodeScanActivity.this, reason);
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

    private void shareIt() {
//sharing implementation here
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "ATM App");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Click here to Download the App \n ");
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    @Override
    public void onBackPressed() {


        AlertDialog.Builder builder = new AlertDialog.Builder(AgentBarcodeScanActivity.this);
        builder.setMessage("Are you sure you want to cancle the transaction?")
                .setCancelable(false).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        cancelTranscation(request_id);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }
}
