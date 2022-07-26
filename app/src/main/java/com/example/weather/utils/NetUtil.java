package com.example.weather.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetUtil {
    public static final String TAG="TAG";

    public static final String URL_WEATHER_WITH_FUTURE="https://v0.yiketianqi.com/api?unescape=1&version=v9&appid=89437145&appsecret=MVHeU3W8";

//    public static String doGet(String urlStr){
//        String result="";
//        HttpURLConnection connection=null;
//        InputStreamReader inputStreamReader=null;
//        BufferedReader bufferedReader=null;
//        //连接网络
//        try {
//            URL url=new URL(urlStr);
//            connection = (HttpURLConnection)url.openConnection();
//            connection.setRequestMethod("GET");
//            connection.setConnectTimeout(5000);
//            //从连接中读取数据(二进制流)
//            InputStream inputStream = connection.getInputStream();
//            inputStreamReader=new InputStreamReader(inputStream);
//            //弄个缓冲区，一部分一部分来处理读取的数据
//            bufferedReader=new BufferedReader(inputStreamReader);
//            //+拼接字符串效率很低，用Stringbulider
//            StringBuilder stringBuilder=new StringBuilder();
//            String line = "";
//            while((line= bufferedReader.readLine())!=null){
//                stringBuilder.append(line);
//            }
//            result =stringBuilder.toString();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }finally {
//            if(connection!=null){
//                connection.disconnect();
//            }
//            if(inputStreamReader!=null){
//                try {
//                    inputStreamReader.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            if(bufferedReader!=null){
//                try {
//                    bufferedReader.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return result;
//    }
    public static String getWeatherOfCity(String city){
        OkHttpClient client=new OkHttpClient();
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
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                Log.d(TAG, "onFailure: "+"请求失败！");
//            }
//
//            @Override
//            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                String data = response.body().string();
//                Log.d("okHttp", "---callEnqueue---: "+data);
//            }
//        });
        return null;
//
//        Log.d("fan","------weatherURL------"+weatherUrl);
//        String result=doGet(weatherUrl);
//        Log.d("fan","------weatherResult------"+result);
//        return result;
    }
}
