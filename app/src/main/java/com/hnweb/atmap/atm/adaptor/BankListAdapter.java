package com.hnweb.atmap.atm.adaptor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ParseException;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.hnweb.atmap.atm.activity.RequestMoneyDetailsActivity;
import com.hnweb.atmap.atm.bo.AgentBank;
import com.hnweb.atmap.atm.bo.User;
import com.hnweb.atmap.contants.AppConstant;
import com.hnweb.atmap.inteface.OnCallBack;
import com.hnweb.atmap.utils.LoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;


public class BankListAdapter extends RecyclerView.Adapter<BankListAdapter.MyViewHolder> {

    private ArrayList<AgentBank> agentBanks;
    private Activity activity;
    LoadingDialog loadingDialog;
    SharedPreferences sharedPreferences;
    String user_id;
    OnCallBack onCallBack;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_bankName, tv_accNo;
        TextView tv_status;

        public MyViewHolder(View view, final List<AgentBank> agentBanks, final Activity activity) {
            super(view);
            tv_bankName = view.findViewById(R.id.tv_bankName);
            tv_accNo = view.findViewById(R.id.tv_accNo);
            tv_status = view.findViewById(R.id.tv_status);
            sharedPreferences = activity.getApplicationContext().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);
            user_id = sharedPreferences.getString(AppConstant.KEY_ID, null);


        }
    }


    public BankListAdapter(ArrayList<AgentBank> agentBanks, Activity activity, OnCallBack onCallBack) {
        this.agentBanks = agentBanks;
        this.activity = activity;
        this.loadingDialog = new LoadingDialog(activity);
        this.onCallBack = onCallBack;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adptor_banklist, parent, false);
        return new MyViewHolder(itemView, agentBanks, activity);
    }

    @SuppressLint({"NewApi", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        AgentBank agentBank = agentBanks.get(position);

        holder.tv_bankName.setText(agentBanks.get(position).getAgent_bank_name());
        holder.tv_accNo.setText(agentBanks.get(position).getAgent_acc_num());
        holder.tv_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                markAsDefault(agentBanks.get(position).getId().toString());
            }
        });
        if (agentBanks.get(position).getDefault_account().equalsIgnoreCase("1")) {
            holder.tv_status.setText("Default");

        }

        System.out.println("agentBanks" + agentBanks.get(position).getAgent_acc_num().substring(0, agentBanks.get(position).getAgent_acc_num().length() - 2));

        String new_word = agentBanks.get(position).getAgent_acc_num().substring(agentBanks.get(position).getAgent_acc_num().length() - 4);
        String substring2 = agentBanks.get(position).getAgent_acc_num().substring(0, agentBanks.get(position).getAgent_acc_num().length() - 4);

        Log.e("new_word", new_word);

        int len = substring2.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append('*');
        }
        Log.e("substring", sb.toString());

        String newbankaccoutn = sb.toString() + new_word;
        Log.e("newbankaccoutn", newbankaccoutn);
        holder.tv_accNo.setText(newbankaccoutn);
    }


    @Override
    public int getItemCount() {
        return agentBanks.size();

    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }


    private void markAsDefault(final String bankId) {
        loadingDialog.show();
        StringRequest postRequest = new StringRequest(Request.Method.POST, AppConstant.API_MARKASDEFAULT,
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

                                loadingDialog.dismiss();
                                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                                builder.setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

                                                onCallBack.refresh();
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            } else {
                                message = j.getString("message");
                                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
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
                params.put("bank_id", bankId);
                Log.e("Params", params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        postRequest.setShouldCache(false);
        requestQueue.add(postRequest);

    }

}
