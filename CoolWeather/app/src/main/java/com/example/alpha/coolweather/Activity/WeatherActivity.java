package com.example.alpha.coolweather.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alpha.coolweather.Model.Weather;
import com.example.alpha.coolweather.R;
import com.example.alpha.coolweather.Services.AutoUpdateService;
import com.example.alpha.coolweather.UI.OvalDegree;
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

    @BindView(R.id.date_1)
    TextView date1;
    @BindView(R.id.wind_direction1)
    TextView windDirection1;
    @BindView(R.id.wind_li1)
    TextView windLi1;
    @BindView(R.id.type_1)
    TextView type1;
    @BindView(R.id.wendu_1)
    TextView wendu1;
    @BindView(R.id.date_2)
    TextView date2;
    @BindView(R.id.wind_direction2)
    TextView windDirection2;
    @BindView(R.id.wind_li2)
    TextView windLi2;
    @BindView(R.id.type_2)
    TextView type2;
    @BindView(R.id.wendu_2)
    TextView wendu2;
    @BindView(R.id.date_3)
    TextView date3;
    @BindView(R.id.wind_direction3)
    TextView windDirection3;
    @BindView(R.id.wind_li3)
    TextView windLi3;
    @BindView(R.id.type_3)
    TextView type3;
    @BindView(R.id.wendu_3)
    TextView wendu3;
    @BindView(R.id.date_4)
    TextView date4;
    @BindView(R.id.wind_direction4)
    TextView windDirection4;
    @BindView(R.id.wind_li4)
    TextView windLi4;
    @BindView(R.id.type_4)
    TextView type4;
    @BindView(R.id.wendu_4)
    TextView wendu4;
    @BindView(R.id.tips_ganmao)
    TextView tipsGanmao;
    @BindView(R.id.oval_weather)
    OvalDegree ovalWeather;
    @BindView(R.id.city_name)
    TextView cityName;
    @BindView(R.id.publish_text)
    TextView publishText;
    @BindView(R.id.refeash_view)
    SwipeRefreshLayout refeashView;

    private SharedPreferences sh;
    private String mWeathercode;
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
            queryWeatherCode(countyCode);
        } else {
            showWeather();
        }

        refeashView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refeashView.setRefreshing(false);
                new Thread(){
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                            String weather_code = sh.getString("weather_code", "");
                            if (!TextUtils.isEmpty(weather_code)) {
                                queryWeatherInfo(weather_code);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
        //getCpuInfo();
    }

/*    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
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
    }*/

    private void queryWeatherInfo(String weather_code) {
        SharedPreferences.Editor editor = sh.edit();
        editor.putString("weather_code", mWeathercode);
        editor.apply();
        String address = "http://wthrcdn.etouch.cn/weather_mini?citykey=" + weather_code;
        queryfromServer(address, "weathercode");
        Logger.d(address);
    }

    private void showWeather() {
        showTodayWeather();
        showFutuerWeathwr();
    }

    private void showFutuerWeathwr() {
        Weather weather = Utility.getWeatherInfo();
        if (weather != null) {
            try {
                date1.setText(weather.getData().getForecast().get(1).getDate());
                date2.setText(weather.getData().getForecast().get(2).getDate());
                date3.setText(weather.getData().getForecast().get(3).getDate());
                date4.setText(weather.getData().getForecast().get(4).getDate());

                windDirection1.setText(weather.getData().getForecast().get(1).getFengxiang());
                windDirection2.setText(weather.getData().getForecast().get(2).getFengxiang());
                windDirection3.setText(weather.getData().getForecast().get(3).getFengxiang());
                windDirection4.setText(weather.getData().getForecast().get(4).getFengxiang());

                windLi1.setText(weather.getData().getForecast().get(1).getFengli());
                windLi2.setText(weather.getData().getForecast().get(2).getFengli());
                windLi3.setText(weather.getData().getForecast().get(3).getFengli());
                windLi4.setText(weather.getData().getForecast().get(4).getFengli());

                type1.setText(weather.getData().getForecast().get(1).getType());
                type2.setText(weather.getData().getForecast().get(2).getType());
                type3.setText(weather.getData().getForecast().get(3).getType());
                type4.setText(weather.getData().getForecast().get(4).getType());

                wendu1.setText(weather.getData().getForecast().get(1).getLow().substring(3)
                        + " ~ " + weather.getData().getForecast().get(1).getHigh().substring(3));
                wendu2.setText(weather.getData().getForecast().get(2).getLow().substring(3)
                        + " ~ " + weather.getData().getForecast().get(2).getHigh().substring(3));
                wendu3.setText(weather.getData().getForecast().get(3).getLow().substring(3)
                        + " ~ " + weather.getData().getForecast().get(3).getHigh().substring(3));
                wendu4.setText(weather.getData().getForecast().get(4).getLow().substring(3)
                        + " ~ " + weather.getData().getForecast().get(4).getHigh().substring(3));
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "获取数据失败，请稍后重试", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showTodayWeather() {
        cityName.setText(sh.getString("city_name", ""));
        ovalWeather.setMinTemperature(Integer.parseInt(sh.getString("temp1", "").substring(1,3)));
        //temp1.setText(sh.getString("temp1", ""));
        ovalWeather.setMaxTemperature(Integer.parseInt(sh.getString("temp2", "").substring(1,3)));
        //temp2.setText(sh.getString("temp2", ""));
        ovalWeather.setWeatherType(sh.getString("weather_desp", ""));
        //weatherDisp.setText(sh.getString("weather_desp", ""));
        publishText.setText(sh.getString("publish_time", "") + "更新");
        //currentDate.setText(sh.getString("current_time", ""));
        tipsGanmao.setText(sh.getString("ganmao", ""));
        ovalWeather.setCurrentTemperature(Integer.parseInt(sh.getString("wendu", "0")));
        //wenduToday.setText(sh.getString("wendu", ""));
        //weatherInfoLayout.setVisibility(View.VISIBLE);
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
        Logger.d(countyCode);
    }

    private void queryfromServer(final String address, final String type) {
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Logger.d(type);
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
