package com.maxml.socialsignin.demo;

import android.app.Application;

import com.maxml.socialsignin.SignInManager;

/**
 * Created by maxml on 22.10.16.
 */
public class DemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        SignInManager.getManager().onCreateApplication(this);
    }
}
