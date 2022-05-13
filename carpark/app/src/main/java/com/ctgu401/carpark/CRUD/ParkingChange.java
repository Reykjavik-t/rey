package com.ctgu401.carpark.CRUD;

import com.ctgu401.carpark.entity.Parking;
import com.ctgu401.carpark.utils.VoLog;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;



public class ParkingChange {
    public static String TAG = "CarParkDao";
    private volatile static ParkingChange instance;

    private ParkingChange() {
    }

    public static ParkingChange getInstance() {
        // check 1
        if (null == instance) {
            synchronized (CarChange.class) {
                // check 2
                if (null == instance) {
                    instance = new ParkingChange();
                }
            }
        }
        return instance;
    }

    /**
     * 获取所有的车位号(在使用的)
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
     * 获取所有正在使用的车位对象
     * @return
     */
    public List<Parking> listAllUsedParking()
    {
        List<Parking> list = LitePal.select().find(Parking.class);
        return list;
    }



    /**
     * 添加garageRelation 对象，车位只存在保存，不存在更新
     * @param parking
     * @return
     */
    public boolean saveParking(Parking parking){
        return parking.save();
    }

    /**
     * 根据车牌号出库
     * @param number
     * @return
     */
    public int deleteParkingByNumber(String number){
        return LitePal.deleteAll(Parking.class , "number = ?" , number);
    }


    /**
     * 根据number(车牌号) 获取Parking（车位） 对象
     * @param number
     * @return
     */
    public Parking getParkingByNumber(String number){
        //从Parking表中查询number = number的行
        List<Parking> res = LitePal.where("number = ?" , number).find(Parking.class);
        return res.size() == 0 ? null : res.get(0);
    }

}
