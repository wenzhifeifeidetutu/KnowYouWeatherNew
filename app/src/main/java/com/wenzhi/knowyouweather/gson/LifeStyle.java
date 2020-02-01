package com.wenzhi.knowyouweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * @Description: 生活指数
 * @Author: wenzhi
 * @CreateDate: 2020-01-31 15:49
 * @UpdateUser: 无
 * @UpdateDate: 2020-01-31 15:49
 * @UpdateRemark: 无
 * @Version: 1.0
 */
public class LifeStyle {

    public String type;

    @SerializedName("brf")
    public String typeText;

    @SerializedName("txt")
    public String sugessionText;


}
