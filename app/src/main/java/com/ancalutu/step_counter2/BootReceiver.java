package com.ancalutu.step_counter2;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

/**
 * This BroadcastReceiver automatically (re)starts the flush_alarm when the device is
 * rebooted. This receiver is set to be disabled (android:enabled="false") in the
 * application's manifest file. When the user sets the flush_alarm, the receiver is enabled.
 * When the user cancels the flush_alarm, the receiver is disabled, so that rebooting the
 * device will not trigger this receiver.
 */
// BEGIN_INCLUDE(autostart)
public class BootReceiver extends BroadcastReceiver {
    public static void enableBootReceiver(Context context) {

        // Enable {@code SampleBootReceiver} to automatically restart the flush_alarm when the
        // device is rebooted.
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);


    }

    public static void disableBootReceiver(Context context) {
        // Disable {@code SampleBootReceiver} so that it doesn't automatically restart the
        // flush_alarm when the device is rebooted.
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

    //  AlarmReceiver alarm = new AlarmReceiver();
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            //   alarm.setAlarm(context) ;
         /*   SharedPreferences sharedPref= PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(Constants.M_COUNTER_STEPS, 0);
            editor.commit();*/
            context.startService(new Intent(context, MainService.class).setAction(Constants.ACTION_BOOT_START));
            //  MyService2.startActionRegisterSensor(context, null, null);

        } else if (intent.getAction().equals("android.intent.action.ACTION_SHUTDOWN")) {
            // MyService2.startActionSaveSteps(context, null, null);
            context.startService(new Intent(context, MainService.class).setAction(Constants.ACTION_SHUTDOWN));

        }
    }
}
//END_INCLUDE(autostart)
