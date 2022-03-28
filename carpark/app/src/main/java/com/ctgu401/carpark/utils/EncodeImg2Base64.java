package com.ctgu401.carpark.utils;


import android.util.Base64;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

public class EncodeImg2Base64 {

    /**
     * 将一张本地图片转化成Base64字符串
     */
    public static String getImageStrFromPath(String imgPath) {
        InputStream in = null;
        byte[] data = null;
        // 读取图片字节数组
        try {
            in = new FileInputStream(imgPath);
            data = new byte[in.available()];
            in.read(data);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(null != in){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return getImageStrFromPath(data);
    }

    /**
     *
     * @param data 图像字节数据
     * @return
     */
    public static String getImageStrFromPath(byte[] data){
        return URLEncoder.encode(Base64.encodeToString(data, Base64.DEFAULT));
    }
}
