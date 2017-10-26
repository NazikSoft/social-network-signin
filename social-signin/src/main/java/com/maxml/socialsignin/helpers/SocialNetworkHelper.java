package com.maxml.socialsignin.helpers;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.maxml.socialsignin.util.SignInConstants;

/**
 * Abstract class for all social network verifier' helpers.
 */
public abstract class SocialNetworkHelper {

    private Handler callback;
    private AppCompatActivity activity;
    private SignInConstants.AccountType type;

    public SocialNetworkHelper(Handler callback, AppCompatActivity activity) {
        this.callback = callback;
        this.activity = activity;
    }

    /**
     * Initial functionality, what must be loaded before user sign in
     */
    public void init() {
    }

    /**
     * Initial functionality, what must be loaded before user sign in
     */
    public void init(String... configs) {
    }

    /**
     * Method, that execute some code from result intent from social network server
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public abstract void onActivityResult(int requestCode, int resultCode, Intent data);

    /**
     * All social networks hae own sign in button.
     * Method sends click event on social network button
     */
    public abstract void performClick();

    /**
     * Logout functionality
     */
    public abstract void logout();

    public Handler getCallback() {
        return callback;
    }

    public void setCallback(Handler callback) {
        this.callback = callback;
    }

    public AppCompatActivity getActivity() {
        return activity;
    }

    public Context getContext() {
        return activity;
    }

    public void setActivity(AppCompatActivity activity) {
        this.activity = activity;
    }

    public SignInConstants.AccountType getType() {
        return type;
    }

    public void setType(SignInConstants.AccountType type) {
        this.type = type;
    }
}
