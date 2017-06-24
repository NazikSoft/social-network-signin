package com.maxml.socialsignin.helpers;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.maxml.socialsignin.entity.SignInUser;
import com.maxml.socialsignin.util.SignInConstants;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.core.services.AccountService;

import retrofit2.Call;

/**
 * Extends {@see SocialNetworkHelper}.
 * Helper class for Twitter Sign In functionality.
 *
 * @see <a href="https://dev.twitter.com/twitterkit/android/overview">Twitter Sign In Manual</a>
 */
public class TwitterHelper extends SocialNetworkHelper {

    private TwitterAuthClient mAuthClient;

    public TwitterHelper(Handler callback, AppCompatActivity activity) {
        super(callback, activity);

        setType(SignInConstants.AccountType.TWITTER);
    }

    @Override
    public void init(String... configs) {
        TwitterConfig authConfig = new TwitterConfig.Builder(getContext())
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(configs[0], configs[1]))
                .debug(true)
                .build();
        Twitter.initialize(authConfig);

        mAuthClient = new TwitterAuthClient();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mAuthClient.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void performClick() {
        mAuthClient.authorize(getActivity(), new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
                AccountService statusesService = twitterApiClient.getAccountService();
                Call<User> call = statusesService.verifyCredentials(true, true, true);
                call.enqueue(new Callback<User>() {
                    @Override
                    public void success(Result<User> userResult) {
                        String id = "" + userResult.data.getId();
                        String name = userResult.data.name;
                        String token = "You can push here your consumer secret code";

                        SignInUser signInUser = new SignInUser(SignInConstants.AccountType.TWITTER, id, name, token);

                        getCallback().sendMessage(getCallback().obtainMessage(
                                SignInConstants.HANDLER_ATTACH_USER_ACTION, signInUser));
                    }

                    public void failure(TwitterException exception) {
                        //Do something on failure
                    }
                });
            }

            @Override
            public void failure(TwitterException exception) {
                getCallback().sendMessage(getCallback().obtainMessage(
                        SignInConstants.HANDLER_ERROR_ACTION));
            }
        });
    }

    // https://stackoverflow.com/questions/4163095/using-twitter-api-logout-on-twitter
    public void logout() {
    }
}
