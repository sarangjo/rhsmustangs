package com.sarangjoshi.rhsmustangs;

import android.app.Application;

import com.parse.Parse;

/**
 * Takes care of the Parse initialization.
 *
 * @author Sarang
 */
public class MainApplication extends Application {
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "VTncYgZyAW6wV67VNiXAIAYzotG4EZaz0kfzPCbt", "40wxJTEvnfUdIBS0Zo5nJEqQeLKRVIAN9FItyRhz");
    }
}
