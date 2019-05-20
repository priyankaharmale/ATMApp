package com.hnweb.atmap.user.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
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
import com.hnweb.atmap.contants.AppConstant;
import com.hnweb.atmap.inteface.OnCallBack;
import com.hnweb.atmap.user.adaptor.MonthAdaptor;
import com.hnweb.atmap.user.adaptor.YearAdaptor;
import com.hnweb.atmap.utils.LoadingDialog;
import com.hnweb.atmap.utils.Utils;
import com.hnweb.atmap.utils.Validations;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class UserAddBankAccountFragment extends Fragment implements OnCallBack, View.OnClickListener {
    EditText et_cardNumber, et_cvv, et_expiryyear, et_expirymonth;
    Button btn_submit;
    OnCallBack onCallBack;
    LoadingDialog loadingDialog;
    SharedPreferences prefs;
    String user_id, user_email;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_userbank, container, false);

        prefs = getActivity().getApplicationContext().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);
        user_id = prefs.getString(AppConstant.KEY_ID, null);
        user_email = prefs.getString(AppConstant.KEY_EMAIL, null);

        et_cardNumber = view.findViewById(R.id.et_cardNumber);
        et_cvv = view.findViewById(R.id.et_cvv);
        et_expiryyear = view.findViewById(R.id.et_expiryyear);
        et_expirymonth = view.findViewById(R.id.et_expirymonth);
        btn_submit = view.findViewById(R.id.btn_submit);
        et_expiryyear.setOnClickListener(this);
        et_expirymonth.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
        onCallBack = this;
        loadingDialog = new LoadingDialog(getActivity());
        et_cardNumber.addTextChangedListener(new TextWatcher() {
            private static final char space = ' ';
            boolean isDelete = true;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (before == 0)
                    isDelete = false;
                else
                    isDelete = true;
            }

            @Override
            public void afterTextChanged(Editable editable) {

                String source = editable.toString();
                int length = source.length();

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(source);

                if (length > 0 && length % 5 == 0) {
                    if (isDelete)
                        stringBuilder.deleteCharAt(length - 1);
                    else
                        stringBuilder.insert(length - 1, " ");

                    et_cardNumber.setText(stringBuilder);
                    et_cardNumber.setSelection(et_cardNumber.getText().length());
                }
            }
        });
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_expiryyear:
                dialogYear();
                break;
            case R.id.et_expirymonth:
                dialogMonth();
                break;
            case R.id.btn_submit:
                if (checkValidation()) {
                    if (Utils.isNetworkAvailable(getActivity())) {
                        addBankAcc();

                    } else {
                        Utils.myToast1(getActivity());
                    }
                }
                break;

        }

    }

    @Override
    public void selctedImge(String amount, String image) {

    }

    @Override
    public void callbackYear(String count) {
        et_expiryyear.setText(count);
    }

    @Override
    public void callbackMonthe(String month) {
        et_expirymonth.setText(month);
    }

    @Override
    public void refresh() {

    }

    private boolean checkValidation() {
        boolean ret = true;


        if (!Validations.hasText(et_cardNumber, "Please Enter Card Number"))
            ret = false;
        if (!Validations.hasText(et_expirymonth, "Please Select Expiry Month"))
            ret = false;
        if (!Validations.hasText(et_expiryyear, "Please Select Expiry Year"))
            ret = false;
        if (!Validations.hasText(et_cvv, "Please Enter CVV"))
            ret = false;

        return ret;
    }

    public void dialogYear() {
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_month);
        ListView lv = (ListView) dialog.findViewById(R.id.lv);
        dialog.setCancelable(true);
        dialog.setTitle("Year");
        ArrayList<String> years = new ArrayList<String>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = thisYear; i <= 2060; i++) {
            years.add(Integer.toString(i));
        }
        YearAdaptor adapter = new YearAdaptor(getActivity(), years, onCallBack, dialog);
        lv.setAdapter(adapter);

        dialog.show();
    }

    public void dialogMonth() {
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_month);
        ListView lv = (ListView) dialog.findViewById(R.id.lv);
        dialog.setCancelable(true);
        dialog.setTitle("Month");
        ArrayList<String> years = new ArrayList<String>();
        years.add("01");
        years.add("02");
        years.add("03");
        years.add("04");
        years.add("05");
        years.add("06");
        years.add("07");
        years.add("08");
        years.add("09");
        years.add("10");
        years.add("11");
        years.add("12");
        MonthAdaptor adapter = new MonthAdaptor(getActivity(), years, onCallBack, dialog);
        lv.setAdapter(adapter);
        dialog.show();
    }

    private void addBankAcc() {

        loadingDialog.show();
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = AppConstant.API_ADDUSERBANKACC;

        StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("responseprofile", response);
                        try {

                            JSONObject json = new JSONObject(response);

                            String message = json.getString("message");
                            int msg = json.getInt("message_code");

                            if (msg == 1) {
                                loadingDialog.dismiss();

                                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
                                builder.setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                            }
                                        });
                                android.support.v7.app.AlertDialog alert = builder.create();
                                alert.show();

                            } else {
                                loadingDialog.dismiss();
                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMessage = "Unknown error";

                Log.i("Error", errorMessage);
                error.printStackTrace();

                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", user_id);
                params.put("email", user_email);
                params.put("cardnumber", et_cardNumber.getText().toString());
                params.put("exp_month", et_expirymonth.getText().toString());
                params.put("exp_year", et_expiryyear.getText().toString());
                params.put("cvc", et_cvv.getText().toString());

                return params;
            }
        };

        queue.add(strRequest);
    }

}
