package com.maxml.socialsignin.demo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.maxml.socialsignin.SignInManager;
import com.maxml.socialsignin.entity.SignInUser;
import com.maxml.socialsignin.util.SignInConstants;

public class MainActivity extends AppCompatActivity {

    private SignInManager manager = SignInManager.getManager();
    private SignInUser signInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // need to be before setContentView()
        manager.createHelpers(new ActivityCalback(), this);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        manager.configureSocialHelpers();
        manager.getNeededSocialHelper(SignInConstants.AccountType.TWITTER).init(
                getString(R.string.twitter_api_key),
                getString(R.string.twitter_secret_key)
        );

        findViewById(R.id.twitter_design_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manager.getNeededSocialHelper(SignInConstants.AccountType.TWITTER).performClick();
            }
        });

        findViewById(R.id.gplus_design_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manager.getNeededSocialHelper(SignInConstants.AccountType.GOOGLE).performClick();
            }
        });

        findViewById(R.id.facebook_design_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manager.getNeededSocialHelper(SignInConstants.AccountType.FACEBOOK).performClick();
            }
        });


        findViewById(R.id.logout_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manager.logout(signInUser);

                signInUser = null;
                updateViews();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateViews();
    }


    private void updateViews() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // fill yourself
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        manager.onActivityResult(requestCode, resultCode, data);
    }

    private void attachUser(SignInUser signInUser) {
        this.signInUser = signInUser;

        // fill yourself
        Toast.makeText(MainActivity.this, signInUser.toString(), Toast.LENGTH_SHORT).show();
    }

    private class ActivityCalback extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case SignInConstants.HANDLER_SEND_MESSAGE_ACTION:
                    Toast.makeText(MainActivity.this, "Message: " + msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                case SignInConstants.HANDLER_LOGOUT_ACTION:
                    Toast.makeText(MainActivity.this, getString(R.string.success_logout), Toast.LENGTH_SHORT).show();
                    break;
                case SignInConstants.HANDLER_ERROR_ACTION:
                    Toast.makeText(MainActivity.this, "" + msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                case SignInConstants.HANDLER_UPDATE_VIEW_ACTION:
                    updateViews();
                    break;
                case SignInConstants.HANDLER_ATTACH_USER_ACTION:
                    attachUser((SignInUser) msg.obj);
                    break;
            }
        }
    }
}
