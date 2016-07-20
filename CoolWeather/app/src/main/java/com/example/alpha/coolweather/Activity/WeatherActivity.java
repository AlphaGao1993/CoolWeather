package com.example.alpha.coolweather.Activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
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
import com.example.alpha.coolweather.Services.AutoUpdateService;
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
        sh = PreferenceManager.getDefaultSharedPreferences(this);
        String countyCode = getIntent().getStringExtra("county_code");
        if (!TextUtils.isEmpty(countyCode)) {
            publishText.setText("同步中...");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityName.setVisibility(View.INVISIBLE);
            queryWeatherCode(countyCode);
        } else {
            showWeather();
        }

        refeashView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refeashView.setRefreshing(true);
                String weather_code = sh.getString("weather_code", "");
                if (!TextUtils.isEmpty(weather_code)) {
                    queryWeatherInfo(weather_code);
                }
            }
        });
        //getCpuInfo();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void getCpuInfo() {
        String sb = "主板：" + Build.BOARD + "\n"
                + "系统启动程序版本号：" + Build.BOOTLOADER + "\n"
                + "系统定制商：" + Build.BRAND + "\n"
                + "cpu指令集：" + Build.CPU_ABI + "\n"
                + "cpu指令集2:" + Build.CPU_ABI2 + "\n"
                + "新版cpu指令集1：" + Build.SUPPORTED_ABIS[0] + "\n"
                + "新版cpu指令集2：" + Build.SUPPORTED_ABIS[1] + "\n"
                + "设置参数：" + Build.DEVICE + "\n"
                + "显示屏参数：" + Build.DISPLAY + "\n"
                + "无线电固件版本：" + Build.getRadioVersion() + "\n"
                + "硬件识别码：" + Build.FINGERPRINT + "\n"
                + "硬件名称：" + Build.HARDWARE + "\n"
                + "HOST:" + Build.HOST + "\n"
                + "修订版本列表：" + Build.ID + "\n"
                + "硬件制造商：" + Build.MANUFACTURER + "\n"
                + "版本：" + Build.MODEL + "\n"
                + "硬件序列号：" + Build.SERIAL + "\n"
                + "手机制造商：" + Build.PRODUCT + "\n"
                + "描述Build的标签：" + Build.TAGS + "\n"
                + "TIME:" + Build.TIME + "\n"
                + "builder类型：" + Build.TYPE + "\n"
                + "USER:" + Build.USER;
        Logger.d(sb);
    }

    private void queryWeatherInfo(String weather_code) {
        SharedPreferences.Editor editor = sh.edit();
        editor.putString("weather_code", mWeathercode);
        editor.apply();
        String address = "http://wthrcdn.etouch.cn/weather_mini?citykey=" + weather_code;
        queryfromServer(address, "weathercode");
    }

    private void showWeather() {
        cityName.setText(sh.getString("city_name", ""));
        temp1.setText(sh.getString("temp1", ""));
        temp2.setText(sh.getString("temp2", ""));
        weatherDisp.setText(sh.getString("weather_desp", ""));
        publishText.setText(sh.getString("publish_time", "") + "更新");
        currentDate.setText(sh.getString("current_date", ""));
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityName.setVisibility(View.VISIBLE);
        if (refeashView != null) {
            refeashView.setRefreshing(false);
        }

        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);

        if (!sh.getBoolean("updated_success", true)) {
            Toast.makeText(this, "该区域暂无天气信息，请选择附近地区", Toast.LENGTH_SHORT).show();
        }
    }

    private void queryWeatherCode(String countyCode) {
        String address = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
        queryfromServer(address, "countycode");
    }

    private void queryfromServer(final String address, final String type) {
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                if ("countycode".equals(type)) {
                    if (!TextUtils.isEmpty(response)) {
                        String[] array = response.split("\\|");
                        if (array.length == 2) {
                            mWeathercode = array[1];
                            queryWeatherInfo(mWeathercode);
                        }
                    }
                } else if ("weathercode".equals(type)) {
                    Logger.d("天气地址：" + address);
                    Utility.handWeatherResponse(WeatherActivity.this, response);
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
        Intent intent = new Intent(this, ChooseAreaActivity.class);
        intent.putExtra("from_weather_activity", true);
        startActivity(intent);
        finish();
    }


}
