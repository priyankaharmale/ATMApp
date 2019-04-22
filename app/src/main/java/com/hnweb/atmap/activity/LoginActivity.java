package com.hnweb.atmap.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import com.hnweb.atmap.R;
import com.hnweb.atmap.agent.activity.AgentHomeActivity;
import com.hnweb.atmap.contants.AppConstant;
import com.hnweb.atmap.user.activity.HomeActivity;
import com.hnweb.atmap.utils.AlertUtility;
import com.hnweb.atmap.utils.AppUtils;
import com.hnweb.atmap.utils.LoadingDialog;
import com.hnweb.atmap.utils.SharedPrefManager;
import com.hnweb.atmap.utils.Utils;
import com.hnweb.atmap.utils.Validations;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/* * Created by Priyanka H on 30/03/2019.
 */


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    Button btn_register;
    Button btn_login;
    EditText et_password, et_email;
    String deviceToken = "";
    LoadingDialog loadingDialog;
    SharedPreferences prefUser;
    SharedPreferences.Editor editorUser;
    TextView tv_forgotpwd;
    EditText et_emailId;
    Dialog dialog;
    String userType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getdeviceToken();
        loadingDialog = new LoadingDialog(this);
        btn_register = findViewById(R.id.btn_register);

        btn_login = findViewById(R.id.btn_login);
        et_password = ( EditText ) findViewById(R.id.et_password);
        tv_forgotpwd = findViewById(R.id.tv_forgotpwd);
        et_email = ( EditText ) findViewById(R.id.et_email);

        prefUser = getApplicationContext().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);
        editorUser = prefUser.edit();


        // get data via the key
        userType = prefUser.getString(AppConstant.KEY_USERTYPE, null);

        btn_register.setOnClickListener(this);
        tv_forgotpwd.setOnClickListener(this);
        btn_login.setOnClickListener(this);


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
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    private void login(final String email, final String password) {
        loadingDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_LOGIN_USER,
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
                                    JSONObject jsonObject = j.getJSONObject("details");

                                    String user_id = jsonObject.getString("customer_id");
                                    String user_name = jsonObject.getString("customer_name");
                                    String user_email = jsonObject.getString("customer_email");
                                    String user_phone = jsonObject.getString("customer_mobile");
                                    String user_type = jsonObject.getString("user_type");


                                    editorUser.putString(AppConstant.KEY_ID, user_id);
                                    editorUser.putString(AppConstant.KEY_NAME, user_name);
                                    editorUser.putString(AppConstant.KEY_EMAIL, user_email);
                                    editorUser.putString(AppConstant.KEY_PHONE, user_phone);
                                    editorUser.putString(AppConstant.KEY_USERTYPE, user_type);
                                    editorUser.commit();


                                    if (user_type.equalsIgnoreCase("1")) {

                                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                        builder.setMessage("Customer Login Successfully")
                                                .setCancelable(false)
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                                        startActivity(intent);
                                                        finish();

                                               /* Intent intent = new Intent(UserLoginActivity.this, HomeActivity.class);
                                                startActivity(intent);
                                                finish();*/
                                                        dialog.dismiss();
                                                    }
                                                });
                                        AlertDialog alert = builder.create();
                                        alert.show();

                                    } else {

                                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                        builder.setMessage("ATM Login Successfully")
                                                .setCancelable(false)
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {

                                                        Intent intent = new Intent(LoginActivity.this, AgentHomeActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                        dialog.dismiss();
                                                    }
                                                });
                                        AlertDialog alert = builder.create();
                                        alert.show();

                                    }


                                } catch (JSONException e) {
                                    System.out.println("jsonexeption" + e.toString());
                                }

                            } else {
                                message = j.getString("message");
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
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
                        String reason = AppUtils.getVolleyError(LoginActivity.this, error);
                        AlertUtility.showAlert(LoginActivity.this, reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put(AppConstant.KEY_EMAIL, email);
                    params.put("password", password);
                    params.put(AppConstant.KEY_DEVICETYPE, "Android");
                    if (deviceToken.equals("")) {
                        params.put(AppConstant.KEY_DEVICETOKEN, "");
                    } else {
                        params.put(AppConstant.KEY_DEVICETOKEN, deviceToken);
                    }

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

    public void dialogforgotPwd() {
        dialog = new Dialog(LoginActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.dialog_forgotpwd);
        //   final EditText et_exprydate = (EditText) dialog.findViewById(R.id.et_exprydate);
        TextView tv_submit = ( TextView ) dialog.findViewById(R.id.tv_submit);
        TextView tv_cancle = ( TextView ) dialog.findViewById(R.id.tv_cancle);
        et_emailId = ( EditText ) dialog.findViewById(R.id.et_emailId);

        dialog.show();
        dialog.setCancelable(true);

        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        tv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkValidation()) {
                    if (Utils.isNetworkAvailable(LoginActivity.this)) {
                        forgotpwd(et_emailId.getText().toString());
                    } else {
                        Utils.myToast1(LoginActivity.this);
                    }
                }

            }
        });

    }

    private boolean checkValidation() {
        boolean ret = true;
        if (!Validations.hasText(et_emailId, "Please Enter Email ID "))
            ret = false;

        if (!Validations.isEmailAddress(et_emailId, true, "Please Enter Valid Email ID"))
            ret = false;
        return ret;

    }

    private void forgotpwd(final String email) {
        loadingDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_FORGOTPWD_USER,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        System.out.println("res_register" + response);
                        try {
                            JSONObject j = new JSONObject(response);
                            int message_code = j.getInt("message_code");
                            String message = j.getString("message");

                            if (loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }
                            if (message_code == 1) {

                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog1, int id) {

                                                dialog1.dismiss();
                                                dialog.dismiss();
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            } else {
                                message = j.getString("message");
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
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
                        String reason = AppUtils.getVolleyError(LoginActivity.this, error);
                        AlertUtility.showAlert(LoginActivity.this, reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put(AppConstant.KEY_EMAIL, email);

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
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {


            case R.id.btn_register:
                intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_forgotpwd:
                dialogforgotPwd();
                break;
            case R.id.btn_login:
                if (checkValidation1()) {

                    if (Utils.isNetworkAvailable(LoginActivity.this)) {

                        String password = et_password.getText().toString();
                        String email = et_email.getText().toString();

                        login(email, password);


                    } else {
                        Utils.myToast1(LoginActivity.this);
                    }
                }
                break;

            default:
                return;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }


    private boolean checkValidation1() {
        boolean ret = true;
        if (!Validations.hasText(et_email, "Please Enter Email ID "))
            ret = false;
        if (!Validations.isEmailAddress(et_email, true, "Please Enter Valid Email ID"))
            ret = false;
        if (!Validations.hasText(et_password, "Please Enter RE-Enter Password"))
            ret = false;
        if (!Validations.check_text_length_7_text_layout(et_password, "Password atleast 7 characters"))
            ret = false;
        return ret;
    }


}
