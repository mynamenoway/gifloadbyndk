package com.suipu.gifndkdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by hasee on 2017/4/29.
 */
public class GifLoader {
    private final static String TAG = "GifLoader";
    private static volatile GifLoader mGifLoader;
    private static volatile Context mContext;
    private static final int MAXSIZE = 4 * 1024 * 1024;
    private static final String FRAGMENT_TAG="com.suipo.gifloader.suipugifloader";
    private long mGifHandler;
    public Map<ImageView, GifDrawer> mGifDrawers;

    static {
        System.loadLibrary("native-lib");
    }

    public static native long loadGIFc(String path);
    public static native int getWidth(long GifHandler);
    public static native int updateFrame(long GifHandler, Bitmap bitmap);
    public static native int getHeight(long GifHandler);

    private GifLoader() {
        mGifDrawers = new LinkedHashMap<ImageView, GifDrawer>(MAXSIZE, 0.75f, true){
            @Override
            protected boolean removeEldestEntry(Entry<ImageView, GifDrawer> eldest) {
                return size() > 10;
            }
        };
    }
    public static GifLoader with(Context context) {
        mContext = context;
        if (mGifLoader == null) {
            synchronized (GifLoader.class) {
                if (mGifLoader == null) {
                    return mGifLoader = new GifLoader();
                }
            }
        }
        return mGifLoader;
    }

    public GifDrawer load(ImageView imageView) {
        Log.d(TAG, "" + imageView);
        GifDrawer gifDrawer = mGifDrawers.get(imageView);
        if (gifDrawer == null) {
            gifDrawer = new MovieGifDrawer(mContext, imageView);
            mGifDrawers.put(imageView, gifDrawer);
        }
        return gifDrawer;
    }

    public GifDrawer loadc(ImageView imageView) {
        Log.d(TAG, "" + imageView);
        GifDrawer gifDrawer = mGifDrawers.get(imageView);
        if (gifDrawer == null) {
            gifDrawer = new NdkGifDrawer(mContext, imageView);
            mGifDrawers.put(imageView, gifDrawer);
        }
        return gifDrawer;
    }
    public GifLoader load(String path) {
        Log.d(TAG, "" + path);
        mGifHandler = loadGIFc(path);
        return this;
    }
    public void into(ImageView view){
        mImageView = view;
        int height = GifLoader.getHeight(mGifHandler);
        int width = GifLoader.getWidth(mGifHandler);
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        int nextFrame = GifLoader.updateFrame(mGifHandler, bitmap);
        if (nextFrame < 0) {
            return;
        }
        mImageView.setImageBitmap(bitmap);
        mHandler.sendEmptyMessageDelayed(1,nextFrame);
    }

    Bitmap bitmap;
    ImageView mImageView;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int nextFrame = GifLoader.updateFrame(mGifHandler, bitmap);
            if (nextFrame < 0) {
                return;
            }
            mImageView.setImageBitmap(bitmap);
            mHandler.sendEmptyMessageDelayed(1,nextFrame);
        }
    };
}
