package com.wenzhi.knowyouweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * @Description: 现在的天气json
 * @Author: wenzhi
 * @CreateDate: 2020-01-31 14:50
 * @UpdateUser: 无
 * @UpdateDate: 2020-01-31 14:50
 * @UpdateRemark: 无
 * @Version: 1.0
 */
public class Now {
    @SerializedName("tmp")
    public String templerature;

    @SerializedName("cond_txt")
    public String weatherText;

    @SerializedName("wind_dir")
    public String windText;

}
