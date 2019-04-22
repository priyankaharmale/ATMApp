package com.hnweb.atmap.agent.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.hnweb.atmap.R;

public class AgentBarcodeScanActivity extends AppCompatActivity {
    ImageView iv_barcode, iv_dollor;
    String barcode, businessName, address;
    String image;
    Bitmap ImageBitmap;
    TextView tv_hotelname, tv_address, tv_barcodeNo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startwithdraw);
        Intent intent = getIntent();
        barcode = intent.getStringExtra("barcode");
        image = intent.getStringExtra("image");
        address = intent.getStringExtra("address");
        businessName = intent.getStringExtra("businessName");

        iv_barcode = findViewById(R.id.iv_barcode);
        iv_dollor = findViewById(R.id.iv_dollor);
        tv_address = findViewById(R.id.tv_address);
        tv_barcodeNo = findViewById(R.id.tv_barcodeNo);
        tv_hotelname = findViewById(R.id.tv_hotelname);
        iv_dollor.setImageResource(Integer.parseInt(image));

        tv_barcodeNo.setText(barcode);
        tv_address.setText(address);
        tv_hotelname.setText(businessName);

        generate(barcode, iv_barcode);
    }

    private void generate(String palate_id, ImageView qrcode) {
        MultiFormatWriter writer = new MultiFormatWriter();
        String finaldata = Uri.encode(palate_id, "utf-8");

        BitMatrix bm = null;
        try {
            bm = writer.encode(finaldata, BarcodeFormat.CODE_128, 150, 150);

        } catch (WriterException e) {
            e.printStackTrace();
        }
        ImageBitmap = Bitmap.createBitmap(180, 60, Bitmap.Config.ARGB_8888);

        for (int i = 0; i < 180; i++) {//width
            for (int j = 0; j < 60; j++) {//height
                ImageBitmap.setPixel(i, j, bm.get(i, j) ? Color.BLACK : Color.WHITE);
            }
        }

        if (ImageBitmap != null) {
            qrcode.setImageBitmap(ImageBitmap);
        } else {
            Toast.makeText(this, "Error",
                    Toast.LENGTH_SHORT).show();
        }

    }
}
