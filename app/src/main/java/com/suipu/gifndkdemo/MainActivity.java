package com.suipu.gifndkdemo;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

public class MainActivity extends AppCompatActivity {


    public ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.imageView);
        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "test.gif";//.("test.gif");
        GifLoader.with(this).load(path).into(imageView);
    }


}
