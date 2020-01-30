package com.wenzhi.knowyouweather.db;

import org.litepal.crud.DataSupport;

/**
 * @Description: 数据库类，存放省信息
 * @Author: wenzhi
 * @CreateDate: 2020-01-30 13:17
 * @UpdateUser: 无
 * @UpdateDate: 2020-01-30 13:17
 * @UpdateRemark: 无
 * @Version: 1.0
 */
public class Province  extends DataSupport {
    private int id;

    private String provinceName;

    private int provinceCode;


    public int getId() {
        return id;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public String getProvinceName() {
        return provinceName;
    }


    public void setId(int id) {
        this.id = id;
    }


    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }


    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }


}
