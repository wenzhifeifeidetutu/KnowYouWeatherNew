package com.wenzhi.knowyouweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * @Description: 详细天气描述
 * @Author: wenzhi
 * @CreateDate: 2020-01-31 15:45
 * @UpdateUser: 无
 * @UpdateDate: 2020-01-31 15:45
 * @UpdateRemark: 无
 * @Version: 1.0
 */
public class Froecast {
    public String date;

    @SerializedName("tmp_max")
    public String temMax;

    @SerializedName("tmp_min")
    public String temMin;

    @SerializedName("cond_txt_d")
    public String weatherTextDay;


    @SerializedName("cond_txt_n")
    public String weatherTextNight;




}
