package com.example.weather.utils;

import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
//单例模式
public class OkhttpUtil {
    private OkhttpUtil(){};
    public static final String URL_WEATHER_WITH_FUTURE="https://v0.yiketianqi.com/api?unescape=1&version=v9&appid=89437145&appsecret=MVHeU3W8";
    private static OkHttpClient client=new OkHttpClient();
    private static OkhttpUtil instance=new OkhttpUtil();
    public static OkhttpUtil getInstance(){
            return instance;

    }
    public static String getWeatherOfCity(String city){

        //拼接出来天气的url
        //https://v0.yiketianqi.com/api?unescape=1&version=v61&appid=89437145 &appsecret=MVHeU3W8
        String weatherUrl=URL_WEATHER_WITH_FUTURE+"&city="+city;
        Request request=new Request.Builder().url(weatherUrl).build();

        Call call=client.newCall(request);
        try {
            Response response = call.execute();
            String result=response.body().string();
            Log.d("okHttp", "---callExcute---: "+result);
            return result;
        } catch (IOException e) {
            e.printStackTrace();

        }
        return null;
    }
};

