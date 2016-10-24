package com.maxml.socialsignin.helpers;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.maxml.socialsignin.entity.SignInUser;
import com.maxml.socialsignin.util.SignInConstants;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

/**
 * Extends {@see SocialNetworkHelper}.
 * Helper class for Twitter Sign In functionality.
 *
 * @see <a href="https://fabric.io/downloads/android">Twitter Sign In Manual</a>
 */
public class TwitterHelper extends SocialNetworkHelper {

    private TwitterLoginButton twitterButton;

    public TwitterHelper(Handler callback, AppCompatActivity activity) {
        super(callback, activity);

        setType(SignInConstants.AccountType.TWITTER);
    }

    @Override
    public void init() {
        twitterButton = new TwitterLoginButton(getContext());

        twitterButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // The TwitterSession is also available through:
                // Twitter.getInstance().core.getSessionManager().getActiveSession()
                TwitterSession session = result.data;

                String id = "" + session.getId();
                String name = session.getUserName();
                String token = "You can push here your consumer secret code";

                SignInUser signInUser = new SignInUser(SignInConstants.AccountType.TWITTER, id, name, token);

                getCallback().sendMessage(getCallback().obtainMessage(
                        SignInConstants.HANDLER_ATTACH_USER_ACTION, signInUser));
            }

            @Override
            public void failure(TwitterException exception) {
                Log.d("TwitterKit", "Login with Twitter failure", exception);

                getCallback().sendMessage(getCallback().obtainMessage(
                        SignInConstants.HANDLER_ERROR_ACTION, "Error in Twitter: " + exception.getMessage()));
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        twitterButton.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void performClick() {
        twitterButton.performClick();
    }

    public void logout() {
        TwitterSession twitterSession = TwitterCore.getInstance().getSessionManager().getActiveSession();
        if (twitterSession != null) {
            clearCookies(getContext());

            Twitter.getSessionManager().clearActiveSession();
            Twitter.logOut();

            getCallback().sendEmptyMessage(SignInConstants.HANDLER_LOGOUT_ACTION);
        }
    }

    private void clearCookies(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }
}
