package com.ctgu401.carpark.services;


import com.ctgu401.carpark.CRUD.CarChange;
import com.ctgu401.carpark.entity.Car;
import com.ctgu401.carpark.utils.VoLog;


public class CarService {
    public static String TAG = "CarService";
    private volatile static CarService instance;

    private CarService() {
    }

    public static CarService getInstance() {
        // check 1
        if (null == instance) {
            synchronized (CarChange.class) {
                // check 2
                if (null == instance) {
                    instance = new CarService();
                }
            }
        }
        return instance;
    }

    private static CarChange carChange = CarChange.getInstance();

    /**
     * @param number plate number
     */
    public Car getCarByNumber(String number) {
        return carChange.queryByNumber(number);
    }

    /**
     * 注册或更新用户信息。
     *
     * @param number      plate number
     * @param username    the specified username
     * @param isMonthRent 是否月租(1,0)
     * @return true or false
     */
    public boolean saveOrUpdate(String number, String username, boolean isMonthRent) {
        Car car = new Car();
        car.setNumber(number);
        car.setUsername(username);
        car.setMonthRent(isMonthRent);
        if (isMonthRent) {
            car.setMonthRentStartTime(System.currentTimeMillis());
        }
        return carChange.saveOrUpdate(number, car);
    }

    public int monthRentExpired(String number, String username) {
        Car car = new Car();
        car.setNumber(number);
        car.setUsername(username);
        car.setMonthRent(false);
        return carChange.updateMonthRentByNumber(number, car);
    }

    public boolean deleteByNumber(String number) {
        return carChange.deleteByNumber(number);
    }
}
