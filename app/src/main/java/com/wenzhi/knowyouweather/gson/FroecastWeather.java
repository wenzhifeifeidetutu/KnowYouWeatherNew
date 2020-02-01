package com.wenzhi.knowyouweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @Description: 3天信息weather
 * @Author: wenzhi
 * @CreateDate: 2020-01-31 15:57
 * @UpdateUser: 无
 * @UpdateDate: 2020-01-31 15:57
 * @UpdateRemark: 无
 * @Version: 1.0
 */
public class FroecastWeather extends BaseWeather {

    @SerializedName("daily_forecast")
    public List<Froecast> froecastList;

}
