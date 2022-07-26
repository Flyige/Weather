package com.example.weather.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.weather.Beans.NoteBean;
import com.example.weather.NoteDbOpenHelper;
import com.example.weather.R;
import com.example.weather.utils.ToastUtil;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddActivity extends AppCompatActivity {
    private EditText etTitle,etContent;
    private Button mAddButton;
    private NoteDbOpenHelper mNoteDbOpenHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        etContent=findViewById(R.id.et_content);
        etTitle=findViewById(R.id.et_title);
        mAddButton=findViewById(R.id.bt_EditAdd);
        mNoteDbOpenHelper=new NoteDbOpenHelper(this);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title=etTitle.getText().toString();
                String content=etContent.getText().toString();
                if(TextUtils.isEmpty(title)){
                    ToastUtil.toastShort(AddActivity.this,"内容不能为空");
                }else {
                    NoteBean note=new NoteBean();
                    note.setContent(content);
                    note.setTitle(title);
                    note.setCreatedTime(getCurrentTimeFormat());
                    long row=mNoteDbOpenHelper.insertData(note);
                    if(row!=-1){
                        ToastUtil.toastShort(AddActivity.this,"添加成功");
                        AddActivity.this.finish();
                    }else{
                        ToastUtil.toastShort(AddActivity.this,"添加失败");
                    }
                }
            }
        });
    }

    private String getCurrentTimeFormat() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd HH:mm:ss");
        Date date = new Date();
        return simpleDateFormat.format(date);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}