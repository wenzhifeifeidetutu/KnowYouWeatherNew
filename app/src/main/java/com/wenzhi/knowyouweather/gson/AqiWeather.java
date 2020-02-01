package com.wenzhi.knowyouweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * @Description: 空气质量天气
 * @Author: wenzhi
 * @CreateDate: 2020-02-01 14:55
 * @UpdateUser: 无
 * @UpdateDate: 2020-02-01 14:55
 * @UpdateRemark: 无
 * @Version: 1.0
 */
public class AqiWeather extends BaseWeather {

    @SerializedName("air_now_city")
    public AirNowCity airNowCity;

}
