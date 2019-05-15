package com.hnweb.atmap.user.adaptor;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ParseException;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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


public class UserTranscationHistoryAdapter extends RecyclerView.Adapter<UserTranscationHistoryAdapter.ViewHolder> {

    private final List<User> data;
    private static Context context;


    public UserTranscationHistoryAdapter(final List<User> data) {
        this.data = data;

    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        this.context = parent.getContext();
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.adptor_transactionhistory, parent, false));
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        for (int i = 0; i < data.size(); i++) {
            holder.expandState.append(i, false);
        }
        holder.bindView(position);
        final User item = data.get(position);

        holder.tv_hotelname.setText(data.get(position).getCustomer_name());

        holder.tv_address.setText(data.get(position).getCustomer_address());

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

        holder.expandableLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.colorblue));

        holder.expandableLayout.setExpanded(holder.expandState.get(position));


        holder.expandableLayout.setListener(new ExpandableLayoutListenerAdapter() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onPreOpen() {

                createRotateAnimator(holder.iv_arrow, 0f, 180f).start();
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorblue));
                holder.expandState.put(position, true);
                holder.expandableLayout.setExpanded(true);
                holder.iv_arrow.setImageResource(R.drawable.shape_green);


            }

            @SuppressLint("ResourceAsColor")
            @Override
            public void onPreClose() {
                createRotateAnimator(holder.iv_arrow, 180f, 0f).start();
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorWhite));
                holder.expandState.put(position, false);
                holder.iv_arrow.setImageResource(R.drawable.shape);


            }
        });

        holder.iv_arrow.setRotation(holder.expandState.get(position) ? 180f : 0f);
        holder.iv_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                onClickButton(holder.expandableLayout);
            }
        });

        if (data.get(position).getRequest_status().equalsIgnoreCase("1")) {
            holder.iv_check.setImageResource(R.drawable.doublecheck);
            holder.tv_status.setText("Successful");
        } else if (data.get(position).getRequest_status().equalsIgnoreCase("0")) {
            holder.iv_check.setImageResource(R.drawable.cancel);
            holder.tv_status.setText("Cancle");
            holder.tv_status.setTextColor(R.color.colorRed);
        }


    }

    private void onClickButton(final ExpandableLayout expandableLayout) {
        expandableLayout.toggle();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {
        public TextView tv_date, tv_hotelname, tv_time, tv_user_name, tv_status, tv_address;
        private SparseBooleanArray expandState = new SparseBooleanArray();
        public ImageView iv_arrow, iv_doller, iv_check;
        String requesttime, requestdate;
        MapView mMapView;
        public GoogleMap map;
        View layout;
        /**
         * You must use the ExpandableLinearLayout in the recycler view.
         * The ExpandableRelativeLayout doesn't work.
         */
        public ExpandableLinearLayout expandableLayout;

        public ViewHolder(View v) {
            super(v);
            layout = v;

            tv_date = ( TextView ) v.findViewById(R.id.tv_date);
            tv_time = v.findViewById(R.id.tv_time);
            tv_hotelname = v.findViewById(R.id.tv_hotelname);
            iv_arrow = v.findViewById(R.id.iv_arrow);
            iv_doller = v.findViewById(R.id.iv_doller);
            tv_user_name = v.findViewById(R.id.tv_user_name);
            tv_status = v.findViewById(R.id.tv_status);
            iv_check = v.findViewById(R.id.iv_check);
            tv_address = v.findViewById(R.id.tv_address);
            mMapView = v.findViewById(R.id.map);
            expandableLayout = ( ExpandableLinearLayout ) v.findViewById(R.id.expandableLayout);

            if (mMapView != null) {
                // Initialise the MapView
                mMapView.onCreate(null);
                // Set the map ready callback to receive the GoogleMap object
                mMapView.getMapAsync(this);
            }
        }


        @Override
        public void onMapReady(GoogleMap googleMap) {
            MapsInitializer.initialize(context);
            map = googleMap;
            setMapLocation();
        }


        private void setMapLocation() {
            if (map == null) return;

            User data = ( User ) mMapView.getTag();
            if (data == null) return;

            if (data.getCustomer_lat() == null || data.getCustomer_lat().equalsIgnoreCase("null")|| data.getCustomer_lat().equalsIgnoreCase("")) {

            } else {
                Double latitude = Double.valueOf(data.getCustomer_lat());
                Double longitude = Double.valueOf(data.getCustomer_long());
                LatLng lati_long_position = new LatLng(latitude, longitude);

                // Add a marker for this item and set the camera
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(lati_long_position, 100f));
                map.addMarker(new MarkerOptions().position(lati_long_position));

                // Set the map type back to normal.
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }



        }

        private void bindView(int pos) {

            // Store a reference of the ViewHolder object in the layout.
            layout.setTag(this);
            // Store a reference to the item in the mapView's tag. We use it to get the
            // coordinate of a location, when setting the map location.
            mMapView.setTag(data.get(pos));

        }
    }

    public ObjectAnimator createRotateAnimator(final View target, final float from, final float to) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, "rotation", from, to);
        animator.setDuration(300);
        animator.setInterpolator(UtilExapndable.createInterpolator(UtilExapndable.LINEAR_INTERPOLATOR));
        return animator;
    }


}