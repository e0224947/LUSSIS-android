package com.sa45team7.lussis;

import android.app.Application;
import android.content.Context;

/**
 * Created by nhatton on 1/19/18.
 */

public class LUSSISApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getAppContext(){
        return context;
    }
}
