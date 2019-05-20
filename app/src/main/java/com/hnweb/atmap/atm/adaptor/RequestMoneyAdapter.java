package com.hnweb.atmap.atm.adaptor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ParseException;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hnweb.atmap.R;
import com.hnweb.atmap.atm.activity.RequestMoneyDetailsActivity;
import com.hnweb.atmap.atm.bo.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class RequestMoneyAdapter extends RecyclerView.Adapter<RequestMoneyAdapter.MyViewHolder> {

    private List<User> puzzleList;
    private Activity activity;
    String pid;
    private TextView txtScore;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_date, tv_time;
        String requestdate, requesttime;

        public ImageView iv_doller;

        public MyViewHolder(View view, final List<User> puzzleList, final Activity activity) {
            super(view);
            tv_date = view.findViewById(R.id.tv_date);
            tv_time = view.findViewById(R.id.tv_time);
            iv_doller = view.findViewById(R.id.iv_doller);


        }
    }


    public RequestMoneyAdapter(List<User> puzzleList, Activity activity) {
        this.puzzleList = puzzleList;
        this.activity = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adaptor_requestmoney, parent, false);
        return new MyViewHolder(itemView, puzzleList, activity);
    }

    @SuppressLint({"NewApi", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        User puzzle = puzzleList.get(position);
        //holder.tv_date.setText(puzzleList.get(position).getRequest_time());


        try {
            DateFormat f = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date d = null;
            try {
                d = f.parse(puzzleList.get(position).getRequest_time());
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
            SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat time = new SimpleDateFormat(" hh:mm:ss");
            System.out.println("Date: " + date.format(d));
            System.out.println("Time: " + time.format(d));


            SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
            Date dateu = null;
            try {
                dateu = date.parse(date.format(d));
                holder.requestdate = formatter.format(dateu);
                System.out.println("str_startDate: " + holder.requestdate);
                holder.tv_date.setText(holder.requestdate);

            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }


            try {
                SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");
                SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm a");
                Date time1 = sdf1.parse(time.format(d));
                holder.requesttime = sdf2.format(time1);
                System.out.println("MYTIME" + sdf2.format(time1));

                holder.tv_time.setText(holder.requesttime);
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (puzzleList.get(position).getRequest_amount().equalsIgnoreCase("10")) {
            holder.iv_doller.setImageResource(R.drawable.ten_usd);

        } else if (puzzleList.get(position).getRequest_amount().equalsIgnoreCase("20")) {
            holder.iv_doller.setImageResource(R.drawable.twenty_usd);

        } else if (puzzleList.get(position).getRequest_amount().equalsIgnoreCase("40")) {
            holder.iv_doller.setImageResource(R.drawable.forty_usd);


        } else if (puzzleList.get(position).getRequest_amount().equalsIgnoreCase("60")) {
            holder.iv_doller.setImageResource(R.drawable.sixty_usd);


        } else if (puzzleList.get(position).getRequest_amount().equalsIgnoreCase("80")) {
            holder.iv_doller.setImageResource(R.drawable.eighty_usd);

        } else if (puzzleList.get(position).getRequest_amount().equalsIgnoreCase("100")) {
            holder.iv_doller.setImageResource(R.drawable.hundred_usd);


        } else if (puzzleList.get(position).getRequest_amount().equalsIgnoreCase("200")) {
            holder.iv_doller.setImageResource(R.drawable.two_hundred_usd);


        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, RequestMoneyDetailsActivity.class);
                intent.putExtra("name", puzzleList.get(position).getCustomer_name());
                intent.putExtra("requestdate", holder.requestdate);
                intent.putExtra("requesttime", holder.requesttime);
                intent.putExtra("request_user_id",puzzleList.get(position).getRequest_user_id());
                intent.putExtra("request_id",puzzleList.get(position).getRequest_id());
                activity.startActivity(intent);

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
