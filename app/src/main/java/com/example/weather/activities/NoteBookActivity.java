package com.example.weather.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.app.ActionBar;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.appcompat.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.weather.Beans.NoteBean;
import com.example.weather.NoteDbOpenHelper;
import com.example.weather.R;
import com.example.weather.adapter.NoteBookAdapter;
import com.example.weather.utils.SpfUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NoteBookActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private FloatingActionButton mBtnAdd;
    private List<NoteBean> mNotes;
    private NoteBookAdapter mNoteBookAdapter;
    private FloatingActionButton mFabAddButton;
    private NoteDbOpenHelper mNoteDbOpenHelper;
    private FrameLayout mNotebookLayout;
    public static final int MODE_LINEAR = 0;
    public static final int MODE_GRID = 1;

    public static final String KEY_LAYOUT_MODE = "key_layout_mode";

    private int currentListLayoutMode = MODE_LINEAR;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        item.setChecked(true);

        switch (item.getItemId()) {

            case R.id.menu_linear:
                setToLinearList();
                currentListLayoutMode = MODE_LINEAR;
                SpfUtil.saveInt(this,KEY_LAYOUT_MODE,MODE_LINEAR);

                return true;
            case R.id.menu_grid:

                setToGridList();
                currentListLayoutMode = MODE_GRID;
                SpfUtil.saveInt(this,KEY_LAYOUT_MODE,MODE_GRID);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notebook);
        initView();
        initData();
        initEvent();
    }
    @Override
    protected void onResume() {
        super.onResume();
        refreshDataFromDb();
        setListLayout();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mNotes = mNoteDbOpenHelper.queryFromDbByTitle(newText);
                mNoteBookAdapter.refreshData(mNotes);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (currentListLayoutMode == MODE_LINEAR) {
            MenuItem item = menu.findItem(R.id.menu_linear);
            item.setChecked(true);
        } else {
            menu.findItem(R.id.menu_grid).setChecked(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }
    private void refreshDataFromDb() {
        mNotes = getDataFromDB();
        mNoteBookAdapter.refreshData(mNotes);
    }

    private List<NoteBean> getDataFromDB() {
        return mNoteDbOpenHelper.queryAllFromDb();
    }
    private String getCurrentTimeFormat() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd HH:mm:ss");
        Date date = new Date();
        return simpleDateFormat.format(date);
    }

    private void initEvent() {
        mNoteBookAdapter=new NoteBookAdapter(this,mNotes);
        mRecyclerView.setAdapter(mNoteBookAdapter);
        setListLayout();
    }

    private void setListLayout() {
        currentListLayoutMode= SpfUtil.getIntWithDefault(this, KEY_LAYOUT_MODE, MODE_LINEAR);
        if(currentListLayoutMode==MODE_LINEAR){
            setToLinearList();
        }else{
            setToGridList();
        }
    }

    private void initData() {
        mNotes=new ArrayList<>();
        mNoteDbOpenHelper=new NoteDbOpenHelper(NoteBookActivity.this);
    }

    private void setToLinearList() {
        RecyclerView.LayoutManager linerlayoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(linerlayoutManager);
        Log.d("TAG", "----------NoteBookAdapter常量：: "+NoteBookAdapter.TYPE_LINEAR_LAYOUT);
        mNoteBookAdapter.setViewType(NoteBookAdapter.TYPE_LINEAR_LAYOUT);

    }
    private void setToGridList() {
        RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mNoteBookAdapter.setViewType(NoteBookAdapter.TYPE_GRID_LAYOUT);
        mNoteBookAdapter.notifyDataSetChanged();
    }
    private void initView() {
        mRecyclerView=findViewById(R.id.rlv_noteboook);
        mFabAddButton=findViewById(R.id.fab_addButton);
        mNotebookLayout=findViewById(R.id.layout_notebook);
        mFabAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(NoteBookActivity.this,AddActivity.class);
                startActivity(intent);
            }
        });

    }

}
