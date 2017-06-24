package com.maxml.socialsignin;

import android.app.Application;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.maxml.socialsignin.entity.SignInUser;
import com.maxml.socialsignin.helpers.FacebookHelper;
import com.maxml.socialsignin.helpers.GplusHelper;
import com.maxml.socialsignin.helpers.SocialNetworkHelper;
import com.maxml.socialsignin.helpers.TwitterHelper;
import com.maxml.socialsignin.util.SignInConstants;

import java.util.ArrayList;

/**
 * Manager class for all work with all social networks
 */
public class SignInManager {

    private ArrayList<SocialNetworkHelper> helpers;

    private static SignInManager manager;

    public static SignInManager getManager() {
        if (manager == null) {
            manager = new SignInManager();
        }
        return manager;
    }

    private SignInManager() {
        helpers = new ArrayList<>();
    }

    public void onCreateApplication(Application application) {
        /*
         * Initialize the Facebook SDK before executing any other operations,
         * especially, if you're using Facebook UI elements.
         */
        FacebookSdk.sdkInitialize(application);
        AppEventsLogger.activateApp(application);

    }

    public void createHelpers(Handler handler, AppCompatActivity activity) {
        GplusHelper gplusHelper = new GplusHelper(handler, activity);
        FacebookHelper facebookHelper = new FacebookHelper(handler, activity);
        TwitterHelper twitterHelper = new TwitterHelper(handler, activity);

        helpers.add(gplusHelper);
        helpers.add(facebookHelper);
        helpers.add(twitterHelper);
    }

    /**
     * Register the application using Fabric plug-in for managing Twitter apps
     * Creating the Google API client capable of making requests for tokens, signInUser info etc.
     * Creates listener for Facebook.
     */
    public void configureSocialHelpers() {
        for (SocialNetworkHelper h : helpers) {
            h.init();
        }
    }


    public void logout(SignInUser signInUser) {
        if (signInUser == null || signInUser.getType() == null) {
            Log.e(SignInConstants.TAG, "logout: User or social type is null");
            return;
        }

        getNeededSocialHelper(signInUser.getType()).logout();
    }

    public SocialNetworkHelper getNeededSocialHelper(SignInConstants.AccountType type) {
        for (SocialNetworkHelper h : helpers) {
            if (type.equals(h.getType())) {
                return h;
            }
        }
        return null;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        for (SocialNetworkHelper h :
                helpers) {
            h.onActivityResult(requestCode, resultCode, data);
        }
    }
}
