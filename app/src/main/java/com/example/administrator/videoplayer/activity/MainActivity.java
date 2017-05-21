package com.example.administrator.videoplayer.activity;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.example.administrator.videoplayer.R;
import com.example.administrator.videoplayer.adapters.RecyclerAdapter;
import com.example.administrator.videoplayer.beans.VideoBeans;
import com.example.administrator.videoplayer.utils.ParserJsonData;
import com.show.api.ShowApiRequest;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private SurfaceView surfaceView;
    private RecyclerView recyclerView;
   private RecyclerAdapter adapter;
    private static  int j=0;
    private ImageButton jilu;
    private static ArrayList<String> info = new ArrayList<String>();
    private static ArrayList<String> havePlayTitle = new ArrayList<String>();
    private static ArrayList<String> havePlayTime = new ArrayList<String>();
    private static ArrayList<String> havePlayUrl = new ArrayList<String>();
    private static ArrayList<String> havePlayName = new ArrayList<String>();
    private static ArrayList<String> havePlayLove = new ArrayList<String>();
    private static ArrayList<String> havePlayHate = new ArrayList<String>();


    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Button getData;
    private LinearLayoutManager mLinearLayoutManager;
    protected Handler mHandler =  new Handler();
    private MediaPlayer player;
    private SurfaceHolder mSurfaceHolderholder;
    private ImageButton playVideo;
    private String uri;
    private List<VideoBeans> itemList;
    private ProgressBar progressBar;

    private Handler handler = new Handler(){
        public void handleMessage(Message message){
            itemList  = (List<VideoBeans>) message.obj;
            adapter.refresh(itemList);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bind();
        setRecyclerView();
        getJsonFromApi();
        setSwipeRefresh();
        jilu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,HavePlayActivity.class);
                intent.putStringArrayListExtra("HavePlayVideoUrl",info);
                intent.putStringArrayListExtra("HavePlayUrl",havePlayUrl);
                intent.putStringArrayListExtra("HavePlayTime",havePlayTime);
                intent.putStringArrayListExtra("HavePlayName",havePlayName);
                intent.putStringArrayListExtra("HavePlayTitle",havePlayTitle);
                intent.putStringArrayListExtra("HavePlayLove",havePlayLove);
                intent.putStringArrayListExtra("HavePlayHate",havePlayHate);
                startActivity(intent);
            }
        });
    }

    private void setSwipeRefresh() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setColorSchemeColors(Color.WHITE,Color.BLACK);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                itemList.clear();
                getJsonFromApi();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }
    private void setRecyclerView() {
        mLinearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        adapter = new RecyclerAdapter(new RecyclerAdapter.CallBack() {
            @Override
            public void onClick(VideoBeans videoBeans) {
                if(videoBeans.getVideoUrl().equals("")){
                    Log.d("MainActivity","失败了");

                }else {
                    Log.d("MainActivity","成功了！");
                    j++;
                    info.add(videoBeans.getVideoUrl());
                    havePlayTitle.add(videoBeans.getTitle());
                    havePlayHate.add(videoBeans.getHate());
                    havePlayLove.add(videoBeans.getLove());
                    havePlayUrl.add(videoBeans.getIconUrl());
                    havePlayName.add(videoBeans.getName());
                    havePlayTime.add(videoBeans.getData());
                }

            }
        });
        recyclerView.setAdapter(adapter);
    }




    private void bind() {
       // surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        jilu = (ImageButton) findViewById(R.id.toolbar_jilu);
        itemList = new ArrayList<>();
    }



    public void getJsonFromApi() {

            new Thread(){
                //在新线程中发送网络请求
                public void run() {
                    String appid="38535";//ID
                    String secret="71da855bfabb45e7be0dbf41a02cdc4a";//密匙
                    final String res=new ShowApiRequest("http://route.showapi.com/255-1",appid,secret)
                            .addTextPara("type","41")
                            .post();
                    ParserJsonData parserJsonData = new ParserJsonData();
                   itemList =  parserJsonData.getJsonData(res);

                    mHandler.post(new Thread(){
                        public void run() {
                           Message message = new Message();
                            message.obj = itemList;
                            handler.sendMessage(message);

                        }
                    });
                }
            }.start();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(itemList==null){
                    }else {
                        adapter.refresh(itemList);
                    }
                }
            });

    }


}

