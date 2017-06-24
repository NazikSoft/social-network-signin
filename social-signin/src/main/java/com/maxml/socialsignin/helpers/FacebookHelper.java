package com.maxml.socialsignin.helpers;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.maxml.socialsignin.entity.SignInUser;
import com.maxml.socialsignin.util.SignInConstants;

/**
 * Extends {@see SocialNetworkHelper}.
 * Helper class for Facebook Sign In functionality.
 *
 * @see <a href="https://developers.facebook.com/quickstarts/?platform=android">Facebook Sign In Manual</a>
 */
public class FacebookHelper extends SocialNetworkHelper {

    /**
     * Facebook UI helper class used for managing the login UI.
     */
    private CallbackManager facebookCallback;
    private LoginButton loginButton;

    public FacebookHelper(Handler callback, AppCompatActivity activity) {
        super(callback, activity);

        setType(SignInConstants.AccountType.FACEBOOK);
    }

    @Override
    public void init() {
        loginButton = new LoginButton(getContext());
        loginButton.setReadPermissions("public_profile");
        // Other app specific specialization
        facebookCallback = CallbackManager.Factory.create();

        // Callback registration
        loginButton.registerCallback(facebookCallback, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String id = "" + loginResult.getAccessToken().getUserId();
                String name = Profile.getCurrentProfile().getName();
                String token = loginResult.getAccessToken().getToken();

                SignInUser signInUser = new SignInUser(SignInConstants.AccountType.FACEBOOK, id, name, token);

                getCallback().sendMessage(getCallback().obtainMessage(
                        SignInConstants.HANDLER_ATTACH_USER_ACTION, signInUser));
            }

            @Override
            public void onCancel() {
                getCallback().sendMessage(getCallback().obtainMessage(
                        SignInConstants.HANDLER_ERROR_ACTION, "Cancel in Facebook connection!"));
            }

            @Override
            public void onError(FacebookException exception) {
                getCallback().sendMessage(getCallback().obtainMessage(
                        SignInConstants.HANDLER_ERROR_ACTION,
                        "Error in Facebook connection: " + exception.getMessage()));
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        facebookCallback.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void performClick() {
        loginButton.performClick();
    }

    @Override
    public void logout() {
        LoginManager.getInstance().logOut();
        
        getCallback().sendEmptyMessage(SignInConstants.HANDLER_LOGOUT_ACTION);
    }

}

