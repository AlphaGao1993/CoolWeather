package com.example.alpha.coolweather.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alpha.coolweather.R;
import com.example.alpha.coolweather.Utils.HttpCallbackListener;
import com.example.alpha.coolweather.Utils.HttpUtil;
import com.example.alpha.coolweather.Utils.Utility;
import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 显示天气的界面
 * Created by Alpha on 2016/7/19.
 */
public class WeatherActivity extends AppCompatActivity {

    private SharedPreferences sh;
    private String mWeathercode;

    @BindView(R.id.city_name)
    TextView cityName;
    @BindView(R.id.publish_text)
    TextView publishText;
    @BindView(R.id.current_date)
    TextView currentDate;
    @BindView(R.id.weather_disp)
    TextView weatherDisp;
    @BindView(R.id.temp1)
    TextView temp1;
    @BindView(R.id.temp2)
    TextView temp2;
    @BindView(R.id.weather_info_layout)
    LinearLayout weatherInfoLayout;
    @BindView(R.id.refeash_view)
    SwipeRefreshLayout refeashView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);
        ButterKnife.bind(this);
        sh= PreferenceManager.getDefaultSharedPreferences(this);
        String countyCode=getIntent().getStringExtra("county_code");
        if (!TextUtils.isEmpty(countyCode)){
            publishText.setText("同步中...");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityName.setVisibility(View.INVISIBLE);
            queryWeatherCode(countyCode);
        }else {
            showWeather();
        }

        refeashView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refeashView.setRefreshing(true);
                String weather_code=sh.getString("weather_code","");
                if (!TextUtils.isEmpty(weather_code)){
                    queryWeatherInfo(weather_code);
                }
            }
        });
    }

    private void queryWeatherInfo(String weather_code) {
        SharedPreferences.Editor editor=sh.edit();
        editor.putString("weather_code",mWeathercode);
        editor.apply();
        String address="http://wthrcdn.etouch.cn/weather_mini?citykey="+weather_code;
        queryfromServer(address,"weathercode");
    }

    private void showWeather() {
            cityName.setText(sh.getString("city_name",""));
            temp1.setText(sh.getString("temp1",""));
            temp2.setText(sh.getString("temp2",""));
            weatherDisp.setText(sh.getString("weather_desp",""));
            publishText.setText(sh.getString("publish_time","")+"更新");
            currentDate.setText(sh.getString("current_date",""));
            weatherInfoLayout.setVisibility(View.VISIBLE);
            cityName.setVisibility(View.VISIBLE);
            if (refeashView!=null){
                refeashView.setRefreshing(false);
            }
        if (!sh.getBoolean("updated_success",true)){
            Toast.makeText(this,"该区域暂无天气信息，请选择附近地区",Toast.LENGTH_SHORT).show();
        }
    }

    private void queryWeatherCode(String countyCode) {
        String address="http://www.weather.com.cn/data/list3/city"+countyCode+".xml";
        queryfromServer(address,"countycode");
    }

    private void queryfromServer(final String address,final String type) {
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                if ("countycode".equals(type)){
                    if (!TextUtils.isEmpty(response)){
                        String[] array=response.split("\\|");
                        if (array.length==2){
                            mWeathercode=array[1];
                            queryWeatherInfo(mWeathercode);
                        }
                    }
                }else if ("weathercode".equals(type)){
                    Logger.d("天气地址："+address);
                    Utility.handWeatherResponse(WeatherActivity.this,response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishText.setText("同步失败");
                    }
                });
            }
        });
    }

    @OnClick(R.id.city_name)
    public void onClick() {
        Intent intent=new Intent(this,ChooseAreaActivity.class);
        intent.putExtra("from_weather_activity",true);
        startActivity(intent);
        finish();
    }


}
