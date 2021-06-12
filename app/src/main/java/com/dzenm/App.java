package com.dzenm;

import android.app.Application;

import com.dzenm.crash.CrashHelper;

import org.litepal.LitePal;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CrashHelper.getInstance().init(this);
        LitePal.initialize(this);
    }
}
