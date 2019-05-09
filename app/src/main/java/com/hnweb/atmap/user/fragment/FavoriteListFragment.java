package com.hnweb.atmap.user.fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import com.hnweb.atmap.user.adaptor.FavoriteAdapter;
import com.hnweb.atmap.utils.LoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class FavoriteListFragment extends Fragment {
    RecyclerView recyclerView;
    LoadingDialog loadingDialog;
    ArrayList<User> users;
    SharedPreferences prefs;
    String user_id;
    FavoriteAdapter requestMoneyAdapter;
    TextView tv_header;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_requestmoney, container, false);
        recyclerView = view.findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        loadingDialog = new LoadingDialog(getActivity());
        tv_header=view.findViewById(R.id.tv_header);
        tv_header.setText("Favourite");

        prefs = getActivity().getApplicationContext().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);
        user_id = prefs.getString(AppConstant.KEY_ID, null);
        getFavoList();
        return view;
    }

    private void getFavoList() {
        loadingDialog.show();
        StringRequest postRequest = new StringRequest(Request.Method.POST, AppConstant.API_FAVLIST,
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
                                        String userPic_url = jsonObjectpostion.getString("customer_profile_pic");

                                      /*  agent.setCustomer_lat(jsonObjectpostion.getString("customer_lat"));
                                        agent.setCustomer_long(jsonObjectpostion.getString("customer_long"));
                                     */   agent.setCustomer_address(jsonObjectpostion.getString("customer_address"));
                                        users.add(agent);
                                    }
                                    requestMoneyAdapter = new FavoriteAdapter(users, getActivity());
                                    recyclerView.setAdapter(requestMoneyAdapter);


                                } catch (JSONException e) {
                                    System.out.println("jsonexeption" + e.toString());
                                } catch (Exception e) {
                                    System.out.println("jsonexeption" + e.toString());
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
                params.put("user_id", user_id);
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
