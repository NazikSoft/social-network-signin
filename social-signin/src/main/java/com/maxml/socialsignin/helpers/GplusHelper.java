package com.maxml.socialsignin.helpers;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.maxml.socialsignin.entity.SignInUser;
import com.maxml.socialsignin.util.SignInConstants;

/**
 * Extends {@see SocialNetworkHelper}.
 * Helper class for Google Sign In functionality.
 *
 * @see <a href="https://developers.google.com/identity/sign-in/android/start-integrating">
 *     Google Sign In Manual</a>
 */
public class GplusHelper extends SocialNetworkHelper implements GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInOptions gso;
    private SignInButton signInButton;

    public GplusHelper(Handler callback, AppCompatActivity activity) {
        super(callback, activity);

        setType(SignInConstants.AccountType.GOOGLE);
    }

    /**
     * Configure sign-in to request the user's ID, email address, and basic
     * profile. ID and basic profile are included in DEFAULT_SIGN_IN.
     * Build a GoogleApiClient with access to the Google Sign-In API and the
     * options specified by gso.
     */
    @Override
    public void init() {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity() /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        signInButton = new SignInButton(getContext());
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                getActivity().startActivityForResult(signInIntent, SignInConstants.RC_SIGN_IN);
            }
        });
    }

    /**
     * Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SignInConstants.RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignInResult(result);
        }
    }

    @Override
    public void performClick() {
        // Doesn't work because of
        // https://code.google.com/p/android/issues/detail?id=220703
        signInButton.performClick();
        signInButton.hasOnClickListeners();

        // so use this
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        getActivity().startActivityForResult(signInIntent, SignInConstants.RC_SIGN_IN);
    }

    private void handleGoogleSignInResult(GoogleSignInResult result) {
        Log.d(SignInConstants.TAG, "handleGoogleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            String id = acct.getId();
            String name = acct.getDisplayName();
            String token = acct.getServerAuthCode();
            SignInUser signInUser = new SignInUser(SignInConstants.AccountType.GOOGLE, id, name, token);

            getCallback().sendMessage(getCallback().obtainMessage(
                    SignInConstants.HANDLER_ATTACH_USER_ACTION, signInUser));
        } else {
            getCallback().sendMessage(getCallback().obtainMessage(
                    SignInConstants.HANDLER_ERROR_ACTION, "Error in Google authentication!"));
        }
    }

    @Override
    public void logout() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                Toast.makeText(getContext(), "Success logout!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        getCallback().sendMessage(getCallback().obtainMessage(
                SignInConstants.HANDLER_ERROR_ACTION, "Error in Google connection!"));
    }
}
