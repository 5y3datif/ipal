package com.tahashaheen.chotu;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
/**
 * @file MyAdminReceiver.java
 * @brief This allows the app to turn off the device
 * @details
 * @li DeviceAdminReceiver is the base class for implementing a device administration component
 * @li This class provides a convenience for interpreting the raw intent actions that are sent by the system.
 * @li Definitely read the <a href="https://developer.android.com/reference/android/app/admin/DeviceAdminReceiver">Android Developer's page</a> on this
 * @li The manifest file was also updated when adding this class
 */


/**
 * @brief This allows the app to turn off the device
 * @details
 * @li DeviceAdminReceiver is the base class for implementing a device administration component
 * @li This class provides a convenience for interpreting the raw intent actions that are sent by the system.
 * @li Definitely read the <a href="https://developer.android.com/reference/android/app/admin/DeviceAdminReceiver">Android Developer's page</a> on this
 * @li The manifest file was also updated when adding this class
 */
public class MyAdminReceiver extends DeviceAdminReceiver {

    String TAG = "MY_APP_DEBUG_TAG";
    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);
        Log.d(TAG, "Admin receiver status enabled");
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        super.onDisabled(context, intent);
    }
}

