package com.example.administrator.videoplayer.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

import com.example.administrator.videoplayer.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Administrator on 2017/4/7.
 */

public class MyImageLoader {

    private static final String TAG = "ImgLoader";

    private static final int MESSAGE_POST_RESULT = 1;
    //cpu核心数
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    //核心线程数量
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    //最大线程数量
    private static final int MAX_POOL_SIZE = CPU_COUNT * 2 + 1;
    //线程最大等待时长
    private static final long KEEP_ALIVE = 10L;
    //imageView 的key
    private static final int TAG_KEY_URL = R.id.publisher_icon;
    //硬盘最大为50M
    private static final int DISK_CACHE_SIZE = 1024*1024*50;
    //创建一个ThreadFactory
    private static final ThreadFactory mThreadFactory = new ThreadFactory(){
        //线程安全的加减操作
        private final AtomicInteger mCount = new AtomicInteger(1);
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r,"ImageLoader#"+mCount.getAndIncrement());
        }
    };
    //创建线程池
    public static final Executor Thread_Pool_Executor = new ThreadPoolExecutor(
            CORE_POOL_SIZE,MAX_POOL_SIZE,KEEP_ALIVE, TimeUnit.SECONDS,new LinkedBlockingDeque<Runnable>(),mThreadFactory
    );
    //创建一个在主线程的Handler
    private Handler mMainHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            LoadResult result = (LoadResult) msg.obj;
            ImageView imageView = result.imageView;
            String url = (String) imageView.getTag(TAG_KEY_URL);
            if(url.equals(result.url)){
                imageView.setImageBitmap(result.bitmap);
            }else{
                Log.d(TAG,"URL匹配失败");
            }
        }
    };

    private Context mContext;
    private PictureEditor editor;
    private LruCache<String,Bitmap> mLruCache;
    //硬盘缓存的地址
    private String mFilePath = null;

    private MyImageLoader(Context context){
        mContext = context.getApplicationContext();//获取application的上下文
        int maxMemory = (int) (Runtime.getRuntime().maxMemory()/1024);
        int cacheSize = maxMemory/8;
        mLruCache = new LruCache<String, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight()/1024;
            }
        };
        mFilePath = mContext.getExternalCacheDir().getPath();
        editor = new PictureEditor();
    }
    public static MyImageLoader build(Context context){
        return new MyImageLoader(context);
    }

    /**
     * 将bitmap存入LruCache
     * @param key
     * @param bitmap
     */
    private void addBitmapToLruCache(String key, Bitmap bitmap){
        if(getBitmapFromLruCache(key) == null && key != null && bitmap != null){
            mLruCache.put(key,bitmap);
        }
    }

    private Bitmap getBitmapFromLruCache(String key){
        if(mLruCache.get(key) != null){
            return mLruCache.get(key);
        }
        return null;
    }

    /**
     * 从LruCache取bitmap
     * @param url
     * @return
     */
    private Bitmap loadBitmapFromLrucache(String url){
        Log.d(TAG,"fromLruCache:");
        return getBitmapFromLruCache(getMD5(url));
    }

    /**
     * 从网络加载图片
     * @param url
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private Bitmap loadBitmapFromInternet(String url, int reqWidth, int reqHeight){
        Log.d(TAG,"fromHttp:");
        if(Looper.myLooper() == Looper.getMainLooper()){
            throw new RuntimeException("不能在主线程进行网络操作");
        }
        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;
        Bitmap bitmap = null;
        try{
            urlConnection = (HttpURLConnection) new URL(url).openConnection();
            urlConnection.setDoInput(true);
            urlConnection.connect();
            inputStream = urlConnection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
            saveBitmap(inputStream,mFilePath,url,reqWidth,reqHeight);//保存bitmap到本地
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try{
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            urlConnection.disconnect();
        }
        return bitmap;
    }

    /**
     * 从磁盘中获取图片
     * @param url
     * @return
     */
    private Bitmap loadBitmapFromDisk(String url){
        Bitmap bitmap = null;
        //文件名字
        String key = getMD5(url)+".jpg";
        //获取缓存文件夹
        File file = new File(mFilePath);
        //获取所有文件
        File[] files = file.listFiles();
        //遍历磁盘缓存中找到图片
        for(int i = 0;i<files.length;i++){
            if(files[i].getName().equals(key)){
                bitmap = BitmapFactory.decodeFile(files[i].getPath());
                Log.d(TAG,"fromDisk:");
                break;
            }
        }
        addBitmapToLruCache(getMD5(url),bitmap);
        return bitmap;
    }

    /**
     * 缓存没找到就调用此方法
     * @param url
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public Bitmap loadBitmap(String url, int reqWidth, int reqHeight){
        Bitmap bitmap = loadBitmapFromDisk(url);
        if(bitmap != null){
            return bitmap;
        }
        bitmap = loadBitmapFromInternet(url,reqWidth,reqHeight);
        addBitmapToLruCache(getMD5(url),bitmap);
        return bitmap;
    }

    /**
     * bind imageView(绑定imageView)
     * @param url
     * @param imageView
     * @param reqWidth
     * @param reqHeight
     */
    public void bindImageView(final String url, final ImageView imageView, final int reqWidth, final int reqHeight){
        imageView.setTag(TAG_KEY_URL,url);
        Bitmap bitmap = loadBitmapFromLrucache(url);

        if(bitmap != null){
            imageView.setImageBitmap(bitmap);
            return;
        }
        Runnable loadBitmapTask = new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = loadBitmap(url,reqWidth,reqHeight);
                if(bitmap != null){
                    LoadResult result = new LoadResult(imageView,url,bitmap);
                    mMainHandler.obtainMessage(MESSAGE_POST_RESULT,result).sendToTarget();
                }
            }
        };

        Thread_Pool_Executor.execute(loadBitmapTask);
    }

    /**
     * 保存到本地
     * @param in
     * @param path
     * @param url
     * @param reqWidth
     * @param reqHeight
     */
    public void saveBitmap(InputStream in, String path, String url, int reqWidth, int reqHeight){
        String filePath;
        String fileName = getMD5(url)+".jpg";
        if(path == null){
            filePath = mFilePath;
        }else {
            filePath = path;
        }
        File bitmapDir = new File(filePath);
        if(!bitmapDir.exists()){
            bitmapDir.mkdir();
        }
        try{
            if(getDiskSize(filePath)>DISK_CACHE_SIZE){
                clearDisk(path);
            }
            File file = new File(filePath,fileName);
            if(file.exists()){
                return;
            }
            FileOutputStream fos = new FileOutputStream(file);
            Bitmap bitmap = editor.decodeSampleBitmapFromInputStream(in,reqWidth,reqHeight);
            if(bitmap != null){
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
                Log.d(TAG,"已保存："+fileName);
            }
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取文件的总大小
     * @param path
     * @return size
     */
    public long getDiskSize(String path){
        long size  = 0;
        try{
            File file = new File(path);//获取缓存文件夹
            File[] files = file.listFiles();//获取所有文件
            for(int i = 0;i<files.length;i++){
                size = size+files[i].length();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 清除目录所有文件
     * @param path
     */
    public  void clearDisk(String path){
        File file = new File(path);
        File[] files = file.listFiles();
        for(File f:files){
            f.delete();
        }
    }

    /**
     * MD5加密
     * @param val 待加密的String
     * @return 加密后的String
     */
    public static String getMD5(String val){
        MessageDigest md5 = null;
        try{
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        md5.update(val.getBytes());
        byte[] m = md5.digest();//加密
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<m.length;i++){
            sb.append(m[i]);
        }
        return sb.toString();
    }

    private static class LoadResult{
        public ImageView imageView;
        public String url;
        public Bitmap bitmap;
        public LoadResult(ImageView imageView , String url, Bitmap bitmap){
            this.imageView = imageView;
            this.url = url;
            this.bitmap = bitmap;
        }
    }
}

