package com.wenzhi.knowyouweather;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.wenzhi.knowyouweather.db.City;
import com.wenzhi.knowyouweather.db.County;
import com.wenzhi.knowyouweather.db.Province;
import com.wenzhi.knowyouweather.util.HttpUtil;
import com.wenzhi.knowyouweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * @Description: 遍历城市县的数据碎片
 * @Author: wenzhi
 * @CreateDate: 2020-01-30 18:55
 * @UpdateUser: 无
 * @UpdateDate: 2020-01-30 18:55
 * @UpdateRemark: 无
 * @Version: 1.0
 */
public class ChooseAreaFragment extends Fragment {

    //三种状态
    public static final  int LEVEL_PROVINCE = 0;

    public static final int LEVEL_CITY = 1;

    public static final int LEVEL_COUNTY = 2;


    //状态栏

    private ProgressDialog progressDialog;

    private TextView titileText;

    private Button backButton;

    private ListView listView;

    private ArrayAdapter<String> arrayAdapter;

    private List<String> dataList = new ArrayList<>();

    //省列表
    private List<Province> provinceList;

    //市列表
    private List<City> cityList;

    //县列表
    private List<County> countyList;


    //选中的省
    private Province selectedProvince;

    //选中的城市
    private City selectedCity;
    //当前城市名称
    private String currentCityName;

    //当前选中的级别

    private int currentLevel ;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);

        titileText =(TextView) view.findViewById(R.id.title_text);
        backButton = (Button)view.findViewById(R.id.back_button);
        listView = (ListView)view.findViewById(R.id.list_view);

        arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);

        listView.setAdapter(arrayAdapter);

        return view;


    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    queryCities();
                }else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    currentCityName = selectedCity.getCityName();
                    queryCounties();
                }else if (currentLevel == LEVEL_COUNTY) {
                    String weatherId = countyList.get(position).getWeatherId();
                    //设置sheardpreference
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("weather_id", weatherId);
                    editor.apply();
                    if (getActivity() instanceof MainActivity) {
                        //如果是在主界面就会
                        Intent intent = new Intent(getActivity(), WeatherActivity.class);
                        intent.putExtra("weather_id", weatherId);
                        startActivity(intent);
                        getActivity().finish();
                        Log.d("myweather", "主界面onItemClick: "+weatherId);
                    }else if (getActivity() instanceof WeatherActivity){
                        //如果是在天气界面
                        WeatherActivity activity = (WeatherActivity) getActivity();
                        activity.drawerLayout.closeDrawers();
                        //进行刷新
                        activity.swipeRefreshLayout.setRefreshing(true);
                        activity.requestWeather(weatherId);
                        Log.d("myweather", "天气界面onItemClick: "+weatherId);

                    }

                }
            }
        });

//        设置后退
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_COUNTY) {
                    queryCities();
                }else if (currentLevel == LEVEL_CITY) {
                    queryProvinces();
                }
            }
        });

        queryProvinces();

    }
    //查询省

    private void queryProvinces() {
        //标题显示
        titileText.setText("中国");

        backButton.setVisibility(View.GONE);

        provinceList = DataSupport.findAll(Province.class);

        if (provinceList.size() > 0) {
            dataList.clear();

            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());

            }

            arrayAdapter.notifyDataSetChanged();
            listView.setSelection(0);

            currentLevel = LEVEL_PROVINCE;
        }else {
            //数据库里面没有数据
            String address = "http://guolin.tech/api/china";
            queryFromServerce(address, "province");
        }
    }

    //查询城市
    private void queryCities() {
        titileText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceid = ?", String.valueOf(selectedProvince.getId())).find(City.class);
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }

            arrayAdapter.notifyDataSetChanged();

            listView.setSelection(0);

            currentLevel = LEVEL_CITY;
        }else {
            int provinceCode = selectedProvince.getProvinceCode();

            String address  = "http://guolin.tech/api/china/"+ provinceCode;

            queryFromServerce(address, "city");
        }

    }



    //查询县
    private void queryCounties() {
        titileText.setText(selectedCity.getCityName());

        backButton.setVisibility(View.VISIBLE);

        countyList = DataSupport.where("cityid = ?", String.valueOf(selectedCity.getId())).find(County.class);

        if (countyList.size() > 0) {
            dataList.clear();

            for (County county : countyList) {
                dataList.add(county.getCountyName());

            }

            arrayAdapter.notifyDataSetChanged();

            listView.setSelection(0);

            currentLevel = LEVEL_COUNTY;

        }else {
            int proviceCode = selectedProvince.getProvinceCode();

            int cityCode = selectedCity.getCityCode();

            String address  = "http://guolin.tech/api/china/"+proviceCode+"/"+cityCode;

            queryFromServerce(address, "county");
        }
    }




    //在服务器商查询数据
    private void queryFromServerce(String address, final String area) {
        showProgressDialog();

        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //通过runUi方法回到主线
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();

                        Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();

                boolean result = false;

                if ("province".equals(area)) {
                    result = Utility.handleProvinceResponse(responseText);
                }else if ("city".equals(area)){
                    result = Utility.handleCityResponse(responseText, selectedProvince.getId());

                }else if ("county".equals(area)) {
                    result = Utility.handleCountyResponse(responseText, selectedCity.getId());

                }

                //重新载入一遍数据
                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(area)) {
                                queryProvinces();
                            }else if ("city".equals(area)) {
                                queryCities();
                            }else if ("county".equals(area)) {
                                queryCounties();
                            }
                        }
                    });
                }
            }
        });
    }

    //关闭dialog

    private void closeProgressDialog() {

        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    //显示状态dialog
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载....");
            progressDialog.setCanceledOnTouchOutside(false);
        }

        progressDialog.show();
    }
}
