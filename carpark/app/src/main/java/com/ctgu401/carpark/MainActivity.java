package com.ctgu401.carpark;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ctgu401.carpark.activity.RegisterActivity;
import com.ctgu401.carpark.entity.Car;
import com.ctgu401.carpark.entity.Parking;
import com.ctgu401.carpark.services.ParkingService;
import com.ctgu401.carpark.services.CarService;
import com.ctgu401.carpark.utils.GetCarNumber;
import com.ctgu401.carpark.utils.ImageHandler;
import com.ctgu401.carpark.utils.VoLog;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;
import org.litepal.LitePal;
import com.alibaba.fastjson.JSONObject;


public class MainActivity extends AppCompatActivity implements View.OnClickListener,EasyPermissions.PermissionCallbacks
{
    private static final String TAG = "MainActivity";

    private static String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};//拍照和存储权限

    public static final int TAKE_PHOTO = 1; // 拍照
    public static final int CHOOSE_PHOTO = 2; // 选择相册

    private int freeParking;//空闲车位

    //页面组件
    private TextView garageId;
    private Button scanBtn;
    private Button choosePhoto;
    private Button manualInput;
    private ImageView picture;

    //存放图片byte数组
    private static byte[] imagedata;

    private ParkingService parkingService = ParkingService.getInstance();
    private CarService carService = CarService.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        VoLog.i(TAG, " onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LitePal.initialize(this);//初始化LitePal
        init();
    }

    public void init()
    {
        VoLog.i(TAG, " init");
        garageId = findViewById(R.id.garage_id);
        scanBtn = findViewById(R.id.scan_btn);
        choosePhoto = findViewById(R.id.choose_from_album);
        picture = findViewById(R.id.iv_picture);
        manualInput = findViewById(R.id.manual_input);

        freeParking = 50;
//        freeParking = parkNumberService.leaveParkNumbers();
//        garageId.setText(parkingSpace + "");
        scanBtn.setOnClickListener(this);
        choosePhoto.setOnClickListener(this);
        manualInput.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        if (freeParking <= 0) {
            Toast.makeText(MainActivity.this, "暂无车位！", Toast.LENGTH_SHORT).show();
            return;
        }

        switch (v.getId()) {
            case R.id.choose_from_album:
                getPermission();
                openPhotos();
                break;
            case R.id.scan_btn:
                //scan();
                break;
            case R.id.manual_input:
                Intent simpleIntent = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(simpleIntent);
                break;
        }
    }

    // 获取权限
    private void getPermission() {
        if (EasyPermissions.hasPermissions(this, permissions)) {
            //已经打开权限
            // Toast.makeText(this, "已经申请相关权限", Toast.LENGTH_SHORT).show();
        } else {
            //没有打开相关权限、申请权限
            EasyPermissions.requestPermissions(this, "需要获取您的相册、照相使用权限", 1, permissions);
        }

    }

    private void openPhotos()
    {
        VoLog.i(TAG, "openPhotos");
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    /**
     * 当你启动一个活动退出时调用，给你你开始它的requestCode，它返回的resultCode，以及任何来自它的额外数据。
     * 如果活动显式地返回，没有返回任何结果，或在操作期间崩溃，resultCode将是RESULT_CANCELED。
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        VoLog.i(TAG, "onActivityResult , requestCode = " + requestCode + ",resultCode = " + requestCode);
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // 拍照
            case TAKE_PHOTO:
//                if (resultCode == RESULT_OK) {
//                    try {
//                        Bitmap bm = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
//
//                        if (null != bm) {
//                            bm = ImageHandler.rotateBitmap(bm, 90);
//                            bm = ImageHandler.imageZoom(bm);
//
//                            picture.setImageBitmap(bm);
//                            imagedata = ImageHandler.bitmap2ByteArray(bm);
//                        }
//                        new Thread(networkTask).start();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
                break;
            // 选择图片
            case CHOOSE_PHOTO:
                //表示相册这个Activity运行成功
                if (resultCode == Activity.RESULT_OK) {
                    Bitmap bitmap;
                    Uri uri = data.getData();
                    VoLog.i(TAG, "onActivityResult photo's  uri = " + uri);
                    ContentResolver contentResolverr = this.getContentResolver();
                    InputStream inputStream = null;
                    try {
                        inputStream = contentResolverr.openInputStream(uri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    //生成bitmap
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    picture.setImageBitmap(bitmap);

                    //byte[] imagedata;存放图片的byte数组
                    if (null != bitmap) {
                        imagedata = ImageHandler.bitmap2ByteArray(bitmap);
                    }
                    if (imagedata != null) {
                        new Thread(networkTask).start();
                    }
                }
            default:
                break;
        }
    }

    /**
     * 网络操作相关的子线程
     */
    Runnable networkTask = new Runnable() {

        @Override
        public void run() {
            VoLog.i(TAG, "Runnable().run()" );
            // 子线程使用Toast
            if (Looper.myLooper() == null) {
                Looper.prepare();
            }
            try {
                String result = GetCarNumber.identifyImg(imagedata);
                JSONObject jsonObject = JSONObject.parseObject(result);

                if (jsonObject.getString("error_code") != null) {
                    Toast.makeText(MainActivity.this, "识别失败，请重试！", Toast.LENGTH_SHORT).show();
                } else {
                    //车牌号plateNumber
                    String plateNumber = jsonObject.getJSONObject("words_result").getString("number");
                    VoLog.i(TAG, "解析后车牌号(plateNumber)= " + plateNumber);
                    park(plateNumber);
                }
                Looper.loop();
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            } finally {
                imagedata = null;
            }
        }
    };

    private void park(String plateNumber) {
        VoLog.i(TAG, "park,plateNumber= " + plateNumber);
        //根据车牌号获取到车库对象表(如果是空表，则表示入库)
        Parking carParkDO = parkingService.getGarParkDOByNumber(plateNumber);
        // 离开停车场
        if (carParkDO != null) {
//            Car carDO = carService.getByNumber(plateNumber);
//            // 小时向上取整
//            long time = (System.currentTimeMillis() / 1000 - carParkDO.getEnterTime()) / 60 / 60 + 1;
//            Intent intent = new Intent(MainActivity.this, LeaveActivity.class);
//
//            // 缴费金额
//            long cost = time * 5;
//            // 传递相关数据
//            intent.putExtra("cost", cost);
//            intent.putExtra("time", time);
//            intent.putExtra("garageId", carParkDO.getGarageNumber());
//            intent.putExtra("carDO", carDO);
//            if (carParkDO.getMonthRent()) {
//                // 是月租
//                intent.putExtra("cost", 0L);
//            }
//            startActivity(intent);
//
//            // 出库
//            //删除关联表信息
//            carParkService.deleteCarParkDOByNumber(carParkDO.getNumber());
//            //维护set集合信息
//            parkNumberService.outParkNumber(carParkDO.getGarageNumber());

        } else { // 停车
            Car carDO = carService.getByNumber(plateNumber);
            if (null == carDO) {
                // 注册流程
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                intent.putExtra("plateNumber", plateNumber);
                startActivity(intent);
                return;
            } else if (carDO.getMonthRent()) {
//                if ((System.currentTimeMillis() - carDO.getMonthRentStartTime()) / 1000 / 60 / 60 / 24 > 30) {
//                    int i = carService.monthRentExpired(plateNumber, carDO.getUsername());
//                    // 避免无限递归
//                    if (i > 0) {
//                        Toast.makeText(MainActivity.this, "月租已到期，请重新选择", Toast.LENGTH_SHORT).show();
//                        park(plateNumber);
//                    }
//                }
//                // 直接进入
//                //添加关联表信息
//                int parkNumber = parkNumberService.getParkNumber();
//                carParkService.saveCarParkDO(plateNumber, true, parkNumber);
//                //跳到展示页面
//                Intent intent = new Intent(MainActivity.this, EnterActivity.class);
//                intent.putExtra("plateNumber" , plateNumber);
//                intent.putExtra("carUserName" , carDO.getUsername());
//                intent.putExtra("parkNumer" , parkNumber + "");
//                intent.putExtra("payType" , "月租");
//                startActivity(intent);
                return;
            }

            // 进入ChooseActivity
//            Intent intent = new Intent(MainActivity.this, ChargePolicyActivity.class);
//            intent.putExtra("plateNumber", plateNumber);
//            intent.putExtra("username", carDO.getUsername());
//            startActivity(intent);
        }
    }


    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        VoLog.i(TAG, " onPermissionsGranted");
        // Toast.makeText(this, "相关权限获取成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        VoLog.i(TAG, " onPermissionsDenied");
        Toast.makeText(this, "请同意相关权限，否则功能无法使用", Toast.LENGTH_SHORT).show();
    }
}