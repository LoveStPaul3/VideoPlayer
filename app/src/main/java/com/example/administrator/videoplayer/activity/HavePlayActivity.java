package com.example.administrator.videoplayer.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.example.administrator.videoplayer.R;
import com.example.administrator.videoplayer.adapters.RecyclerAdapter;
import com.example.administrator.videoplayer.beans.VideoBeans;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 我 on 2017/5/21.
 */

public class HavePlayActivity extends AppCompatActivity {
    private static ArrayList<String> info = new ArrayList<String>();
    private static ArrayList<String> havePlayTitle = new ArrayList<String>();
    private static ArrayList<String> havePlayTime = new ArrayList<String>();
    private static ArrayList<String> havePlayUrl = new ArrayList<String>();
    private static ArrayList<String> havePlayName = new ArrayList<String>();
    private static ArrayList<String> havePlayLove = new ArrayList<String>();
    private static ArrayList<String> havePlayHate = new ArrayList<String>();
    private static List<VideoBeans> sVideoBeansList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    private LinearLayoutManager mLinearLayoutManager;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_haveplay);
        recyclerView = (RecyclerView)findViewById(R.id.haveplay_recycler);
        getIntentData();
        setRecyclerView();

       adapter.refresh(sVideoBeansList);

    }
    private void getIntentData() {
        sVideoBeansList.clear();
        Intent intent = getIntent();
        info = getIntent().getStringArrayListExtra("HavePlayVideoUrl");
        havePlayUrl = getIntent().getStringArrayListExtra("HavePlayUrl");
        havePlayTime = getIntent().getStringArrayListExtra("HavePlayTime");
        havePlayName = getIntent().getStringArrayListExtra("HavePlayName");
        havePlayTitle = getIntent().getStringArrayListExtra("HavePlayTitle");
        havePlayLove = getIntent().getStringArrayListExtra("HavePlayLove");
        havePlayHate = getIntent().getStringArrayListExtra("HavePlayHate");
        for(int i=0;i<info.size();i++){
            VideoBeans videoBeans = new VideoBeans();
            videoBeans.setHate(havePlayHate.get(i));
            videoBeans.setLove(havePlayLove.get(i));
            videoBeans.setTitle(havePlayTitle.get(i));
            videoBeans.setName(havePlayName.get(i));
            videoBeans.setData(havePlayTime.get(i));
            videoBeans.setIconUrl(havePlayUrl.get(i));
            videoBeans.setVideoUrl(info.get(i));
            sVideoBeansList.add(videoBeans);
        }
    }
    private void setRecyclerView() {
        mLinearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        adapter = new RecyclerAdapter(new RecyclerAdapter.CallBack() {
            @Override
            public void onClick(VideoBeans videoBeans) {
            }
        });
        recyclerView.setAdapter(adapter);
    }
}
