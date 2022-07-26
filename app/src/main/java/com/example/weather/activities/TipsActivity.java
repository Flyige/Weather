package com.example.weather.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.weather.Beans.DayWeatherBean;
import com.example.weather.R;
import com.example.weather.adapter.TipsAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;

public class TipsActivity extends AppCompatActivity {
    private static final String TAG = "TAG";
    private RecyclerView rlvTips;
    private TipsAdapter mTipsAdapter;

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);
        rlvTips = findViewById(R.id.rlv_tips);

//        Intent intent = getIntent();
//        DayWeatherBean weatherBean = (DayWeatherBean) intent.getSerializableExtra("tips");
//        if (weatherBean == null) {
//            return;
//        }
//        mTipsAdapter = new TipsAdapter(this,weatherBean.getTipsBeans());
//        rlvTips.setAdapter(mTipsAdapter);
//        rlvTips.setLayoutManager(new LinearLayoutManager(this));

    }
    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void onTipsEvent(DayWeatherBean todayWeather){
        if (todayWeather == null) {
            Log.d(TAG, "onTipsEvent-------: "+todayWeather);
            return;
        }
        Log.d(TAG, "onTipsEvent: todayWeather is not null");
        mTipsAdapter = new TipsAdapter(this,todayWeather.getTipsBeans());
        rlvTips.setAdapter(mTipsAdapter);
        rlvTips.setLayoutManager(new LinearLayoutManager(this));
    }
    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();

    }
}