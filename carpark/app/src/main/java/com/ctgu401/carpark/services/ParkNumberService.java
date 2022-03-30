package com.ctgu401.carpark.services;

import com.ctgu401.carpark.CRUD.CarChange;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

//分发车库号
public class ParkNumberService {
    private volatile static ParkNumberService instance;
    private static ParkingService parkingService = ParkingService.getInstance();
    //默认为车库，从1-100
    private static final int garageIdSize = 100;
    //已经使用过的GarageId
    private static Set<Integer> usedParkNumber = null;
    //还未使用过的GarageId
    private static Set<Integer> unUsedParkNumber = null;

    private ParkNumberService() {
    }

    public static ParkNumberService getInstance() {
        // check 1
        if (null == instance) {
            synchronized (CarChange.class) {
                // check 2
                if (null == instance) {
                    instance = new ParkNumberService();
                }
            }
        }
        return instance;
    }


    /**
     * 初始化Set集合
     */
    private static void initSet() {
        usedParkNumber = parkingService.listAllParkNumber();
        Set<Integer> set2 = new HashSet<>();
        for (int i = 1; i <= garageIdSize; i++) {
            if (!usedParkNumber.contains(i)) {
                set2.add(i);
            }
        }
        unUsedParkNumber = set2;
    }

    /**
     * 入库，获取车牌号
     * @return
     */
    public int getParkNumber() {
        if (usedParkNumber == null || unUsedParkNumber == null) {
            initSet();
        }
        if (unUsedParkNumber.isEmpty()) {
            return -1;
        }
        Object[] objs = unUsedParkNumber.toArray();
        int ran = new Random().nextInt(objs.length);
        unUsedParkNumber.remove(objs[ran]);
        usedParkNumber.add((Integer) objs[ran]);
        return (Integer) objs[ran];
    }

    /**
     * 出库
     * @param parkNumber(车位号)
     */
    public void outParkNumber(int parkNumber) {
        if (usedParkNumber == null || unUsedParkNumber == null) {
            initSet();
        }
        usedParkNumber.remove(parkNumber);
        unUsedParkNumber.add(parkNumber);
    }

    /**
     * 剩余车位个数
     * @return
     */
    public int leaveParkNumbers() {
        if (usedParkNumber == null || unUsedParkNumber == null) {
            initSet();
        }
        return unUsedParkNumber.size();
    }


}
