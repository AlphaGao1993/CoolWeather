package com.example.alpha.coolweather.Utils;

import com.orhanobut.logger.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 网络请求工具
 * Created by Alpha on 2016/7/19.
 */
public class HttpUtil {
    public static void sendHttpRequest(final String path,final HttpCallbackListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection=null;
                try {
                    URL mUrl=new URL(path);
                    connection= (HttpURLConnection) mUrl.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(10000);
                    connection.setReadTimeout(15000);
                    int code=connection.getResponseCode();
                    if (code==200){
                        InputStream in=connection.getInputStream();
                        BufferedReader reader=new BufferedReader(new InputStreamReader(in));
                        StringBuilder response=new StringBuilder();
                        String line;
                        while ((line=reader.readLine())!=null){
                            response.append(line);
                        }
                        if (listener!=null){
                            listener.onFinish(response.toString());
                        }
                        in.close();
                    }else {
                        Logger.d("网络连接失败");
                    }
                } catch (java.io.IOException e) {
                    if (listener!=null){
                        listener.onError(e);
                    }
                    e.printStackTrace();
                } finally {
                    if (connection!=null){
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
}
