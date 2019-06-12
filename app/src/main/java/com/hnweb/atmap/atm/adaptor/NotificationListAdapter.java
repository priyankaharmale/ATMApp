package com.hnweb.atmap.atm.adaptor;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hnweb.atmap.R;
import com.hnweb.atmap.atm.bo.NotificationModel;
import com.hnweb.atmap.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.ViewHolder> {

    private Context context;
    private List<NotificationModel> notificationModelList;
    private LayoutInflater inflater;

    public NotificationListAdapter(FragmentActivity activity, ArrayList<NotificationModel> notificationModelList) {

        this.context = activity;
        this.notificationModelList = notificationModelList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public NotificationListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rowView = inflater.inflate(R.layout.adaptor_notification_list, parent, false);
        NotificationListAdapter.ViewHolder vh = new NotificationListAdapter.ViewHolder(rowView);
        return vh;
    }

    @Override
    public void onBindViewHolder(final NotificationListAdapter.ViewHolder holder, final int i) {
        final NotificationModel notificationModel = notificationModelList.get(i);

        holder.tvNotificationMsg.setText(notificationModel.getName());
        String chat_date_time = notificationModel.getRequest_time();
        String requeststatus = notificationModel.getRequest_status();
        if (requeststatus.equalsIgnoreCase("0")) {
            holder.textView.setText("Pending Request");

        } else if (requeststatus.equalsIgnoreCase("1")) {
            holder.textView.setText("Approved Request");

        } else if (requeststatus.equalsIgnoreCase("2")) {
            holder.textView.setText("Reject Request");

        }

        Log.e("DateFormatBook", chat_date_time);

        Utils util = new Utils();
        String input_date_format = "yyyy-MM-dd HH:mm:ss";
        String output_date_format = "HH:mm aa, dd MMM yyyy";
        String outputDateTimeFormat = util.dateFormats(chat_date_time, input_date_format, output_date_format);
        Log.d("DateFormatBook", outputDateTimeFormat);

        holder.tvChatDateTime.setText(outputDateTimeFormat);

    }


    @Override
    public int getItemCount() {
        return notificationModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvNotificationMsg, tvChatDateTime;
        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);

            tvNotificationMsg = itemView.findViewById(R.id.textView_notification);
            tvChatDateTime = itemView.findViewById(R.id.textView_msg_date_time);
            textView = itemView.findViewById(R.id.textView);

        }
    }
}


