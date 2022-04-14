package com.ctgu401.carpark.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ctgu401.carpark.MainActivity;
import com.ctgu401.carpark.R;
import com.ctgu401.carpark.entity.Car;
import com.ctgu401.carpark.services.ParkNumberService;
import com.ctgu401.carpark.services.ParkingService;
import com.ctgu401.carpark.utils.VoLog;

public class LeaveActivity extends AppCompatActivity implements View.OnClickListener {
    public static String TAG = "leaveActivity";
    private int garageId;    //车库号
    private long time;        //总时间
    private long cost;           //费用
    private Button leaveBtn;
    private TextView plateNumberTextView;
    private TextView usernameTextView;
    private TextView garageIdTextView;
    private TextView timeTextView;
    private TextView costTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setBackgroundDrawableResource(R.drawable.background);
        setContentView(R.layout.activity_leave);
        plateNumberTextView = findViewById(R.id.plate_number);
        usernameTextView = findViewById(R.id.username);
        garageIdTextView = findViewById(R.id.garage_id);
        timeTextView = findViewById(R.id.park_time);
        costTextView = findViewById(R.id.cost);
        leaveBtn = findViewById(R.id.leave);

        garageId = getIntent().getIntExtra("garageId", 0);
        time = getIntent().getLongExtra("time", 0L);
        cost = getIntent().getLongExtra("cost", 0L);
        // 获取反序列化的user
        Car car = (Car) getIntent().getSerializableExtra("car");
        if (car.getMonthRent()) {
            cost = 0L;
        }

        plateNumberTextView.setText(car.getNumber());
        usernameTextView.setText(car.getUsername());

        leave(car.getNumber());
        garageIdTextView.setText(garageId + "");
        timeTextView.setText(time + "小时");
        long day = 30 - ((System.currentTimeMillis() - car.getMonthRentStartTime()) / 1000 / 60 / 60 / 24 + 1);
        if (car.getMonthRent()) {
            costTextView.setText("月租剩余:" + day + "天");
        } else {
            costTextView.setText(cost + "元");
        }

        leaveBtn.setOnClickListener(this);
    }

    // 出库 删除数据库相关信息
    private void leave(String number) {
        ParkNumberService parkNumberService = ParkNumberService.getInstance();
        ParkingService parkingService = ParkingService.getInstance();
        //删除关联表相关信息
        parkingService.deleteParkingByNumber(number);
        //维护set集合
        parkNumberService.outParkNumber(garageId);
        Toast.makeText(getApplicationContext(),"出库收费 "+cost+" 元。",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(LeaveActivity.this, MainActivity.class);
        startActivity(intent);
    }
}