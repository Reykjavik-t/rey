package com.ctgu401.carpark.entity;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.io.Serializable;

public class Car extends LitePalSupport implements Serializable {
    @Column(nullable = false)
    private String number; // 车牌号

    private String username; // 用户名

    @Column(nullable = false)
    private Boolean isMonthRent; // 是否月租

    private Long monthRentStartTime; // 月租开始时间

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getMonthRent() {
        return isMonthRent;
    }

    public void setMonthRent(Boolean monthRent) {
        isMonthRent = monthRent;
    }

    public Long getMonthRentStartTime() {
        return monthRentStartTime;
    }

    public void setMonthRentStartTime(Long monthRentStartTime) {
        this.monthRentStartTime = monthRentStartTime;
    }

    @Override
    public String toString() {
        return "CarDO{" +
                "number='" + number + '\'' +
                ", username='" + username + '\'' +
                ", isMonthRent=" + isMonthRent +
                ", monthRentStartTime=" + monthRentStartTime +
                '}';
    }
}
