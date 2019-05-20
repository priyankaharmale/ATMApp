package com.hnweb.atmap.user.adaptor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
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
import com.hnweb.atmap.atm.bo.User;
import com.hnweb.atmap.contants.AppConstant;
import com.hnweb.atmap.inteface.OnCallBack;
import com.hnweb.atmap.user.activity.AgentBarcodeScanActivity;
import com.hnweb.atmap.user.activity.AgentDetailsActivity;
import com.hnweb.atmap.utils.AlertUtility;
import com.hnweb.atmap.utils.AppUtils;
import com.hnweb.atmap.utils.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;


public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.MyViewHolder> {

    private List<User> userList;
    private Activity activity;
    OnCallBack onCallBack;
    LoadingDialog loadingDialog;
    SharedPreferences prefs;
    String user_id;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_hotelname, tv_address;
        public ImageView iv_profilePic, iv_fav;

        RatingBar rb_raitng;

        public MyViewHolder(View view, final List<User> puzzleList, final Activity activity) {
            super(view);
            tv_hotelname = view.findViewById(R.id.tv_hotelname);
            tv_address = view.findViewById(R.id.tv_address);
            iv_profilePic = view.findViewById(R.id.iv_profilePic);
            iv_fav = view.findViewById(R.id.iv_fav);
            rb_raitng = view.findViewById(R.id.rb_raitng);
            loadingDialog = new LoadingDialog(activity);
        }
    }

    public FavoriteAdapter(List<User> puzzleList, Activity activity, OnCallBack onCallBack) {
        this.userList = puzzleList;
        this.activity = activity;
        this.onCallBack = onCallBack;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adaptor_favorite, parent, false);
        return new MyViewHolder(itemView, userList, activity);
    }

    @SuppressLint({"NewApi", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        User puzzle = userList.get(position);

        prefs = activity.getApplicationContext().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);
        user_id = prefs.getString(AppConstant.KEY_ID, null);
        holder.tv_address.setText(userList.get(position).getCustomer_address());
        holder.tv_hotelname.setText(userList.get(position).getCustomer_name());
        holder.rb_raitng.setRating(Float.parseFloat(userList.get(position).getRatting()));
        String profilePic = userList.get(position).getCustomer_profile_pic();

        holder.iv_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage("Are you sure you want to remove from favourite list?")
                        .setCancelable(false).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                holder.iv_fav.setImageResource(R.drawable.unlike);
                                removeToFav(userList.get(position).getAgent_id(), user_id);


                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

            }
        });
        if (profilePic == null || profilePic.equals("") || profilePic.equals("null")) {
            Glide.with(activity)
                    .load(R.drawable.no_img)
                    .into(holder.iv_profilePic);
        } else {
            Glide.with(activity)
                    .load(profilePic)
                    .placeholder(R.drawable.no_img)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(holder.iv_profilePic);


            //  picUserDetails_iv  = (ImageView) findViewById(R.id.picUserDetails_iv);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });
    }


    @Override
    public int getItemCount() {
        return userList.size();

    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private void removeToFav(final String agentId, final String user_id) {
        loadingDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_REMOVE_TO_FAV,
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
                        } catch (JSONException e) {
                            System.out.println("jsonexeption" + e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String reason = AppUtils.getVolleyError(activity, error);
                        AlertUtility.showAlert(activity, reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("agent_id", agentId);
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
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        requestQueue.add(stringRequest);

    }

}
