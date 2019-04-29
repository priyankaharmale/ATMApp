package com.hnweb.atmap.user.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
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
    ImageView iv_barcode, iv_dollor;
    String barcode, businessName, address, request_id;
    String image;
    Bitmap ImageBitmap;
    Button btn_cancletrans;
    LoadingDialog loadingDialog;
    TextView tv_hotelname, tv_address, tv_barcodeNo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startwithdraw);

        loadingDialog = new LoadingDialog(this);
        Intent intent = getIntent();
        barcode = intent.getStringExtra("barcode");
        image = intent.getStringExtra("image");
        address = intent.getStringExtra("address");
        businessName = intent.getStringExtra("businessName");
        request_id = intent.getStringExtra("request_id");

        iv_barcode = findViewById(R.id.iv_barcode);
        iv_dollor = findViewById(R.id.iv_dollor);
        tv_address = findViewById(R.id.tv_address);
        tv_barcodeNo = findViewById(R.id.tv_barcodeNo);
        tv_hotelname = findViewById(R.id.tv_hotelname);
        btn_cancletrans = findViewById(R.id.btn_cancletrans);
        iv_dollor.setImageResource(Integer.parseInt(image));

        tv_barcodeNo.setText(barcode);
        tv_address.setText(address);
        tv_hotelname.setText(businessName);

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
                                                onBackPressed();
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

}