package com.example.administrator.videoplayer.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.videoplayer.R;
import com.example.administrator.videoplayer.activity.FullScreamShowVideo;
import com.example.administrator.videoplayer.beans.VideoBeans;
import com.example.administrator.videoplayer.utils.MyImageLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.administrator.videoplayer.R.id.decor_content_parent;
import static com.example.administrator.videoplayer.R.id.never;
import static com.example.administrator.videoplayer.R.id.surfaceView;
import static com.example.administrator.videoplayer.R.id.surface_image;

/**
 * Created by 我 on 2017/5/19.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>{

    public interface CallBack {
        void onClick(VideoBeans videoBeans);
    }

    private MediaPlayer mediaPlayer;
    private Handler mHandler;
    private static final  int TAG_KEY_URL = R.id.surfaceView;
    private Handler hander;
    private static ArrayList<String> info = new ArrayList<String>();
    private static int outPausePosition =0;
    private List<VideoBeans> list = new ArrayList<>();
    private SurfaceHolder mSurfaceHolder;
    private Context mContext = null;
    private static int play_position;
    private boolean flag = true;//在播放还是没播放
    private upDateSeekBar playingSeekBar; // 更新进度条用
    private static String path;
    private CallBack callBack = null;

    private Bitmap bitmap;

    public RecyclerAdapter(CallBack callBack) {
        this.callBack = callBack;
    }
    public void refresh(List<VideoBeans> list) {
        this.list = list;
        for(int i=0;i<list.size();i++){
            info.add(list.get(i).getVideoUrl());
        }
        notifyDataSetChanged();
    }

    @Override
    public RecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerAdapter.MyViewHolder holder, final int position) {
          VideoBeans videoBeans = null;
        if (list.size() > position) {
            videoBeans = list.get(position);

        }
        if (videoBeans !=null){
            play_position = position;
           holder.date.setText(videoBeans.getData());
            holder.name.setText(videoBeans.getName());
            MyImageLoader.build(mContext).bindImageView(videoBeans.getIconUrl(),holder.icon,holder.icon.getWidth(),holder.icon.getHeight());
            holder.title.setText(videoBeans.getTitle());
            holder.date.setText(videoBeans.getData());
            holder.dianzanshu.setText(videoBeans.getLove());
            holder.caishu.setText(videoBeans.getHate());

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(list.get(position).getVideoUrl(), new HashMap<String, String>());
            bitmap = retriever.getFrameAtTime(1000);
            Drawable drawable = new BitmapDrawable(bitmap);


           holder.surfance_image.setImageDrawable(drawable);
            playingSeekBar  = new upDateSeekBar();

            holder.play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callBack.onClick(list.get(position));

                     Toast.makeText(mContext,"点击放大后可以实现更多功能",Toast.LENGTH_SHORT).show();
                        holder.surfance_image.setVisibility(View.INVISIBLE);
                        mediaPlayer = new MediaPlayer();
                        holder.play.setVisibility(View.INVISIBLE);

                        mSurfaceHolder = holder.surfaceView.getHolder();
                        holder.surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
                            @Override
                            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                                holder.play.setVisibility(View.VISIBLE);
                                replay(outPausePosition,surfaceHolder);
                                holder.play.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        mediaPlayer.start();
                                        holder.play.setVisibility(View.INVISIBLE);
                                    }
                                });
                            }

                            @Override
                            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

                            }

                            @Override
                            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                                outPausePosition = mediaPlayer.getCurrentPosition();
                                mediaPlayer.stop();
                            }
                        });
                    //封装播放视频
                    path = list.get(position).getVideoUrl();
                    play(outPausePosition,holder.surfaceView,list.get(position).getVideoUrl(),holder.video_tv_otime,holder.video_tv_ctime,holder.video_seekbar_time);
                        setVisible(holder.video_ll_title,holder.full_scram);

                }

            });

             holder.full_scram.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {

                     Intent intent = new Intent();
                     intent.putExtra("playPosition",mediaPlayer.getCurrentPosition());
                     intent.putStringArrayListExtra("VideoUrl",info);
                      intent.putExtra("position",position);
                     intent.setClass(mContext, FullScreamShowVideo.class);
                     mediaPlayer.stop();
                     mContext.startActivity(intent);
                 }
             });
            holder.video_seekbar_time.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {


                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    int value = holder.video_seekbar_time.getProgress()
                            * mediaPlayer.getDuration()/ holder.video_seekbar_time.getMax();
                    holder.video_tv_otime.setText(change(value/1000));
                    mediaPlayer.seekTo(value);

                }
            });
            holder.video_playOrpause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mediaPlayer.isPlaying()) {
                        holder.play
                                .setBackgroundResource(R.drawable.play);
                        mediaPlayer.pause();
                    } else {
                        holder.play
                                .setBackgroundResource(R.drawable.zanting);
                        mediaPlayer.start();

                    }
                }
            });
            holder.surfaceView.setOnTouchListener(new View.OnTouchListener() {
                int i = 1;
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                        i++;
                        if(i%2==0){
                            holder.video_ll_title.setVisibility(View.VISIBLE);
                        }else{
                            holder.video_ll_title.setVisibility(View.INVISIBLE);
                        }
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    private void setVisible(LinearLayout linearLayout,ImageButton full) {
        linearLayout.setVisibility(View.VISIBLE);
        full.setVisibility(View.VISIBLE);
    }
     private void replay(int position,SurfaceHolder holder){
         try {
             mediaPlayer.reset();
             mediaPlayer.setDataSource(path);
             mediaPlayer.setDisplay(holder);
             mediaPlayer.prepare();//缓冲
             mediaPlayer.seekTo(position);
         } catch (Exception e) {
             e.printStackTrace();
         }
     }


    private void play(int position, SurfaceView surface, final  String path, final TextView otime,final TextView ctime, final SeekBar seekbar) {

        try {
            mSurfaceHolder = surface.getHolder();

            mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            //mSurfaceHolder.addCallback(new MyCallBack());
            mediaPlayer.setDataSource(path);
            mediaPlayer.setDisplay(surface.getHolder());
            mediaPlayer.prepare();//缓冲
            Log.d("服服服",outPausePosition+"");
            mediaPlayer.seekTo(outPausePosition);

            mediaPlayer.start();
            seekbar.setVisibility(View.VISIBLE);
            ctime.setText(change(mediaPlayer.getDuration() / 1000));
            onStopTrackingTouch(seekbar);
            new Thread(playingSeekBar).start();
            mHandler = new Handler() {

                public void handleMessage(Message msg) {

                    if (mediaPlayer.isPlaying()) {
                        flag = true;
                        int position = mediaPlayer.getCurrentPosition();
                        int mMax = mediaPlayer.getDuration();
                        int sMax = seekbar.getMax();
                        if (mMax > 0) {
                            otime.setText(change(position / 1000));
                            ctime.setText(change(mMax / 1000));
                            seekbar.setProgress(position * sMax / mMax);
                        }
                    }

                };
            };
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onStopTrackingTouch(SeekBar seekBar) {//停止拖拽时回调
        mediaPlayer.seekTo(seekBar.getProgress());//停止拖拽时进度条的进度
    }



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

        return h + ":" + d + ":" + s +"/";
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
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        Button video_playOrpause;
        TextView name,date,title;
        SurfaceView surfaceView;
        LinearLayout video_ll_title;
        ImageButton full_scram;
         TextView video_tv_otime;
         TextView video_tv_ctime;
        TextView dianzanshu,caishu;
        ImageButton play;
        ImageView surfance_image;
         SeekBar video_seekbar_time;
        public MyViewHolder(View itemView) {
            super(itemView);
            video_tv_ctime = (TextView) itemView.findViewById(R.id.video_tv_ctime);
            video_tv_otime = (TextView) itemView.findViewById(R.id.video_tv_otime);
            icon = (ImageView) itemView.findViewById(R.id.publisher_icon);
            name = (TextView) itemView.findViewById(R.id.publish_name);
            date = (TextView) itemView.findViewById(R.id.publish_time);
            dianzanshu = (TextView) itemView.findViewById(R.id.dianzanshu);
            caishu = (TextView) itemView.findViewById(R.id.caishu);
            video_playOrpause = (Button) itemView.findViewById(R.id.video_btn_play);
            title = (TextView) itemView.findViewById(R.id.publish_title);
            surfaceView = (SurfaceView) itemView.findViewById(R.id.surfaceView);
            play = (ImageButton) itemView.findViewById(R.id.button_play);
            video_ll_title = (LinearLayout) itemView.findViewById(R.id.video_ll_bottom);
            video_ll_title.setVisibility(View.INVISIBLE);
            full_scram = (ImageButton) itemView.findViewById(R.id.video_btn_full);
            full_scram.setVisibility(View.INVISIBLE);
            surfaceView.getHolder().setKeepScreenOn(true);
            surfance_image = (ImageView) itemView.findViewById(R.id.surface_image);

            video_seekbar_time = (SeekBar) itemView.findViewById(R.id.video_seekbar);
            video_seekbar_time.setVisibility(View.INVISIBLE);

        }


    }



}