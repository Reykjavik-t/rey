package com.ctgu401.carpark.services;

import com.ctgu401.carpark.dao.CarDao;
import com.ctgu401.carpark.dao.CarParkDao;
import com.ctgu401.carpark.entity.Parking;
import com.ctgu401.carpark.utils.VoLog;

import java.util.HashSet;
import java.util.Set;



// CarParkDO service
public class CarParkService {
    public static String TAG = "CarParkService";
    private volatile static CarParkService instance;

    private CarParkService() {
    }

    public static CarParkService getInstance() {
        // check 1
        if (null == instance) {
            //给CarDao这个类上锁
            //synchronized关键字可以保证被它修饰的方法或者代码块在任意时刻只能有一个线程执行，它解决的是多个线程之间访问资源的同步性。
            synchronized (CarDao.class) {
                // check 2
                if (null == instance) {
                    instance = new CarParkService();
                }
            }
        }
        return instance;
    }

    private CarParkDao carParkDao = CarParkDao.getInstance();

    /**
     * 获取所有在使用的车库号
     * @return
     */
    public Set<Integer> listAllParkNumber(){
        return new HashSet<Integer>(carParkDao.listAllParkNumber());
    }

    /**
     * 根据number、isRent、garageNumber 添加CarParkDO对象
     * @param number
     * @param isRent
     * @param garageNumber
     * @return
     */
    public boolean saveCarParkDO(String number , boolean isRent , int garageNumber){
        Parking carParkDO = new Parking();
        carParkDO.setNumber(number);
        carParkDO.setMonthRent(isRent);
        carParkDO.setGarageNumber(garageNumber);
        carParkDO.setEnterTime(System.currentTimeMillis() / 1000); //秒级别的
        return carParkDao.saveCarParkDO(carParkDO);
    }


    /**
     * 根据number 删除GarParkDO对象
     * @param number
     * @return
     */
    public int deleteCarParkDOByNumber(String number){
        return carParkDao.deleteCarParkDOByNumber(number);
    }


    /**
     * 根据number(车牌) 获取Parking对象表
     * @param number:车牌号
     * @return
     */
    public Parking getGarParkDOByNumber(String number){
        VoLog.i(TAG, "getGarParkDOByNumber(number),number(车牌号)= " + number);
        return carParkDao.getCarParkDOByNumber(number);
    }
}
