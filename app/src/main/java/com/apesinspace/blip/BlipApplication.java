package com.apesinspace.blip;

import android.app.Application;

/**
 * Created by samnwosu on 8/15/15.
 */
public class BlipApplication extends Application {
    protected static boolean isLoggedIn;
    protected static User currentUser;
    @Override
    public void onCreate() {
        super.onCreate();
        isLoggedIn = false;
        currentUser = new User();
    }

    public static boolean getLoggedIn(){
        return isLoggedIn;
    }

    public static void setIsLoggedIn(boolean bool){
        isLoggedIn = bool;
    }

    public static User getCurrentUser(){
        return currentUser;
    }

    public static void setCurrentUser(User user){
        currentUser = user;
    }
}
