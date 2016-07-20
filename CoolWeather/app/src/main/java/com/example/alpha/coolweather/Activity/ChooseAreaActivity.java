package com.example.alpha.coolweather.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alpha.coolweather.DB.CoolWeatherDB;
import com.example.alpha.coolweather.Model.City;
import com.example.alpha.coolweather.Model.County;
import com.example.alpha.coolweather.Model.Province;
import com.example.alpha.coolweather.R;
import com.example.alpha.coolweather.Utils.HttpCallbackListener;
import com.example.alpha.coolweather.Utils.HttpUtil;
import com.example.alpha.coolweather.Utils.Utility;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * 选择区域界面
 * Created by Alpha on 2016/7/19.
 */
public class ChooseAreaActivity extends AppCompatActivity {
    public static final int LEVEL_PROVINCE=1;
    public static final int LEVEL_CITY=2;
    public static final int LEVEL_COUNTY=3;

    private ProgressDialog progressDialog;
    private TextView mTitleText;
    private ListView mListView;

    private ArrayAdapter<String> adapter;//数据适配器
    private CoolWeatherDB coolWeatherDB;
    private List<String> datalist=new ArrayList<>();//数据容器

    private List<Province> provinceList;//省集合
    private List<City> cityList;//市集合
    private List<County> countyList;//县集合

    private Province selectedProvince;//当前选择的省份
    private City selectedCity;//当前选择的城市
    private County selectedCounty;

    private int currentLevel;//当前区域级别

    private Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        intent=getIntent();

        mListView= (ListView) findViewById(R.id.list_view);
        mTitleText= (TextView) findViewById(R.id.title_text);

        //设置适配器
        adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,datalist);
        mListView.setAdapter(adapter);
        coolWeatherDB=CoolWeatherDB.getInsteance(this);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel==LEVEL_PROVINCE){
                    selectedProvince=provinceList.get(position);
                    querryCities();
                }else if (currentLevel==LEVEL_CITY){
                    selectedCity=cityList.get(position);
                    querryCounties();
                }else if (currentLevel==LEVEL_COUNTY){
                    String countycode=countyList.get(position).getCode();
                    Intent intent=new Intent(ChooseAreaActivity.this,WeatherActivity.class);
                    intent.putExtra("county_code",countycode);
                    startActivity(intent);
                    finish();
                }
            }
        });
        querryProvinces();
    }

    private void querryProvinces() {
        provinceList=coolWeatherDB.loadProvinces();
        if (provinceList.size()>0){
            datalist.clear();
            for (Province p:provinceList){
                datalist.add(p.getName());
            }
            adapter.notifyDataSetChanged();
            mListView.setSelection(0);
            mTitleText.setText("中国");
            currentLevel=LEVEL_PROVINCE;
        }else {
            queryFromServier(null,"province");
        }
    }

    private void queryFromServier(final String code, final String level) {
        String adderss;
        if (!TextUtils.isEmpty(code)){
            adderss="http://www.weather.com.cn/data/list3/city"+code+".xml";
        }else {
            adderss="http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(adderss, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result=false;
                if ("province".equals(level)){
                    result= Utility.handleProvincesResponse(coolWeatherDB,response);
                }else if("city".equals(level)){
                    result=Utility.handleCitiesResponse(coolWeatherDB,response,selectedProvince.getId());
                }else if ("county".equals(level)){
                    result=Utility.handleCountiesResponse(coolWeatherDB,response,selectedCity.getId());
                }
                if (result){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(level)){
                                querryProvinces();
                            }else if ("city".equals(level)){
                                querryCities();
                            }else if ("county".equals(level)){
                                querryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this,"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void closeProgressDialog() {
        if (progressDialog!=null){
            progressDialog.dismiss();
        }
    }

    private void showProgressDialog() {
        if (progressDialog==null){
            progressDialog=new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void querryCounties() {
        countyList=coolWeatherDB.loadCouties(selectedCity.getId());
        if (countyList.size()>0){
            datalist.clear();
            for (County county:countyList){
                datalist.add(county.getName());
            }
            adapter.notifyDataSetChanged();
            mListView.setSelection(0);
            mTitleText.setText(selectedCity.getName());
            currentLevel=LEVEL_COUNTY;
        }else {
            queryFromServier(selectedCity.getCode(),"county");
        }
    }

    private void querryCities() {
        cityList=coolWeatherDB.loadCitys(selectedProvince.getId());
        if (cityList.size()>0){
            datalist.clear();
            for (City city:cityList){
                datalist.add(city.getName());
            }
            adapter.notifyDataSetChanged();
            mListView.setSelection(0);
            mTitleText.setText(selectedProvince.getName());
            currentLevel=LEVEL_CITY;
        }else {
            queryFromServier(selectedProvince.getCode(),"city");
        }
    }

    @Override
    public void onBackPressed() {
        if (currentLevel==LEVEL_COUNTY){
            querryCities();
        }else if (currentLevel==LEVEL_CITY){
            querryProvinces();
        }else {
            if ((intent.getBooleanExtra("from_weather_activity",false))){
                Intent newintent=new Intent(this,WeatherActivity.class);
                startActivity(newintent);
            }
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        if (!intent.getBooleanExtra("from_weather_activity",false)){
            if (sharedPreferences.getBoolean("city_selected",false)){
                Intent intent=new Intent(this,WeatherActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }
}
