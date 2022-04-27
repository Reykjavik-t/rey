package com.ctgu401.carpark.utils;

import com.ctgu401.carpark.utils.EncodeImg2Base64;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * 识别车牌号码
 */
public class GetCarNumber {

    private static final String ACCESS_TOKEN = "24.28a82165e6e006f8b8671eb5cdbfdd92.2592000.1653628258.282335-25157919";
    private static final String POST_URL = "https://aip.baidubce.com/rest/2.0/ocr/v1/license_plate?access_token="+ACCESS_TOKEN;
    private static String TAG= "GetCarNumber";

    /**
     * 识别本地图片的文字
     */
    public static String identifyImg(String path) throws URISyntaxException, IOException {
        File file = new File(path);
        if (!file.exists()) {
            throw new NullPointerException("图片不存在");
        }
        String image = EncodeImg2Base64.getImageStrFromPath(path);
        String param = "image=" + image;
        return sendPostRequest(param);
    }


    /**
     * 识别本地图片的文字
     */
    public static String identifyImg(byte[] data) throws URISyntaxException, IOException {
        VoLog.i(TAG, "identifyImg" );
        String image = EncodeImg2Base64.getImageStrFromPath(data);
        String param = "image=" + image;
        return sendPostRequest(param);
    }

    /**
     * 通过传递参数：image进行文字识别
     */
    public static String sendPostRequest(String parma){
        VoLog.i(TAG, "sendPostRequest, parma= " + parma);
        try {
            URL realUrl = new URL(POST_URL);
            // 打开和URL之间的连接
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            connection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            byte[] data = parma.getBytes();
            connection.getOutputStream().write(data , 0 , data.length);
            connection.connect();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String result = "";
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            VoLog.i(TAG, "sendPostRequest, result= " + result);
            return result;
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return null;
    }
}
