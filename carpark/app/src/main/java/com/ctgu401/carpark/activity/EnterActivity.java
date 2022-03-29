package com.ctgu401.carpark.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ctgu401.carpark.MainActivity;
import com.ctgu401.carpark.R;

import java.text.SimpleDateFormat;
import java.util.Date;



public class EnterActivity extends AppCompatActivity implements View.OnClickListener{

    private String plateNumber;//车牌号码
    private String carUserName;//车主信息
    private String parkNumer;//车库号码
    private String payType;//付费方式

    //TextView
    private TextView tvCarNumber;
    private TextView tvCarUserName;
    private TextView tvParkNumer;
    private TextView tvEnterTime;
    private TextView tvPayType;

    private Button leaveBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter);

        //初始化基本组件
        init();

        leaveBtn.setOnClickListener(this);

    }

    //初始化基本组件
    private void init() {
        tvCarNumber = findViewById(R.id.carNumber);
        tvCarUserName = findViewById(R.id.carUserName);
        tvParkNumer = findViewById(R.id.parkNumber);
        tvEnterTime = findViewById(R.id.enterTime);
        tvPayType = findViewById(R.id.payType);

        leaveBtn = findViewById(R.id.leave);

        plateNumber = getIntent().getStringExtra("plateNumber");
        carUserName = getIntent().getStringExtra("carUserName");
        parkNumer = getIntent().getStringExtra("parkNumer");
        payType = getIntent().getStringExtra("payType");


        tvCarNumber.setText(plateNumber);
        tvCarUserName.setText(carUserName);
        tvParkNumer.setText(parkNumer);
        tvEnterTime.setText(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
        tvPayType.setText(payType);
    }


    @Override
    public void onClick(View view) {
        Intent intent = new Intent(EnterActivity.this, MainActivity.class);
        startActivity(intent);
    }
}