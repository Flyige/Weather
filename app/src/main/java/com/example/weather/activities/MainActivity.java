package com.example.weather.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.weather.Beans.DayWeatherBean;
import com.example.weather.Beans.WeatherBean;
import com.example.weather.R;
import com.example.weather.adapter.FutureWeatherAdapter;
import com.example.weather.utils.NetUtil;
import com.example.weather.utils.OkhttpUtil;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class MainActivity extends AppCompatActivity {
    private LinearLayout mMainlayout;
    private AppCompatSpinner mSpinner;
    private ArrayAdapter<String> mSpAdapter;
    private String[] mCities;
    private Button mNoteBook,btCamera;
    private TextView tvWeather, tvTem, tvTemLowHigh, tvWin, tvAir,tvDate;
    private ImageView ivWeather;
    private RecyclerView rlvFutureWeather;
    private FutureWeatherAdapter mWeatherAdapter;
    private DayWeatherBean todayWeather;
    private Uri imageUri;
    public static final int REQUEST_CODE_TAKE = 1;
    private Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                String weather = (String) msg.obj;
                Log.d("fan", "--主线程收到了天气数据-weather---" + weather);
                if (TextUtils.isEmpty(weather)) {
                    Toast.makeText(MainActivity.this, "天气数据为空！", Toast.LENGTH_LONG).show();
                    return;
                }
                Gson gson = new Gson();
                WeatherBean weatherBean = gson.fromJson(weather, WeatherBean.class);
                if (weatherBean != null) {
                    Log.d("fan", "--解析后的数据-weather---" + weatherBean.toString());
                }
                updateUiOfWeather(weatherBean);
                Toast.makeText(MainActivity.this, "更新天气~", Toast.LENGTH_SHORT).show();
            }
        }
    };
    private void updateUiOfWeather(WeatherBean weatherBean) {
        if (weatherBean == null) {
            return;
        }

        List<DayWeatherBean> dayWeathers = weatherBean.getDayWeathers();
        todayWeather = dayWeathers.get(0);
        if (todayWeather == null) {
            return;
        }

        tvTem.setText(todayWeather.getTem());
        tvWeather.setText(todayWeather.getWea() + "(" + todayWeather.getDate() + todayWeather.getWeek() + ")");
        tvTemLowHigh.setText(todayWeather.getTem2() + "~" + todayWeather.getTem1());
        tvWin.setText(todayWeather.getWin()[0] + todayWeather.getWinSpeed());
        tvAir.setText("空气:" + todayWeather.getAir() + todayWeather.getAirLevel() + "\n" + todayWeather.getAirTips());
//        tvDate.setText(todayWeather.getDate());
        ivWeather.setImageResource(getImgResOfWeather(todayWeather.getWeaImg()));

        dayWeathers.remove(0); // 去掉当天的天气

        // 未来天气的展示
        mWeatherAdapter = new FutureWeatherAdapter(this, dayWeathers);
        rlvFutureWeather.setAdapter(mWeatherAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rlvFutureWeather.setLayoutManager(layoutManager);

    }

    private int getImgResOfWeather(String weaStr) {
        // xue、lei、shachen、wu、bingbao、yun、yu、yin、qing
        int result = 0;
        switch (weaStr) {
            case "qing":
                result = R.drawable.biz_plugin_weather_qing;
                break;
            case "yin":
                result = R.drawable.biz_plugin_weather_yin;
                break;
            case "yu":
                result = R.drawable.biz_plugin_weather_dayu;
                break;
            case "yun":
                result = R.drawable.biz_plugin_weather_duoyun;
                break;
            case "bingbao":
                result = R.drawable.biz_plugin_weather_leizhenyubingbao;
                break;
            case "wu":
                result = R.drawable.biz_plugin_weather_wu;
                break;
            case "shachen":
                result = R.drawable.biz_plugin_weather_shachenbao;
                break;
            case "lei":
                result = R.drawable.biz_plugin_weather_leizhenyu;
                break;
            case "xue":
                result = R.drawable.biz_plugin_weather_daxue;
                break;
            default:
                result = R.drawable.biz_plugin_weather_qing;
                break;
        }

        return result;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        initView();
        initEvent();
    }

    private void initEvent() {

    }

    private void initView() {
        mSpinner = findViewById(R.id.sp_city);
        btCamera=findViewById(R.id.bt_photo);
        btCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePhoto(v);
            }
        });
        mNoteBook=findViewById(R.id.bt_noteBook);
        mNoteBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Log.d("button","-----this button is clicked-----");
                Intent intent=new Intent(MainActivity.this,NoteBookActivity.class);
                startActivity(intent);

            }
        });
        mCities = getResources().getStringArray(R.array.cities);
        mSpAdapter = new ArrayAdapter<>(this, R.layout.sp_item_layout, mCities);
        mSpinner.setAdapter(mSpAdapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCity = mCities[position];

                getWeatherOfCity(selectedCity);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        tvWeather = findViewById(R.id.tv_weather);
        tvAir = findViewById(R.id.tv_air);
        tvTem = findViewById(R.id.tv_tem);
        tvTemLowHigh = findViewById(R.id.tv_tem_low_high);
        tvWin = findViewById(R.id.tv_win);
        ivWeather = findViewById(R.id.iv_weather);
        tvDate=findViewById(R.id.tv_date);
        rlvFutureWeather = findViewById(R.id.rlv_future_weather);

        tvAir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TipsActivity.class);
                // 将数据传递给tipsActivity
                EventBus.getDefault().postSticky(todayWeather);
                startActivity(intent);

            }
        });
        //拿glide获取网络照片作为壁纸
        mMainlayout=findViewById(R.id.mainActivity);
        Glide.with(getApplicationContext()).load("https://images.pexels.com/photos/3888585/pexels-photo-3888585.jpeg").into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                mMainlayout.setBackground(resource);
            }
            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        });

    }

    private void choosePhoto(View view) {


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==1){
            if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                doCamera();
            }else {
                Toast.makeText(this,"你没有货的权限",Toast.LENGTH_SHORT).show();
            }
        }
    }

    //拍照
    private void doCamera() {
        File outputImg=new File(getExternalCacheDir(),"output_image.jpg");
        if(outputImg.exists()){
            outputImg.delete();
        }
        try {
            outputImg.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //判断系统版本，低于7.0会将file对象转换为uir对象否则调用getUriForFile将file对象转化为一个封装过的uir对象
        //因为7.0开始直接使用本地真实路径会被认为是不安全的会抛出FileUirExposeption异常，FileProvider是一个
        //内容提供器会将封装的uir提供给外部

        if(Build.VERSION.SDK_INT>=24){
            //ContentProvider的用法，四大组件之一，把自己的内容提供给其他，保障安全性
            imageUri= FileProvider.getUriForFile(MainActivity.this,"com.example.weather.fileprovider",outputImg);
        }else {
            imageUri = Uri.fromFile(outputImg);
        }
        Intent intent=new Intent();
        intent.setAction("android.media.action.IMAGE_CAPUTRE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        startActivityForResult(intent,REQUEST_CODE_TAKE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE_TAKE){
            if(resultCode==RESULT_OK){
                //获取拍摄照片
                try {
                    InputStream inputStream = getContentResolver().openInputStream(imageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
                    mMainlayout.setBackground(bitmapDrawable);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void getWeatherOfCity(String selectedCity) {
        // 开启子线程，请求网络
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 请求网络
//                String weatherOfCity = NetUtil.getWeatherOfCity(selectedCity);
                try {
                    String weatherOfCity = OkhttpUtil.getInstance().getWeatherOfCity(selectedCity);
                    // 使用handler将数据传递给主线程
//                    Message message=mHandler.obtainMessage();
                    Message message = Message.obtain();
                    message.what = 0;
                    message.obj = weatherOfCity;
                    mHandler.sendMessage(message);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
}