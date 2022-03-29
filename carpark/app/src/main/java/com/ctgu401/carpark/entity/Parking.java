package com.ctgu401.carpark.entity;

import org.litepal.crud.LitePalSupport;

/**
 * 车位关系表
 */
public class Parking extends LitePalSupport {

    private Integer garageNumber;//车位号

    private String number;//车牌号

    private Long enterTime;//入库时间

    private Boolean isMonthRent;//是否月租

    public Integer getGarageNumber() {
        return garageNumber;
    }

    public void setGarageNumber(Integer garageNumber) {
        this.garageNumber = garageNumber;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Long getEnterTime() {
        return enterTime;
    }

    public void setEnterTime(Long enterTime) {
        this.enterTime = enterTime;
    }

    public Boolean getMonthRent() {
        return isMonthRent;
    }

    public void setMonthRent(Boolean monthRent) {
        isMonthRent = monthRent;
    }

    @Override
    public String toString() {
        return "CarParkDO{" +
                "garageNumber=" + garageNumber +
                ", number='" + number + '\'' +
                ", enterTime=" + enterTime +
                ", isMonthRent=" + isMonthRent +
                '}';
    }
}
