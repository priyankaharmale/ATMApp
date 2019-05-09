package com.hnweb.atmap.user.adaptor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.hnweb.atmap.R;
import com.hnweb.atmap.atm.bo.User;

import java.util.List;


public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.MyViewHolder> {

    private List<User> puzzleList;
    private Activity activity;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_hotelname, tv_address;
        public ImageView iv_profilePic;

        public MyViewHolder(View view, final List<User> puzzleList, final Activity activity) {
            super(view);
            tv_hotelname = view.findViewById(R.id.tv_hotelname);
            tv_address = view.findViewById(R.id.tv_address);
            iv_profilePic = view.findViewById(R.id.iv_profilePic);
        }
    }

    public FavoriteAdapter(List<User> puzzleList, Activity activity) {
        this.puzzleList = puzzleList;
        this.activity = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adaptor_favorite, parent, false);
        return new MyViewHolder(itemView, puzzleList, activity);
    }

    @SuppressLint({"NewApi", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        User puzzle = puzzleList.get(position);

        holder.tv_address.setText(puzzleList.get(position).getCustomer_address());
        holder.tv_hotelname.setText(puzzleList.get(position).getCustomer_name());

        String profilePic = puzzleList.get(position).getCustomer_profile_pic();
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
        return puzzleList.size();

    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
