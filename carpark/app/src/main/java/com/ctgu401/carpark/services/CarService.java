package com.ctgu401.carpark.services;


import com.ctgu401.carpark.dao.CarDao;
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
            synchronized (CarDao.class) {
                // check 2
                if (null == instance) {
                    instance = new CarService();
                }
            }
        }
        return instance;
    }

    private static CarDao carDao = CarDao.getInstance();

    /**
     * @param number plate number
     */
    public Car getByNumber(String number) {
        VoLog.i(TAG, "getByNumber(String number),number= " + number);
        return carDao.queryByNumber(number);
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
        Car carDO = new Car();
        carDO.setNumber(number);
        carDO.setUsername(username);
        carDO.setMonthRent(isMonthRent);
        if (isMonthRent) {
            carDO.setMonthRentStartTime(System.currentTimeMillis());
        }
        return carDao.saveOrUpdate(number, carDO);
    }

    public int monthRentExpired(String number, String username) {
        Car carDO = new Car();
        carDO.setNumber(number);
        carDO.setUsername(username);
        carDO.setMonthRent(false);
        return carDao.updateMonthRentByNumber(number, carDO);
    }

    public boolean deleteByNumber(String number) {
        return carDao.deleteByNumber(number);
    }
}
