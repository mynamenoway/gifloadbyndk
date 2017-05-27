package com.suipu.gifndkdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.InputStream;

/**
 * Created by hasee on 2017/5/1.
 */
public class NdkGifDrawer implements GifDrawer{
    private static final String TAG="NdkGifDrawer";
    public String mFilePath;
    Bitmap bitmap;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int nextFrame = GifLoader.updateFrame(mGifHandler, bitmap);
            if (nextFrame < 0) {
                return;
            }
            mHandler.sendEmptyMessageDelayed(1,nextFrame);
        }
    };
    private ImageView mImageView;
    private Context mContext;
    private long mGifHandler;
    public NdkGifDrawer(Context context, ImageView imageView) {
        mImageView = imageView;
        mContext = context;
    }

    @Override
    public void into(InputStream inputStream) {

    }

    @Override
    public void into(String path) {
        if (!new File(path).exists()) {
            Log.d(TAG, "filepath not exist =" +path);
            return;
        }
        mGifHandler = GifLoader.loadGIFc(path);
        int height = GifLoader.getHeight(mGifHandler);
        int width = GifLoader.getWidth(mGifHandler);
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        int nextFrame = GifLoader.updateFrame(mGifHandler, bitmap);
        if (nextFrame < 0) {
            return;
        }
        mHandler.sendEmptyMessageDelayed(1,nextFrame);
    }

    @Override
    public void onStop() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestory() {

    }
}
