package com.suipu.gifndkdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

/**
 * Created by hasee on 2017/4/29.
 */
public class MovieGifDrawer implements GifDrawer{
    private final static String TAG = "MovieGifDrawer";
    public InputStream mInputstream;
    private Handler mHandler = new Handler();
    private ImageView mImageView;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private boolean isLoop = true;
    private Movie movie;
    private boolean isVisible = true;
    private Context context;

    public MovieGifDrawer(Context context, ImageView imageView) {
        mImageView = imageView;
        this.context = context;

    }

    @Override
    public void into(InputStream inputStream) {
        if (inputStream == null) {
            throw new RuntimeException("inputStream can not be null");
        }
        if (mImageView == null) {
            return;
        }
        if (mInputstream != null) {
            mHandler.removeCallbacks(drawRunnable);
        }
        mInputstream = inputStream;

        movie = Movie.decodeStream(mInputstream);
        if (movie == null) {
            return;
        }
        if (movie.width() <= 0 || movie.height() <= 0) {
            return;
        }

        mBitmap = Bitmap.createBitmap(movie.width(), movie.height(), Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mHandler.post(drawRunnable);

    }

    @Override
    public void into(String path) {

    }

    @Override
    public void onStop() {
        isLoop = false;
        Log.d(TAG, "onstop");
    }

    @Override
    public void onResume() {
        if (!isLoop) {
            isLoop = true;
            Log.d(TAG, "onResume");
            mHandler.post(drawRunnable);
        }

    }

    @Override
    public void onDestory() {
        isLoop = false;
        Log.d(TAG, "onDestory");
        mHandler.removeCallbacks(drawRunnable);
    }

    public Runnable drawRunnable = new Runnable() {
        @Override
        public void run() {
            draw();
            if (isLoop){
                mHandler.postDelayed(drawRunnable, 16);
            }

        }
    };
    void draw(){
        mCanvas.save();
        movie.draw(mCanvas, 0, 0);
        mCanvas.restore();
        movie.setTime((int) (System.currentTimeMillis() % movie.duration()));
        mImageView.setImageBitmap(mBitmap);

    }


}
