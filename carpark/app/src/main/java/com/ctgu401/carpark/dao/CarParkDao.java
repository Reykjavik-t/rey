package com.ctgu401.carpark.dao;

import com.ctgu401.carpark.entity.Parking;
import com.ctgu401.carpark.utils.VoLog;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;



public class CarParkDao {
    public static String TAG = "CarParkDao";
    private volatile static CarParkDao instance;

    private CarParkDao() {
    }

    public static CarParkDao getInstance() {
        // check 1
        if (null == instance) {
            synchronized (CarDao.class) {
                // check 2
                if (null == instance) {
                    instance = new CarParkDao();
                }
            }
        }
        return instance;
    }

    /**
     * 获取所有的车库号
     * @return
     */
    public List<Integer> listAllParkNumber(){
        List<Integer> res = new ArrayList<>();
        List<Parking> list = LitePal.select("garageNumber").find(Parking.class);
        for(int i = 0 ; i < list.size() ; i++){
            res.add(list.get(i).getGarageNumber());
        }
        return res;
    }


    /**
     * 添加garageRelation 对象
     * @param carParkDO
     * @return
     */
    public boolean saveCarParkDO(Parking carParkDO){
        return carParkDO.save();
    }

    /**
     * 根据车牌号出库
     * @param number
     * @return
     */
    public int deleteCarParkDOByNumber(String number){
        return LitePal.deleteAll(Parking.class , "number = ?" , number);
    }


    /**
     * 根据number(车牌号) 获取Parking（停车场） 对象
     * @param number
     * @return
     */
    public Parking getCarParkDOByNumber(String number){
        VoLog.i(TAG, "getCarParkDOByNumber(String number),number= " + number);
        //从Parking表中查询number = number的行
        List<Parking> res = LitePal.where("number = ?" , number).find(Parking.class);
        return res.size() == 0 ? null : res.get(0);
    }

}
