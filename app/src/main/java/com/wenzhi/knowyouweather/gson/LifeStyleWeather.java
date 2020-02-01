package com.wenzhi.knowyouweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @Description: 生活指数weather
 * @Author: wenzhi
 * @CreateDate: 2020-01-31 15:55
 * @UpdateUser: 无
 * @UpdateDate: 2020-01-31 15:55
 * @UpdateRemark: 无
 * @Version: 1.0
 */
public class LifeStyleWeather extends BaseWeather{

    @SerializedName("lifestyle")
    public List<LifeStyle> lifeStyleList;

}
