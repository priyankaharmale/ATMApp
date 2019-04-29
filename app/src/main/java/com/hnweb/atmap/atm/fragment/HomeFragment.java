package com.hnweb.atmap.atm.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hnweb.atmap.R;
import com.hnweb.atmap.atm.activity.AddBankAccountActivity;
import com.hnweb.atmap.atm.activity.RequestMoneyDetailsActivity;

public class HomeFragment extends Fragment {


LinearLayout ll_addacount;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bankaccount, container, false);

        ll_addacount=view.findViewById(R.id.ll_addacount);

        ll_addacount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddBankAccountActivity.class);
                startActivity(intent);

            }
        });
        return view;
    }



}
