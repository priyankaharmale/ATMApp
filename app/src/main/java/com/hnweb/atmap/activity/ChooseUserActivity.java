package com.hnweb.atmap.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.hnweb.atmap.R;
import com.hnweb.atmap.contants.AppConstant;

public class ChooseUserActivity extends AppCompatActivity {
    Button btn_customer, btn_agent;
    SharedPreferences pref;
    SharedPreferences.Editor editorUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chooseuser);
        btn_customer = findViewById(R.id.btn_customer);
        btn_agent = findViewById(R.id.btn_agent);
        pref = getApplicationContext().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);
        editorUser = pref.edit();


        btn_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChooseUserActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                editorUser.putString(AppConstant.KEY_USERTYPE, "1");
                editorUser.commit();
                startActivity(intent);
            }
        });

        btn_agent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChooseUserActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                editorUser.putString(AppConstant.KEY_USERTYPE, "2");
                editorUser.commit();
                startActivity(intent);
            }
        });
    }
}
