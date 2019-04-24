package com.hnweb.atmap.atm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hnweb.atmap.R;

public class RequestMoneyDetailsActivity extends AppCompatActivity {
    TextView tv_user_name, tv_requesttime, tv_requestdate;
    String name, requesttime, requestdate;
    Toolbar toolbar;
    ImageView iv_back;
    Button btn_scan;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requestmoney_details);
        tv_user_name = findViewById(R.id.tv_user_name);
        tv_requesttime = findViewById(R.id.tv_requesttime);
        tv_requestdate = findViewById(R.id.tv_requestdate);
        toolbar = findViewById(R.id.toolbar);
        iv_back = toolbar.findViewById(R.id.iv_back);
        btn_scan = findViewById(R.id.btn_scan);

        final Intent intent = getIntent();
        name = intent.getStringExtra("name");
        requesttime = intent.getStringExtra("requesttime");
        requestdate = intent.getStringExtra("requestdate");
        tv_user_name.setText(name);
        tv_requesttime.setText(requesttime);
        tv_requestdate.setText(requestdate);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(RequestMoneyDetailsActivity.this, ScannedBarcodeActivity.class);
                startActivity(intent1);
            }
        });

    }
}
