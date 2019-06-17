package com.hnweb.atmap.atm.fragment;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hnweb.atmap.R;
import com.hnweb.atmap.atm.activity.AgentHomeActivity;
import com.hnweb.atmap.atm.adaptor.NotificationListAdapter;
import com.hnweb.atmap.atm.bo.NotificationModel;
import com.hnweb.atmap.contants.AppConstant;
import com.hnweb.atmap.user.activity.HomeActivity;
import com.hnweb.atmap.utils.ConnectionDetector;
import com.hnweb.atmap.utils.LoadingDialog;
import com.hnweb.atmap.utils.NotificationUpdateModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class NotificationFragment extends Fragment {

    Toolbar toolbar;

    LoadingDialog loadingDialog;
    ConnectionDetector connectionDetector;
    SharedPreferences prefs;
    String user_id, userType;
    ArrayList<NotificationModel> notificationModels = new ArrayList<NotificationModel>();
    NotificationListAdapter notificationListAdapter;
    RecyclerView recyclerViewListChat;
    TextView textViewEmpty;
    SharedPreferences.Editor editorUser;
    String callfrom;
   /* @Override
    public void onResume() {
        super.onResume();
        getMessageList();
    }*/

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notificationlist, container, false);
        //toolbar = ((MainActivityUser) getActivity()).toolbar;
        //toolbar.setTitle("MESSAGES");
        callfrom = getArguments().getString("callfrom");
        prefs = getActivity().getApplicationContext().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);
        user_id = prefs.getString(AppConstant.KEY_ID, null);

        initViewById(view);

        editorUser = prefs.edit();
        userType = prefs.getString(AppConstant.KEY_USERTYPE, null);


        connectionDetector = new ConnectionDetector(getActivity());
        loadingDialog = new LoadingDialog(getActivity());

        if (connectionDetector.isConnectingToInternet()) {
            getNotificationList();
            getNotificationMarkAsRead();
        } else {
          /*  Snackbar snackbar = Snackbar
                    .make(((MainActivityUser) getActivity()).coordinatorLayout, "No Internet Connection, Please try Again!!", Snackbar.LENGTH_LONG);

            snackbar.show();*/
            Toast.makeText(getActivity(), "No Internet Connection, Please try Again!!", Toast.LENGTH_SHORT).show();
        }


        return view;

    }


    private void getNotificationMarkAsRead() {
        StringRequest postRequest = new StringRequest(Request.Method.POST, AppConstant.API_NOTIFICATIONMARKASREAD,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d("MarkAsRead", response);
                        if (loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int message_code = jsonObject.getInt("message_code");
                            String msg = jsonObject.getString("message");
                            Log.d("message_code:- ", String.valueOf(message_code));

                            if (callfrom.equalsIgnoreCase("1")) {
                                ((HomeActivity) getActivity())
                                        .notificationStateChanged();
                            } else {
                                ((AgentHomeActivity) getActivity())
                                        .notificationStateChanged();
                            }


                            if (message_code == 1) {
                                NotificationUpdateModel.getInstance().setmState(true);
                            } else {
                                NotificationUpdateModel.getInstance().setmState(true);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
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
                Log.e("MarkAsRead", params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        postRequest.setShouldCache(false);
        requestQueue.add(postRequest);
    }


    private void initViewById(View view) {
        recyclerViewListChat = view.findViewById(R.id.recycler_view_message_chat_list);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerViewListChat.setLayoutManager(layoutManager);

        textViewEmpty = view.findViewById(R.id.textView_empty);
    }

    private void getNotificationList() {
        loadingDialog.show();
        String url;
        if (userType.equalsIgnoreCase("1")) {
            url = AppConstant.API_NOTIFICATIONLISTINGUSER;
        } else {
            url = AppConstant.API_NOTIFICATIONLISTINGAGENT;
        }

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d("UpdateFacilityList", response);
                        if (loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int message_code = jsonObject.getInt("message_code");
                            String msg = jsonObject.getString("message");
                            Log.d("message_code:- ", String.valueOf(message_code));
                            if (message_code == 1) {
                                recyclerViewListChat.setVisibility(View.VISIBLE);
                                JSONArray jsonArray = jsonObject.getJSONArray("list");
                                notificationModels.clear();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObjectDetails = jsonArray.getJSONObject(i);

                                    NotificationModel notificationModel = new NotificationModel();
                                    notificationModel.setName(jsonObjectDetails.getString("name"));
                                    notificationModel.setRequest_id(jsonObjectDetails.getString("request_id"));
                                    notificationModel.setRequest_agent_id(jsonObjectDetails.getString("request_agent_id"));
                                    notificationModel.setRequest_user_id(jsonObjectDetails.getString("request_user_id"));
                                    notificationModel.setRequest_amount(jsonObjectDetails.getString("request_amount"));
                                    notificationModel.setRequest_barcode(jsonObjectDetails.getString("request_barcode"));
                                    notificationModel.setRequest_status(jsonObjectDetails.getString("request_status"));
                                    notificationModel.setRequest_time(jsonObjectDetails.getString("request_time"));
                                    notificationModel.setRequest_approve_time(jsonObjectDetails.getString("request_approve_time"));

                                    notificationModels.add(notificationModel);

                                }

                                notificationListAdapter = new NotificationListAdapter(getActivity(), notificationModels);
                                recyclerViewListChat.setAdapter(notificationListAdapter);
                                textViewEmpty.setVisibility(View.GONE);
                            } else {
                                //Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                                //Utils.myToast(MessageChatActivity.this,"");
                                textViewEmpty.setVisibility(View.VISIBLE);
                                textViewEmpty.setText("No Data Found!!");
                                recyclerViewListChat.setVisibility(View.GONE);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
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
                Log.e("NotificationParams", params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        postRequest.setShouldCache(false);
        requestQueue.add(postRequest);
    }
}
