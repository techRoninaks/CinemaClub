package com.create.sidhu.movbox;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by nihalpradeep on 27/08/18.
 */

public final class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/myriadpro.otf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }
}
