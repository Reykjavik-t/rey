package com.ctgu401.carpark.activity;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.ctgu401.carpark.services.CarParkService;
import com.ctgu401.carpark.services.CarService;
import com.ctgu401.carpark.R;
import com.ctgu401.carpark.services.ParkNumberService;

public class ChargePolicyActivity extends AppCompatActivity implements View.OnClickListener {
    private String plateNumber;      // 车牌号
    private String username;     // 用户名
    private Button monthBtn;
    private Button singleBtn;

    private CarService carService = CarService.getInstance();
    private CarParkService carParkService = CarParkService.getInstance();

    private ParkNumberService parkNumberService = ParkNumberService.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge_policy);

        monthBtn = findViewById(R.id.Month);
        singleBtn = findViewById(R.id.Hour);
        plateNumber = getIntent().getStringExtra("plateNumber");
        username = getIntent().getStringExtra("username");

        monthBtn.setOnClickListener(this);
        singleBtn.setOnClickListener(this);
    }

    //点击包月或者按次计费之后跳到主页
    @Override
    public void onClick(View v) {
        int parkNumber = 0;
        String payType = "";
        switch (v.getId()) {
            case R.id.Month:
                parkNumber = registerAndEnter(true);
                payType = "月租";
                break;
            case R.id.Hour:
                parkNumber = registerAndEnter(false);
                payType = "按时计费";
                break;
        }
        //跳到展示页面
//        Intent intent = new Intent(ChargePolicyActivity.this, EnterActivity.class);
//        intent.putExtra("plateNumber" , plateNumber);
//        intent.putExtra("carUserName" , username);
//        intent.putExtra("parkNumer" , parkNumber + "");
//        intent.putExtra("payType" , payType);
//        startActivity(intent);
    }

    private int registerAndEnter(boolean isMonthRent) {
        carService.saveOrUpdate(plateNumber, username, isMonthRent);

        //添加车库关联信息
        int parkNumber = parkNumberService.getParkNumber();
        carParkService.saveCarParkDO(plateNumber, isMonthRent, parkNumber);

        return parkNumber;
    }
}