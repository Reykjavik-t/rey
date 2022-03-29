package com.ctgu401.carpark.services;

import com.ctgu401.carpark.CRUD.CarChange;
import com.ctgu401.carpark.CRUD.ParkingChange;
import com.ctgu401.carpark.entity.Parking;
import com.ctgu401.carpark.utils.VoLog;

import java.util.HashSet;
import java.util.Set;



// CarParkDO service
public class ParkingService {
    public static String TAG = "CarParkService";
    private volatile static ParkingService instance;

    private ParkingService() {
    }

    public static ParkingService getInstance() {
        // check 1
        if (null == instance) {
            //给CarDao这个类上锁
            //synchronized关键字可以保证被它修饰的方法或者代码块在任意时刻只能有一个线程执行，它解决的是多个线程之间访问资源的同步性。
            synchronized (CarChange.class) {
                // check 2
                if (null == instance) {
                    instance = new ParkingService();
                }
            }
        }
        return instance;
    }

    private ParkingChange parkingChange = ParkingChange.getInstance();

    /**
     * 获取所有在使用的车库号
     * @return
     */
    public Set<Integer> listAllParkNumber(){
        return new HashSet<Integer>(parkingChange.listAllParkNumber());
    }

    /**
     * 根据number、isRent、garageNumber 添加CarParkDO对象
     * @param number
     * @param isRent
     * @param garageNumber
     * @return
     */
    public boolean saveParking(String number , boolean isRent , int garageNumber){
        Parking parking = new Parking();
        parking.setNumber(number);
        parking.setMonthRent(isRent);
        parking.setGarageNumber(garageNumber);
        parking.setEnterTime(System.currentTimeMillis() / 1000); //秒级别的
        return parkingChange.saveParking(parking);
    }


    /**
     * 根据number 删除GarParkDO对象
     * @param number
     * @return
     */
    public int deleteCarParkDOByNumber(String number){
        return parkingChange.deleteCarParkDOByNumber(number);
    }


    /**
     * 根据number(车牌) 获取Parking对象表
     * @param number:车牌号
     * @return
     */
    public Parking getGarParkDOByNumber(String number){
        VoLog.i(TAG, "getGarParkDOByNumber(number),number(车牌号)= " + number);
        return parkingChange.getCarParkDOByNumber(number);
    }
}
