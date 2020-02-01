package com.wenzhi.knowyouweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * @Description: 天气请求basic类
 * @Author: wenzhi
 * @CreateDate: 2020-01-30 21:21
 * @UpdateUser: 无
 * @UpdateDate: 2020-01-30 21:21
 * @UpdateRemark: 无
 * @Version: 1.0
 */
public class Basic {
    @SerializedName("cid")
    public String weatherId;

    @SerializedName("location")
    public String locationName;

    @SerializedName("parent_city")

    public String cityName;



    
}
