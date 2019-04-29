package com.hnweb.atmap.user.adaptor;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hnweb.atmap.R;
import com.hnweb.atmap.inteface.OnCallBack;

import java.util.ArrayList;

public class ImagesAdaptor extends RecyclerView.Adapter<ImagesAdaptor.MyViewHolder> {
    ArrayList personImages;
    ArrayList personNames;
    Context context;
    int row_index=0;
    OnCallBack onCallBack;

    public ImagesAdaptor(Context context, ArrayList personImages, ArrayList personNames, OnCallBack onCallBack) {
        this.context = context;
        this.personImages = personImages;
        this.personNames = personNames;
        this.onCallBack = onCallBack;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adaptor_images, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ImagesAdaptor.MyViewHolder holder, final int position) {
        holder.image.setImageResource(( Integer ) personImages.get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        holder.linearlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                row_index = position;
                notifyDataSetChanged();
            }
        });
        if (row_index == position) {
            holder.linearlayout.setBackgroundResource(R.drawable.curve);
            selectdAmount(String.valueOf(personNames.get(position)), personImages.get(position).toString());

        } else {
            holder.linearlayout.setBackgroundColor(Color.parseColor("#3a5c68"));
        }
    }

    @Override
    public int getItemCount() {
        return personImages.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        // init the item view's
        ImageView image;
        LinearLayout linearlayout;

        public MyViewHolder(View itemView) {
            super(itemView);
            // get the reference of item view's
            image = itemView.findViewById(R.id.image);
            linearlayout = itemView.findViewById(R.id.linearlayout);
        }
    }

    public void selectdAmount(String amount, String image) {
        onCallBack.selctedImge(amount, image);
    }
}
