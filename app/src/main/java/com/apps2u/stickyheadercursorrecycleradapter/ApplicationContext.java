package com.apps2u.stickyheadercursorrecycleradapter;

import android.app.Application;
import android.content.Context;



/**
 * Created by Ouday Khaled on 2/9/2018.
 */

public class ApplicationContext extends Application {


    public static final boolean isuser1 = true;

    final static String TAG = "ApplicationContext";


    public static Application instance;

    public ApplicationContext() {
        instance = this;

    }

    public static Application getInstance() {
        return instance;
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }






}
