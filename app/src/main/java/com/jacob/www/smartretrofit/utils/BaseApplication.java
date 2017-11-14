package com.jacob.www.smartretrofit.utils;

import android.app.Application;

public class BaseApplication extends Application {
    private static Application mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static Application getInstance(){
        return mInstance;
    }
}
