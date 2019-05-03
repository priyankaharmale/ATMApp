package com.hnweb.atmap.atm.fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import com.hnweb.atmap.atm.activity.AddBankAccountActivity;
import com.hnweb.atmap.atm.activity.RequestMoneyDetailsActivity;
import com.hnweb.atmap.atm.adaptor.BankListAdapter;
import com.hnweb.atmap.atm.adaptor.RequestMoneyAdapter;
import com.hnweb.atmap.atm.bo.AgentBank;
import com.hnweb.atmap.atm.bo.User;
import com.hnweb.atmap.contants.AppConstant;
import com.hnweb.atmap.inteface.OnCallBack;
import com.hnweb.atmap.utils.LoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment implements OnCallBack {

    RecyclerView recyclerView;
    LinearLayout ll_addacount;
    LoadingDialog loadingDialog;
    BankListAdapter bankListAdapter;
    ArrayList<AgentBank> agentBanks;

    SharedPreferences sharedPreferences;
    String user_id;
    OnCallBack onCallBack;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bankaccount, container, false);

        recyclerView = view.findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        loadingDialog = new LoadingDialog(getActivity());
        ll_addacount = view.findViewById(R.id.ll_addacount);
        sharedPreferences = getActivity().getApplicationContext().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);
        user_id = sharedPreferences.getString(AppConstant.KEY_ID, null);
        onCallBack=this;
        ll_addacount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddBankAccountActivity.class);
                startActivity(intent);

            }
        });
        getBankList();
        return view;
    }


    private void getBankList() {
        loadingDialog.show();
        StringRequest postRequest = new StringRequest(Request.Method.POST, AppConstant.API_AGENT_BANKLIST,
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
                                final JSONArray jsonArrayRow = j.getJSONArray("details");
                                loadingDialog.dismiss();
                                agentBanks = new ArrayList<AgentBank>();
                                try {
                                    for (int k = 0; k < jsonArrayRow.length(); k++) {
                                        AgentBank agent = new AgentBank();
                                        JSONObject jsonObjectpostion = jsonArrayRow.getJSONObject(k);
                                        agent.setId(jsonObjectpostion.getString("id"));
                                        agent.setAgent_id(jsonObjectpostion.getString("agent_id"));
                                        agent.setAgent_bank_name(jsonObjectpostion.getString("agent_bank_name"));
                                        agent.setAgent_acc_num(jsonObjectpostion.getString("agent_acc_num"));
                                        agent.setAgent_ssn(jsonObjectpostion.getString("agent_ssn"));
                                        agent.setAgent_router_number(jsonObjectpostion.getString("agent_router_number"));
                                        agent.setStripe_acc_id(jsonObjectpostion.getString("stripe_acc_id"));
                                        agent.setDefault_account(jsonObjectpostion.getString("default_account"));

                                        agentBanks.add(agent);
                                    }
                                    System.out.println("jsonobk" + jsonArrayRow);
                                    System.out.println("agentArrayList size." + agentBanks.size());
                                    bankListAdapter = new BankListAdapter(agentBanks, getActivity(),onCallBack);
                                    recyclerView.setAdapter(bankListAdapter);


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
                params.put("agent_id", user_id);
                Log.e("Params", params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        postRequest.setShouldCache(false);
        requestQueue.add(postRequest);

    }

    @Override
    public void selctedImge(String amount, String image) {

    }

    @Override
    public void callbackYear(String count) {

    }

    @Override
    public void callbackMonthe(String month) {

    }

    @Override
    public void refresh() {
        getBankList();
    }
}
