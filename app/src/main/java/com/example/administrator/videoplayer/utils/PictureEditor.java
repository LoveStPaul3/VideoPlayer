package com.example.administrator.videoplayer.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.FileDescriptor;
import java.io.InputStream;

/**
 * Created by Administrator on 2017/4/7.
 */

public class PictureEditor {

    private static final String TAG = "PictureEditor";

    /**
     * 计算采样率
     * @param options
     * @param reqWidth  imageView的宽度
     * @param reqHeight  imageView的高度
     * @return
     */
    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight){
        if(reqHeight == 0||reqWidth == 0){
            return 1;
        }
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if(height>reqHeight||width>reqWidth){
            final int halfHeight = height/2;
            final int halfWidth = width/2;
            while ((halfHeight/inSampleSize)>=reqHeight && (halfWidth/inSampleSize)>=width){
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    /**
     * 从资源文件中压缩bitmao并且加载
     * @param res
     * @param resId
     * @param reqWidth
     * @param reqHeight
     * @return bitmap
     */
    public Bitmap decodeSampleBitmapFromResouse(Resources res, int resId, int reqWidth, int reqHeight){
        //首先测量图片的基本信息，以便计算采样率
        BitmapFactory.Options options = new BitmapFactory.Options();
        //只测量，不加载
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res,resId,options);
        //计算采样率
        options.inSampleSize = calculateInSampleSize(options,reqWidth,reqHeight);
        //加载图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res,resId,options);
    }

    /**
     * 从FileDescriptor加载并压缩
     * @param fd
     * @param reqWidth
     * @param reqHeight
     * @return bitmap
     */
    public Bitmap decodeSampleBitmapFromFileDescriptor(FileDescriptor fd, int reqWidth, int reqHeight){
        //同样先测量图片
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fd,null,options);
        //计算采样率
        options.inSampleSize = calculateInSampleSize(options,reqWidth,reqHeight);
        //加载图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFileDescriptor(fd,null,options);
    }

    /**
     * 从inputStream加载并压缩
     * @param is
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public Bitmap decodeSampleBitmapFromInputStream(InputStream is, int reqWidth, int reqHeight){
        //测量图片
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is,null,options);
        //计算采样率
        options.inSampleSize = calculateInSampleSize(options,reqWidth,reqHeight);
        //加载图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeStream(is,null,options);
    }

}
