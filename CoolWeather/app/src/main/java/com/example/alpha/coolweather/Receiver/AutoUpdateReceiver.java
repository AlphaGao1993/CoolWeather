package com.example.alpha.coolweather.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.alpha.coolweather.Services.AutoUpdateService;

/**
 * 自动更新广播接收
 * Created by Alpha on 2016/7/20.
 */
public class AutoUpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i=new Intent(context, AutoUpdateService.class);
        context.startService(i);
    }
}
