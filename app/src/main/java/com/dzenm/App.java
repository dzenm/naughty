package com.dzenm;

import android.app.Application;

import com.dzenm.crash.CrashHelper;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CrashHelper.getInstance().init(this);
    }
}
