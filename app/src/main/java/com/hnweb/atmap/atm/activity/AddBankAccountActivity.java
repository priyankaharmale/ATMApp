package com.hnweb.atmap.atm.activity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hnweb.atmap.R;
import com.hnweb.atmap.contants.AppConstant;
import com.hnweb.atmap.utils.LoadingDialog;
import com.hnweb.atmap.utils.Validations;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddBankAccountActivity extends AppCompatActivity {
    EditText et_dob, et_bankname, et_accountNo, et_ssn, et_routerNo, et_businessnmae;
    String bankname, accountNo, ssn, routerNo;
    private int mYear, mMonth, mDay;
    LoadingDialog loadingDialog;
    Button btn_submit;
    String str_dob, user_id;
    ImageView iv_back;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addaccount);
        et_bankname = findViewById(R.id.et_bankname);
        et_accountNo = findViewById(R.id.et_accountNo);
        et_ssn = findViewById(R.id.et_ssn);
        et_routerNo = findViewById(R.id.et_routerNo);
        et_businessnmae = findViewById(R.id.et_businessnmae);
        et_dob = findViewById(R.id.et_dob);
        btn_submit = findViewById(R.id.btn_submit);
        iv_back=findViewById(R.id.iv_back);
        loadingDialog = new LoadingDialog(this);

        sharedPreferences = getApplicationContext().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);
        user_id = sharedPreferences.getString(AppConstant.KEY_ID, null);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkValidation()) {
                    addAccount();
                }

            }
        });
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        et_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDOBPicker();
            }
        });

    }

    private boolean checkValidation() {
        boolean ret = true;

        if (!Validations.hasText(et_dob, "Please Select Date of Birth"))
            ret = false;
        if (!Validations.hasText(et_bankname, "Please Enter Bank Name"))
            ret = false;
        if (!Validations.hasText(et_accountNo, "Please Enter Account Number"))
            ret = false;
        if (!Validations.hasText(et_ssn, "Please Enter Social Security Number"))
            ret = false;
        if (!Validations.hasText(et_routerNo, "Please Router Number"))
            ret = false;

        return ret;
    }


    private void addAccount() {
        loadingDialog.show();
        StringRequest stringRequest;
        RequestQueue queue = Volley.newRequestQueue(AddBankAccountActivity.this);
        String url = AppConstant.API_ADDBANKACCOUNT;

        stringRequest = new StringRequest(Request.Method.POST, url,
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
                                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(AddBankAccountActivity.this);
                                builder.setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

                                                //  getUserDetails();
                                                onBackPressed();

                                            }
                                        });
                                android.support.v7.app.AlertDialog alert = builder.create();
                                alert.show();
                            } else {
                                message = j.getString("message");
                                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(AddBankAccountActivity.this);
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
                System.out.println("VolleyError" + error.toString());
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    }

                    Toast.makeText(AddBankAccountActivity.this, errorMessage, Toast.LENGTH_LONG).show();

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


                Toast.makeText(AddBankAccountActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> Stringparams = new HashMap<String, String>();


                bankname = et_bankname.getText().toString();
                accountNo = et_accountNo.getText().toString();
                routerNo = et_routerNo.getText().toString();
                ssn = et_ssn.getText().toString();

                Stringparams.put("dob", str_dob);
                Stringparams.put("bank_name", bankname);
                Stringparams.put("bank_account_no", accountNo);
                Stringparams.put("router_no", routerNo);
                Stringparams.put("ssn_no", ssn);
                Stringparams.put("agent_id", user_id);
                Log.e("params", Stringparams.toString());

                // System.out.println(params);
                return Stringparams;
            }
        };
        queue.add(stringRequest);
        int MY_SOCKET_TIMEOUT_MS = 50000;
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void getDOBPicker() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(AddBankAccountActivity.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        String list_of_count = String.format("%02d", (monthOfYear + 1));

                        String date = dayOfMonth + "-" + list_of_count + "-" + year;
                        str_dob = year + "/" + list_of_count + "/" + dayOfMonth;


                        Log.e("DateFormatChange", str_dob);
                        et_dob.setText(str_dob);


                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
        // datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() + 1000);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());


    }
}
