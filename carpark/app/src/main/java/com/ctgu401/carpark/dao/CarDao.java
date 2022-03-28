package com.ctgu401.carpark.dao;


import com.ctgu401.carpark.entity.Car;
import com.ctgu401.carpark.utils.VoLog;

import org.litepal.LitePal;



public class CarDao {
    public static String TAG = "CarDao";
    private volatile static CarDao instance;

    private CarDao() {
    }

    public static CarDao getInstance() {
        // check 1
        if (null == instance) {
            synchronized (CarDao.class) {
                // check 2
                if (null == instance) {
                    instance = new CarDao();
                }
            }
        }
        return instance;
    }

    public boolean save(Car carDO) {
        return carDO.save();
    }

    public Car queryByNumber(String number) {
        VoLog.i(TAG, "queryByNumber(String number),number= " + number);
        return LitePal.where("number = ?", number).findFirst(Car.class);

    }

    public boolean saveOrUpdate(String number, Car carDO) {
        return carDO.saveOrUpdate("number = ?", number);
    }

    public int updateMonthRentByNumber(String number, Car carDO) {
        return carDO.updateAll("number = ?", number);
    }

    public boolean deleteByNumber(String number) {
        int i = LitePal.deleteAll(Car.class, "number = ?", number);
        return i > 0;
    }
}