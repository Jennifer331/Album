package com.example.administrator.album.activity;

import android.app.Application;

import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by Lei Xiaoyue on 2015-12-02.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CrashReport.initCrashReport(getApplicationContext(), "900013391", false);
    }
}
