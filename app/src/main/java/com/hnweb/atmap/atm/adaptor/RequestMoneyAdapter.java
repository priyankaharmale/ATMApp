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
    String requestdate,requesttime;
    private TextView txtScore;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_date, tv_time;

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
    public void onBindViewHolder(MyViewHolder holder, final int position) {

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
                requestdate = formatter.format(dateu);
                System.out.println("str_startDate: " +   requestdate);
                holder.tv_date.setText(  requestdate);

            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }


            try {
                SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");
                SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm a");
                Date time1 = sdf1.parse(time.format(d));
                requesttime=sdf2.format(time1);
                System.out.println("MYTIME" + sdf2.format(time1));

                holder.tv_time.setText(requesttime);
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, RequestMoneyDetailsActivity.class);
                intent.putExtra("name", puzzleList.get(position).getCustomer_name());
                intent.putExtra("requestdate", requestdate);
                intent.putExtra("requesttime",requesttime);
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
