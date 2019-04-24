package com.hnweb.atmap.atm.fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hnweb.atmap.R;
import com.hnweb.atmap.atm.adaptor.RequestMoneyAdapter;
import com.hnweb.atmap.atm.bo.User;
import com.hnweb.atmap.contants.AppConstant;
import com.hnweb.atmap.user.bo.Agent;
import com.hnweb.atmap.utils.LoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class RequestMoneyFragment extends Fragment {
    RecyclerView recyclerView;
    LoadingDialog loadingDialog;
    ArrayList<User> users;
    SharedPreferences prefs;
    String user_id;
    RequestMoneyAdapter requestMoneyAdapter;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_requestmoney, container, false);
        recyclerView = view.findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        loadingDialog = new LoadingDialog(getActivity());
        prefs = getActivity().getApplicationContext().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);
        user_id = prefs.getString(AppConstant.KEY_ID, null);
        getRequestMoneyList();
        return view;
    }

    private void getRequestMoneyList() {
        loadingDialog.show();
        StringRequest postRequest = new StringRequest(Request.Method.POST, AppConstant.API_REQUESTMONEYLIST,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        if (loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }

                        Log.i("Response", "ServiceList= " + response);

                        try {
                            JSONObject j = new JSONObject(response);
                            int message_code = j.getInt("message_code");
                            String message = j.getString("message");
                            if (message_code == 1) {
                                final JSONArray jsonArrayRow = j.getJSONArray("detail");
                                loadingDialog.dismiss();
                                users = new ArrayList<User>();
                                try {
                                    for (int k = 0; k < jsonArrayRow.length(); k++) {
                                        User agent = new User();
                                        JSONObject jsonObjectpostion = jsonArrayRow.getJSONObject(k);
                                        agent.setCustomer_name(jsonObjectpostion.getString("customer_name"));
                                        agent.setCustomer_lat(jsonObjectpostion.getString("customer_lat"));
                                        agent.setRequest_user_id(jsonObjectpostion.getString("request_user_id"));
                                        agent.setCustomer_long(jsonObjectpostion.getString("customer_long"));
                                        agent.setCustomer_address(jsonObjectpostion.getString("customer_address"));
                                        agent.setRequest_amount(jsonObjectpostion.getString("request_amount"));
                                        agent.setRequest_status(jsonObjectpostion.getString("request_status"));
                                        agent.setRequest_barcode(jsonObjectpostion.getString("request_barcode"));
                                        agent.setRequest_time(jsonObjectpostion.getString("request_time"));
                                        users.add(agent);
                                    }
                                    System.out.println("jsonobk" + jsonArrayRow);
                                    System.out.println("agentArrayList size." + users.size());
                                    requestMoneyAdapter = new RequestMoneyAdapter(users, getActivity());
                                    recyclerView.setAdapter(requestMoneyAdapter);


                                } catch (JSONException e) {
                                    System.out.println("jsonexeption" + e.toString());
                                } catch (Exception e) {

                                }
                            } else {
                                message = j.getString("message");
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                            if (loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }


                        } catch (JSONException e) {
                            System.out.println("jsonexeption" + e.toString());
                        } catch (Exception e) {

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
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
                params.put("id", user_id);
                Log.e("Params", params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        postRequest.setShouldCache(false);
        requestQueue.add(postRequest);

    }

}
