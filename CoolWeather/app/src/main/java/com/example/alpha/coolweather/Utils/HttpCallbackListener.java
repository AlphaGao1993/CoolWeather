package com.example.alpha.coolweather.Utils;

/**
 * http回调接口
 * Created by Alpha on 2016/7/19.
 */
public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
