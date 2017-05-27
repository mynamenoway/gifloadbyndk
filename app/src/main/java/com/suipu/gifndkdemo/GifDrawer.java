package com.suipu.gifndkdemo;

import java.io.InputStream;

/**
 * Created by hasee on 2017/4/29.
 */
public interface GifDrawer {
    void into(InputStream inputStream);
    void into(String path);
    void onStop();
    void onResume();
    void onDestory();
}
