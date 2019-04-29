package com.hnweb.atmap.atm.adaptor;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ParseException;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hnweb.atmap.R;
import com.hnweb.atmap.atm.bo.User;
import com.hnweb.atmap.inteface.ExpandableLayout;
import com.hnweb.atmap.utils.ExpandableLayoutListenerAdapter;
import com.hnweb.atmap.utils.ExpandableLinearLayout;
import com.hnweb.atmap.utils.UtilExapndable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class TranscationHistoryAdapter extends RecyclerView.Adapter<TranscationHistoryAdapter.ViewHolder> {

    private final List<User> data;
    private Context context;


    public TranscationHistoryAdapter(final List<User> data) {
        this.data = data;

    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        this.context = parent.getContext();
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.adaptor_accounthistory, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        for (int i = 0; i < data.size(); i++) {
            holder.expandState.append(i, true);
        }
        final User item = data.get(position);

        holder.tv_user_name.setText(data.get(position).getCustomer_name());
        if (data.get(position).getRequest_amount().equalsIgnoreCase("10")) {
            holder.iv_doller.setImageResource(R.drawable.ten_usd);

        } else if (data.get(position).getRequest_amount().equalsIgnoreCase("20")) {
            holder.iv_doller.setImageResource(R.drawable.twenty_usd);

        } else if (data.get(position).getRequest_amount().equalsIgnoreCase("40")) {
            holder.iv_doller.setImageResource(R.drawable.forty_usd);


        } else if (data.get(position).getRequest_amount().equalsIgnoreCase("60")) {
            holder.iv_doller.setImageResource(R.drawable.sixty_usd);


        } else if (data.get(position).getRequest_amount().equalsIgnoreCase("80")) {
            holder.iv_doller.setImageResource(R.drawable.eighty_usd);

        } else if (data.get(position).getRequest_amount().equalsIgnoreCase("100")) {
            holder.iv_doller.setImageResource(R.drawable.hundred_usd);


        } else if (data.get(position).getRequest_amount().equalsIgnoreCase("200")) {
            holder.iv_doller.setImageResource(R.drawable.two_hundred_usd);


        }


        try {
            DateFormat f = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date d = null;
            try {
                d = f.parse(data.get(position).getRequest_time());
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

        holder.setIsRecyclable(true);

        holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorWhite));
        holder.expandableLayout.setInRecyclerView(true);
        holder.expandableLayout.setExpanded(false);
        holder.expandableLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.colorblue));


        holder.expandableLayout.setExpanded(holder.expandState.get(position));
        holder.expandableLayout.setListener(new ExpandableLayoutListenerAdapter() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onPreOpen() {
                createRotateAnimator(holder.buttonLayout, 0f, 180f).start();
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorblue));
                      holder.expandState.put(position, true);
            }

            @SuppressLint("ResourceAsColor")
            @Override
            public void onPreClose() {
                createRotateAnimator(holder.buttonLayout, 180f, 0f).start();
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorWhite));
                     holder.expandState.put(position, false);
            }
        });

        holder.buttonLayout.setRotation(holder.expandState.get(position) ? 180f : 0f);
        holder.buttonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                onClickButton(holder.expandableLayout);
            }
        });
    }

    private void onClickButton(final ExpandableLayout expandableLayout) {
        expandableLayout.toggle();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_date, tv_cardnumber, tv_time, tv_user_name;
        private SparseBooleanArray expandState = new SparseBooleanArray();
        public ImageView buttonLayout, iv_doller;
        String requesttime, requestdate;
        /**
         * You must use the ExpandableLinearLayout in the recycler view.
         * The ExpandableRelativeLayout doesn't work.
         */
        public ExpandableLinearLayout expandableLayout;

        public ViewHolder(View v) {
            super(v);
            tv_date = ( TextView ) v.findViewById(R.id.tv_date);
            tv_time = v.findViewById(R.id.tv_time);
            tv_cardnumber = v.findViewById(R.id.tv_cardnumber);
            buttonLayout = v.findViewById(R.id.iv_arrow);
            iv_doller = v.findViewById(R.id.iv_doller);
            tv_user_name = v.findViewById(R.id.tv_user_name);
            expandableLayout = ( ExpandableLinearLayout ) v.findViewById(R.id.expandableLayout);
        }
    }

    public ObjectAnimator createRotateAnimator(final View target, final float from, final float to) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, "rotation", from, to);
        animator.setDuration(300);
        animator.setInterpolator(UtilExapndable.createInterpolator(UtilExapndable.LINEAR_INTERPOLATOR));
        return animator;
    }
}