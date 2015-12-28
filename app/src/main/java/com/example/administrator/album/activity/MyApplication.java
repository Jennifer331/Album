package com.example.administrator.album.activity;

import android.app.Application;
import android.os.StrictMode;

import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by Lei Xiaoyue on 2015-12-02.
 */
public class MyApplication extends Application {
    private static final boolean DEVELOPER_MODE = true;
    @Override
    public void onCreate() {
        if (DEVELOPER_MODE) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()   // or .detectAll() for all detectable problems
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }
        super.onCreate();
        CrashReport.initCrashReport(getApplicationContext(), "900013391", false);
    }
}
