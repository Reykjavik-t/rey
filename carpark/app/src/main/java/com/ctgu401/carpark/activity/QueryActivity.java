package com.ctgu401.carpark.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ctgu401.carpark.MainActivity;
import com.ctgu401.carpark.R;
import com.ctgu401.carpark.entity.Parking;
import com.ctgu401.carpark.services.ParkingService;
import com.ctgu401.carpark.utils.VoLog;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class QueryActivity extends Activity implements View.OnClickListener
{
    private static String TAG = "QueryActivity";

    private RecyclerView recyclerView;
    private List<Parking> allUserParking = new ArrayList<>();

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
        //已使用的车位数
        int usedPark = initData();
        RecyclerView.Adapter adapter = new RecyclerView.Adapter<ParkingViewHolder>(){
            // 创建列表项组件的方法，该方法创建组件会被自动缓存
            @NonNull
            @Override
            public ParkingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(QueryActivity.this).inflate(R.layout.query_item, null);
                return new ParkingViewHolder(view,this);
            }
            // 为列表项组件绑定数据的方法，每次组件重新显示出来时都会重新执行该方法
            @Override
            public void onBindViewHolder(@NonNull ParkingViewHolder parkingViewHolder, int position)
            {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                Date date = new Date((allUserParking.get(position).getEnterTime()) * 1000);
                if(allUserParking.get(position).getMonthRent())
                    parkingViewHolder.isMonthRent.setImageResource(R.drawable.yes);
                else
                    parkingViewHolder.isMonthRent.setImageResource(R.drawable.no);

                parkingViewHolder.garageNumber.setText(allUserParking.get(position).getGarageNumber() +" ");
                parkingViewHolder.number.setText(allUserParking.get(position).getNumber() + " ");
                parkingViewHolder.enterTime.setText(simpleDateFormat.format(date));

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
        switch (v.getId())
        {
            case R.id.query_item_leave:
                Intent intent = new Intent(QueryActivity.this,MainActivity.class);
        }
    }

    class ParkingViewHolder extends RecyclerView.ViewHolder
    {
        private TextView garageNumber;
        private TextView number;
        private TextView enterTime;
        private ImageView isMonthRent;
        public ParkingViewHolder(View itemView, RecyclerView.Adapter adapter)
        {
            super(itemView);
            this.isMonthRent = itemView.findViewById(R.id.query_item_isMonthRent);
            this.enterTime = itemView.findViewById(R.id.query_item_enterTime);
            this.number = itemView.findViewById(R.id.query_item_number);
            this.garageNumber = itemView.findViewById(R.id.query_item_garageNumber);
        }
    }
}
