package com.example.weather.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.weather.Beans.NoteBean;
import com.example.weather.NoteDbOpenHelper;
import com.example.weather.R;
import com.example.weather.utils.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EditActivity extends AppCompatActivity {

    private static final String TAG = "TAG";
    private NoteBean note;
    private EditText etTitle,etContent;
    private NoteDbOpenHelper mNoteDbOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        etTitle = findViewById(R.id.et_title);
        etContent = findViewById(R.id.et_content);
        initData();
    }
    private void initData() {
        Intent intent = getIntent();
        note = (NoteBean) intent.getSerializableExtra("note");
        if (note != null) {
            etTitle.setText(note.getTitle());
            etContent.setText(note.getContent());
        }
        mNoteDbOpenHelper = new NoteDbOpenHelper(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void save(View view) {
        String title = etTitle.getText().toString();
        String content = etContent.getText().toString();
        if (TextUtils.isEmpty(title)) {
            ToastUtil.toastShort(this, "标题不能为空！");
            this.finish();
        }else{
            note.setTitle(title);
            note.setContent(content);
            note.setCreatedTime(getCurrentTimeFormat());
            long rowId = mNoteDbOpenHelper.updateData(note);
            Log.d(TAG, "----------save---------------: 执行保存了");
            if (rowId != -1) {
                ToastUtil.toastShort(this, "修改成功！");
                this.finish();
            }else{
                ToastUtil.toastShort(this, "修改失败！");
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String getCurrentTimeFormat() {
        SimpleDateFormat sdf = new SimpleDateFormat("xxxx年xx月xx日 HH:MM:SS");
        Date date = new Date();
        return sdf.format(date);
    }
}