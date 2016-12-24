package com.example.omnamahshivay.test2;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

/**
 * Created by OM NAMAH SHIVAY on 12/16/2016.
 */

public class processbackground extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(processbackground.this,"service started",Toast.LENGTH_LONG).show();
        return START_STICKY;
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Toast.makeText(processbackground.this,"service destroyed",Toast.LENGTH_LONG).show();


    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
