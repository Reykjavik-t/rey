package com.ctgu401.carpark.CRUD;


import com.ctgu401.carpark.entity.Car;
import com.ctgu401.carpark.utils.VoLog;

import org.litepal.LitePal;



public class CarChange {
    public static String TAG = "CarDao";
    private volatile static CarChange instance;

    private CarChange() {
    }

    public static CarChange getInstance() {
        // check 1
        if (null == instance) {
            synchronized (CarChange.class) {
                // check 2
                if (null == instance) {
                    instance = new CarChange();
                }
            }
        }
        return instance;
    }

    public boolean save(Car car) {
        return car.save();
    }

    public Car queryByNumber(String number) {
        return LitePal.where("number = ?", number).findFirst(Car.class);
    }

    public boolean saveOrUpdate(String number, Car car) {
        return car.saveOrUpdate("number = ?", number);
    }

    public int updateMonthRentByNumber(String number, Car car) {
        return car.updateAll("number = ?", number);
    }

    public boolean deleteByNumber(String number) {
        int i = LitePal.deleteAll(Car.class, "number = ?", number);
        return i > 0;
    }
}