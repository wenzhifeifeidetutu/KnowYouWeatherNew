package com.wenzhi.knowyouweather.util;

import android.text.TextUtils;


import com.wenzhi.knowyouweather.db.City;
import com.wenzhi.knowyouweather.db.County;
import com.wenzhi.knowyouweather.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @Description: 处理返回数据类
 * @Author: wenzhi
 * @CreateDate: 2020-01-30 14:16
 * @UpdateUser: 无
 * @UpdateDate: 2020-01-30 14:16
 * @UpdateRemark: 无
 * @Version: 1.0
 */
public class Utility {
//    处理返回省级数据
    public static boolean handleProvinceResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allProvinces = new JSONArray(response);
                for (int i = 0; i < allProvinces.length(); i++) {
                    JSONObject jsonObject = allProvinces.getJSONObject(i);

                    Province province = new Province();
                    province.setProvinceName(jsonObject.getString("name"));
                    province.setProvinceCode(jsonObject.getInt("id"));
                    //储存到数据库
                    province.save();
                }

                return true;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return false;
    }


    //返回城市数据

    public static boolean handleCityResponse(String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCitys = new JSONArray(response);

                for (int i =0 ; i < allCitys.length(); i++) {
                    JSONObject cityJson = allCitys.getJSONObject(i);

                    City city = new City();

                    city.setCityName(cityJson.getString("name"));
                    city.setCityCode(cityJson.getInt("id"));
                    city.setProvinceID(provinceId);

                    city.save();
                }

                return true;
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return false;

    }

    //县级数据

    public static boolean handleCountyResponse(String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allcountys = new JSONArray(response);

                for (int i =0 ; i <allcountys.length(); i++) {
                    JSONObject jsonObject = allcountys.getJSONObject(i);

                    County county = new County();

                    county.setCountyName(jsonObject.getString("name"));
                    county.setWeatherId(jsonObject.getString("weather_id"));

                    county.setCityId(cityId);

                    county.save();

                }



                return true;
            }catch (JSONException e ) {
                e.printStackTrace();
            }
        }

        return false;

    }

}
