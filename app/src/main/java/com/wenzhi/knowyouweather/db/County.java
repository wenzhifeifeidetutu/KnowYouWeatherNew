package com.wenzhi.knowyouweather.db;

import org.litepal.crud.DataSupport;

/**
 * @Description: 具体定位位置信息，当前县，有id 和县名，天气id和城市id
 * @Author: wenzhi
 * @CreateDate: 2020-01-30 13:22
 * @UpdateUser: 无
 * @UpdateDate: 2020-01-30 13:22
 * @UpdateRemark: 无
 * @Version: 1.0
 */
public class County extends DataSupport {
    private int id;

    private String countyName;

    private String weatherId;

    private int cityId;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
}
