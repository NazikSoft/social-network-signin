package com.maxml.socialsignin.util;

/**
 * Constants, that use in this project
 */
public class SignInConstants {

    public enum AccountType {
        TWITTER, FACEBOOK, GOOGLE
    }

    public static final String TAG = SignInConstants.class.getSimpleName();

    public static final int RC_SIGN_IN = 1010;

    public static final int HANDLER_SEND_MESSAGE_ACTION = 0;
    public static final int HANDLER_ERROR_ACTION = 1;
    public static final int HANDLER_UPDATE_VIEW_ACTION = 2;
    public static final int HANDLER_ATTACH_USER_ACTION = 3;
    public static final int HANDLER_LOGOUT_ACTION = 4;

}
