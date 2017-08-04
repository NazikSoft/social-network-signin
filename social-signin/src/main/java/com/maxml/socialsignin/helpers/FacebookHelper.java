package com.maxml.socialsignin.helpers;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.maxml.socialsignin.entity.SignInUser;
import com.maxml.socialsignin.util.SignInConstants;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Extends {@link SocialNetworkHelper}.
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

    /**
     * https://github.com/maxml/social-network-signin/issues/4
     * Provides key hash SHA1 for facebook console
     * Also, your hash could be found in LogCat with tag "facebook_tag"
     *
     * @param activity your activity with your credentials
     * @return SHA1 key that you can use for API console, null otherwise
     */
    public static String getKeyHash(Activity activity) {
        try {
            PackageInfo info = activity.getPackageManager().getPackageInfo(
                    activity.getApplicationContext().getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());

                String res = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.d("facebook_tag", "KeyHash:" + res);

                // avoiding \n in the end of the line
                return res.replace(System.getProperty("line.separator"), "");
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
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

