package com.ctgu401.carpark.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ctgu401.carpark.MainActivity;
import com.ctgu401.carpark.R;
import com.ctgu401.carpark.entity.Car;
import com.ctgu401.carpark.entity.Parking;
import com.ctgu401.carpark.services.CarService;
import com.ctgu401.carpark.services.ParkingService;
import com.ctgu401.carpark.utils.VoLog;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class QueryActivity extends Activity implements View.OnClickListener
{
    private static String TAG = "QueryActivity";

    private RecyclerView recyclerView;
    private List<Parking> allUserParking = new ArrayList<>();
    private int currentPosition;//当前选中项目在列表中的位置
    private CarService carService = CarService.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);
        recyclerView = findViewById(R.id.recycler);
        // 设置RecyclerView保持固定大小,这样可优化RecyclerView的性能
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        // 设置RecyclerView的滚动方向
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        // 为RecyclerView设置布局管理器
        recyclerView.setLayoutManager(layoutManager);

//        recyclerView.addOnItemTouchListener();
//
//        class RecyclerViewClickListener implements RecyclerView.OnItemTouchListener
//        {
//
//            @Override
//            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
//                return false;
//            }
//
//            @Override
//            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
//
//            }
//
//            @Override
//            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
//
//            }
//        }

        //已使用的车位数
        int usedPark = initData();
        RecyclerView.Adapter adapter = new RecyclerView.Adapter<ParkingViewHolder>(){
            // 创建列表项组件的方法，该方法创建组件会被自动缓存
            @NonNull
            @Override
            public ParkingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                VoLog.i(TAG,"QueryActivity_onCreateViewHolder " + currentPosition);
                View view = LayoutInflater.from(QueryActivity.this).inflate(R.layout.query_item, null);
                view.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        final TextView et = new TextView(QueryActivity.this);
                        new AlertDialog.Builder(QueryActivity.this).setTitle("入库成功")
                                .setIcon(android.R.drawable.sym_def_app_icon)
                                .setView(et)
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //按下确定键后的事件
//                        String number = et.getText().toString();
//                        Toast.makeText(getApplicationContext(), "车牌：" + number,Toast.LENGTH_LONG).show();
                                        //Intent intent = new Intent(EnterActivity.this, MainActivity.class);
                                        //startActivity(intent);
                                    }
                                })
                                .setNegativeButton("取消",null).show();
                    }
                });
                return new ParkingViewHolder(view);
            }
            // 为列表项组件绑定数据的方法，每次组件重新显示出来时都会重新执行该方法
            @Override
            public void onBindViewHolder(@NonNull ParkingViewHolder parkingViewHolder, int position)
            {
                VoLog.i(TAG,"QueryActivity_onBindViewHolder"+position);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                Date date = new Date((allUserParking.get(position).getEnterTime()) * 1000);
                parkingViewHolder.enterTime.setText(simpleDateFormat.format(date));

                if(allUserParking.get(position).getMonthRent())
                    parkingViewHolder.isMonthRent.setImageResource(R.drawable.yes);
                else
                    parkingViewHolder.isMonthRent.setImageResource(R.drawable.no);

                parkingViewHolder.garageNumber.setText(allUserParking.get(position).getGarageNumber() +" ");
                parkingViewHolder.number.setText(allUserParking.get(position).getNumber() + " ");
            }
            // 该方法的返回值决定包含多少个列表项
            @Override
            public int getItemCount()
            {
                return usedPark;
            }
        };

        recyclerView.setAdapter(adapter);

    }
    private int initData()
    {
        ParkingService parkingService = ParkingService.getInstance();
        //所有正在使用的车库对象
        allUserParking = parkingService.allUsedParking();
        //int a = allUserParking.toArray().length;
        int a = allUserParking.size();

        VoLog.i(TAG,"使用中的车位的个数" + a);
        return a;
    }

    @Override
    public void onClick(View v)
    {
//        TableLayout loginForm = (TableLayout) getLayoutInflater().inflate(R.layout.query_detail_dialog, null);
//        switch (v.getId())
//        {
//            case R.id.query_item_details:
//                final TextView et = new TextView(this);
//                new AlertDialog.Builder(this).setTitle("入库成功")
//                        .setIcon(android.R.drawable.sym_def_app_icon)
//                        .setView(et)
//                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                //按下确定键后的事件
//                        String number = et.getText().toString();
//                            }
//                        })
//                        .setNegativeButton("取消",null).show();
//        }
    }


    class ParkingViewHolder extends RecyclerView.ViewHolder
    {
        private TextView garageNumber;
        private TextView number;
        private TextView enterTime;
        private ImageView isMonthRent;
        private Button leaveBtn;
        private Button detailBtn;
        public ParkingViewHolder(View itemView)
        {
            super(itemView);
            this.isMonthRent = itemView.findViewById(R.id.query_item_isMonthRent);
            this.enterTime = itemView.findViewById(R.id.query_item_enterTime);
            this.number = itemView.findViewById(R.id.query_item_number);
            this.garageNumber = itemView.findViewById(R.id.query_item_garageNumber);
            this.leaveBtn = itemView.findViewById(R.id.query_item_leave);
            this.detailBtn = itemView.findViewById(R.id.query_item_details);

            leaveBtn.setOnClickListener((view)->{
                VoLog.i(TAG,"该车位的车牌号：" + allUserParking.get(currentPosition).getNumber());

                currentPosition = this.getAdapterPosition();
                Parking parking = allUserParking.get(currentPosition);
                Car car = carService.getCarByNumber(allUserParking.get(currentPosition).getNumber());

                // 小时向上取整
                long time = (System.currentTimeMillis() / 1000 - parking.getEnterTime()) / 60 / 60 + 1;
                // 缴费金额
                long cost = time * 3;
                Intent intent = new Intent(QueryActivity.this, LeaveActivity.class);
                // 传递相关数据
                intent.putExtra("cost", cost);
                intent.putExtra("time", time);
                intent.putExtra("garageId", parking.getGarageNumber());
                intent.putExtra("car", car);
                if (parking.getMonthRent()) {
                    // 是月租
                    intent.putExtra("cost", 0L);
                }
                startActivity(intent);
            });
        }
    }
}