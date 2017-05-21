package com.example.administrator.videoplayer.activity;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.videoplayer.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by 我 on 2017/5/20.
 */

public class FullScreamShowVideo extends AppCompatActivity {
    private ArrayList<String> infoList = new ArrayList<String>();
    private int  position;
    private LinearLayout video_ll_title;

    private SurfaceView surfaceView;
    private MediaPlayer player;
    private boolean flag = true;
    private SurfaceHolder holder;
    private ImageButton xiazai;
    private SeekBar seekBar;
    private upDateSeekBar playingSeekBar;
    private ImageButton xiayige,shangyige,outFull;
    TextView fullTime,currentTime;
    private static int playPosition;
    private static  int currentPosition;
    private Context mContext;
    private Button playOrpause;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full);
        getIntentData();
        bindView();
        PlayMovie(position,0);
       setOnclick();
        mContext = getApplicationContext();
    }

    private void setOnclick() {
        xiayige.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                player.stop();
                currentPosition++;//当前播放的位置为下一个视频的位置，并且播放
                PlayMovie(currentPosition,1);

            }
        });
           shangyige.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   if(currentPosition>0){//不是第一个视频

                       player.stop();
                       currentPosition--;
                       PlayMovie(currentPosition,1);
                   }else {
                       Toast.makeText(FullScreamShowVideo.this,"没有上一个视频了，试试下拉刷新吧",Toast.LENGTH_SHORT).show();
                   }
               }
           });
        xiazai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                String videoUrl = infoList.get(position);
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(videoUrl));
                request.setDestinationInExternalFilesDir(FullScreamShowVideo.this,"baisiFile",infoList.get(position));
                request.setTitle(infoList.get(position));
                request.setDescription("请等待");
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                long downloadId = downloadManager.enqueue(request);


            }
        });
             seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                 @Override
                 public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                 }

                 @Override
                 public void onStartTrackingTouch(SeekBar seekBar) {

                 }

                 @Override
                 public void onStopTrackingTouch(SeekBar seekBar) {
                     int value = seekBar.getProgress()
                             * player.getDuration()/ seekBar.getMax();
                     currentTime.setText(change(value/1000));
                     player.seekTo(value);
                 }
             });
        playOrpause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(player.isPlaying()){
                    playOrpause.setBackgroundResource(R.drawable.play);
                    player.pause();
                }else {
                    playOrpause.setBackgroundResource(R.drawable.stop);
                    player.start();
                }
            }
        });
        player.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
                seekBar.setSecondaryProgress(i);
            }
        });
        surfaceView.setOnTouchListener(new View.OnTouchListener() {
            int i = 1;
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    i++;
                    if(i%2==0){
                        video_ll_title.setVisibility(View.VISIBLE);
                    }else{
                        video_ll_title.setVisibility(View.INVISIBLE);
                    }
                    return true;
                }
                return false;
            }
        });
    }
    public boolean fileIsExists(int position){
        try{
            File f=new File(Environment.getDataDirectory().getPath()+"/com.example.administrator.videoplayer/files/baisiFile"+infoList.get(position).substring(26) );
            Log.d("本地视频",infoList.get(position).substring(26));
            if(!f.exists()){
                return false;
            }

        }catch (Exception e) {
            // TODO: handle exception
            return false;
        }
        return true;
    }
    private void getIntentData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        infoList = getIntent().getStringArrayListExtra("VideoUrl");
        position = bundle.getInt("position");
        playPosition = bundle.getInt("playPosition");
        currentPosition = position;
    }

    private void PlayMovie(int position,int mode) {//mode为0是第一次加载
        try {
            player.reset();
            if(fileIsExists(position)){
                player.setDataSource("/data/com.example.administrator.videoplayer/files/baisiFile"+infoList.get(position).substring(26));
               //本地找到了该视频
            }else {
                player.setDataSource(this, Uri.parse(infoList.get(position)));

            }
        holder=surfaceView.getHolder();
        holder.addCallback(new MyCallBack());
        player.prepare();
            if(mode==0){
                player.seekTo(playPosition);
            }
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                new Thread(playingSeekBar).start();
                player.start();
                player.setLooping(true);
            }
        });
    } catch (IOException e) {
        e.printStackTrace();
    }
    }

    private void bindView() {
        xiayige = (ImageButton) findViewById(R.id.xiayige_full);
        shangyige = (ImageButton) findViewById(R.id.shangyige_full);
        fullTime = (TextView) findViewById(R.id.video_tv_ctime_full);
        currentTime = (TextView) findViewById(R.id.video_tv_otime_full);
        seekBar = (SeekBar) findViewById(R.id.video_seekbar_full);
        surfaceView = (SurfaceView) findViewById(R.id.full_video);
        xiazai = (ImageButton) findViewById(R.id.full_xiazai);
        playOrpause = (Button) findViewById(R.id.video_btn_play_full);
        video_ll_title = (LinearLayout) findViewById(R.id.video_buttom_full);
        video_ll_title.setVisibility(View.INVISIBLE);

        playingSeekBar = new upDateSeekBar();
        player=new MediaPlayer();

    }

    private class MyCallBack implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            player.setDisplay(holder);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    }
    class upDateSeekBar implements Runnable {

        @Override
        public void run() {
            mHandler.sendMessage(Message.obtain());
            if (flag) {
                mHandler.postDelayed(playingSeekBar, 1000);
            }
        }
    }
    @Override
    protected void onDestroy() { // 退出全屏后，释放资源
        super.onDestroy();
        if (player != null) {
            if (player.isPlaying()) {
                player.stop();
            }
            player.release();
            player = null;
        }
        System.gc();
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {

                if (player == null) {
                    flag = false;
                } else if (player.isPlaying()) {
                    flag = true;
                    int position = player.getCurrentPosition();
                    int mMax = player.getDuration();
                    int sMax = seekBar.getMax();
                    if (mMax > 0) {
                        currentTime.setText(change(position / 1000));
                        fullTime.setText(change(mMax / 1000));
                        seekBar.setProgress(position * sMax / mMax);
                    } else {
                        Toast.makeText(FullScreamShowVideo.this, "无法播放",
                                Toast.LENGTH_LONG).show();
                    }
                }

        };
    };
    public static String change(int second) {
        int h = 0;
        int d = 0;
        int s = 0;
        int temp = second % 3600;
        if (second > 3600) {
            h = second / 3600;
            if (temp != 0) {
                if (temp > 60) {
                    d = temp / 60;
                    if (temp % 60 != 0) {
                        s = temp % 60;
                    }
                } else {
                    s = temp;
                }
            }
        } else {
            d = second / 60;
            if (second % 60 != 0) {
                s = second % 60;
            }
        }

        return h + ":" + d + ":" + s;
    }
}
