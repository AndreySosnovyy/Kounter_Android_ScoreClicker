package com.example.exhaustion;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

public class Helper {

    public static final String APP_PREFERENCES = "appSettings";
    public static final String SETTINGS_HAS_VISITED = "hasVisited";
    public static final String SETTINGS_SOUND = "settingsSound";
    public static final String SETTINGS_VIBRATION = "settingsVibration";
    public static final String SETTINGS_PAID = "settingsPaid";

    public static boolean isAppRunning(final Context context, final String packageName) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        if (procInfos != null)
        {
            for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
                if (processInfo.processName.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }
}