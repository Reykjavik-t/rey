package com.ctgu401.carpark;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.ctgu401.carpark.activity.ChargePolicyActivity;
import com.ctgu401.carpark.activity.EnterActivity;
import com.ctgu401.carpark.activity.LeaveActivity;
import com.ctgu401.carpark.activity.QueryActivity;
import com.ctgu401.carpark.activity.RegisterActivity;
import com.ctgu401.carpark.entity.Car;
import com.ctgu401.carpark.entity.Parking;
import com.ctgu401.carpark.services.ParkNumberService;
import com.ctgu401.carpark.services.ParkingService;
import com.ctgu401.carpark.services.CarService;
import com.ctgu401.carpark.utils.GetCarNumber;
import com.ctgu401.carpark.utils.ImageHandler;
import com.ctgu401.carpark.utils.VoLog;

import java.io.File;
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

    //页面组件
    private TextView garageId;
    private Button scanBtn;
    private Button choosePhotoBtn;
    private Button manualInputBtn;
    private Button queryBtn;
    private ImageView picture;

    public static final int TAKE_PHOTO = 1; // 拍照
    public static final int CHOOSE_PHOTO = 2; // 选择相册
    private int freeParking;//空闲车位
    private Uri imageUri;
    //存放图片byte数组
    private static byte[] imagedata;

    //Service实体
    private ParkingService parkingService = ParkingService.getInstance();
    private CarService carService = CarService.getInstance();
    private ParkNumberService parkNumberService = ParkNumberService.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        VoLog.i(TAG, " onCreate");
        this.getWindow().setBackgroundDrawableResource(R.drawable.background);
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
        choosePhotoBtn = findViewById(R.id.choose_from_album);
        picture = findViewById(R.id.iv_picture);
        manualInputBtn = findViewById(R.id.manual_input);
        queryBtn = findViewById(R.id.parking_query);

        freeParking = 50;
        freeParking = parkNumberService.leaveParkNumbers();
        garageId.setText(freeParking + "");
        scanBtn.setOnClickListener(this);
        choosePhotoBtn.setOnClickListener(this);
        manualInputBtn.setOnClickListener(this);
        queryBtn.setOnClickListener(this);
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
                scan();
                break;
            case R.id.manual_input:
                dialog();
                break;
            case R.id.parking_query:
                parking_query();
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

    private void scan()
    {
        getPermission();

        //创建file对象，用于存储拍照后的图片；
        File outputImage = new File(getExternalCacheDir(), "output_image.jpg");

        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(MainActivity.this,
                    "com.ctgu401.carpark.fileprovider", outputImage);
        } else {
            imageUri = Uri.fromFile(outputImage);
        }
        VoLog.i(TAG,"scan() imageUri="+ imageUri);
        VoLog.i(TAG,"scan() path="+imageUri.getPath());
        //启动相机程序
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    //手动输入车牌
    private void dialog()
    {
        final EditText et = new EditText(this);
        new AlertDialog.Builder(this).setTitle("请输入车牌号")
                .setIcon(android.R.drawable.sym_def_app_icon)
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //按下确定键后的事件
                        String number = et.getText().toString();
                        Toast.makeText(getApplicationContext(), "车牌：" + number,Toast.LENGTH_LONG).show();
                        park(number);
                    }
                })
                .setNegativeButton("取消",null).show();
    }

    //查询车位状态
    private void parking_query()
    {
        VoLog.i(TAG,"parking_query");
        Intent intent = new Intent(MainActivity.this, QueryActivity.class);
        startActivity(intent);
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
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bm = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));

                        if (null != bm) {
                            bm = ImageHandler.rotateBitmap(bm, 90);
                            bm = ImageHandler.imageZoom(bm);

                            picture.setImageBitmap(bm);
                            imagedata = ImageHandler.bitmap2ByteArray(bm);
                        }
                        new Thread(networkTask).start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
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

    //网路相关子线程
    Runnable networkTask = new Runnable() {

        @Override
        public void run() {
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
        VoLog.i(TAG, "park,plateNumber(车牌号)= " + plateNumber);
        //根据车牌号获取到车位对象(车位对象如果是空，则表示该车为入库)
        Parking parking = parkingService.getParkingByNumber(plateNumber);
        // 离开停车场(车位表中有相关信息)
        if (parking != null) {
            Car car = carService.getCarByNumber(plateNumber);
            // 小时向上取整
            long time = (System.currentTimeMillis() / 1000 - parking.getEnterTime()) / 60 / 60 + 1;
            Intent intent = new Intent(MainActivity.this, LeaveActivity.class);

            // 缴费金额
            long cost = time * 3;
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
        }

        //停车(车位表中没有该车牌号(用Car.getMonthRent判断是否为月租用户)
        else {
            Car car = carService.getCarByNumber(plateNumber);//根据车牌号获取到对应的车辆对象
            if (null == car) {
                // 注册流程
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                intent.putExtra("plateNumber", plateNumber);
                startActivity(intent);
                return;
            } else if (car.getMonthRent()) {
                //月租到期
                if ((System.currentTimeMillis() - car.getMonthRentStartTime()) / 1000 / 60 / 60 / 24 > 30) {
                    int i = carService.monthRentExpired(plateNumber, car.getUsername());
                    // 避免无限递归,只要i>0(代表到期，对Car对象的isMonthRent置为false)
                    if (i > 0) {
                        Toast.makeText(MainActivity.this, "月租已到期，请重新选择", Toast.LENGTH_SHORT).show();
                        park(plateNumber);
                    }
                }
                //月租未到期，直接进入
                //添加关联表信息
                int parkNumber = parkNumberService.getParkNumber();
                parkingService.saveParking(plateNumber, true, parkNumber);
                //跳到展示页面
                Intent intent = new Intent(MainActivity.this, EnterActivity.class);
                intent.putExtra("plateNumber" , plateNumber);
                intent.putExtra("carUserName" , car.getUsername());
                intent.putExtra("parkNumer" , parkNumber + "");
                intent.putExtra("payType" , "月租");
                startActivity(intent);
                return;
            }

            // 进入ChooseActivity
            Intent intent = new Intent(MainActivity.this, ChargePolicyActivity.class);
            intent.putExtra("plateNumber", plateNumber);
            intent.putExtra("username", car.getUsername());
            startActivity(intent);
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