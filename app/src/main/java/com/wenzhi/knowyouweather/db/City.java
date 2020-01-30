package com.wenzhi.knowyouweather.db;

import org.litepal.crud.DataSupport;

/**
 * @Description: 城市数据库类,存放城市信息,id， 城市名称，城市码，所属省的id
 * @Author: wenzhi
 * @CreateDate: 2020-01-30 13:19
 * @UpdateUser: 无
 * @UpdateDate: 2020-01-30 13:19
 * @UpdateRemark: 无
 * @Version: 1.0
 */
public class City extends DataSupport {

    private int id;

    private String cityName;

    private int cityCode;

    private int provinceID;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public int getProvinceID() {
        return provinceID;
    }

    public void setProvinceID(int provinceID) {
        this.provinceID = provinceID;
    }
}
