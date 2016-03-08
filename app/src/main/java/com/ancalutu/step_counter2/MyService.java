package com.ancalutu.step_counter2;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import java.util.Random;

public class MyService extends android.app.Service {
    private static final int BATCH_LATENCY = 300 * 1000000; //microsecunde
    private static final int STATE_OTHER = 0;
    private static final int STATE_COUNTER = 2;
    private int mState = STATE_OTHER;
    private int mSteps;
    private int mCounterSteps =0;
    private int mPreviousCounterSteps;
    private Notification mNotification;
    private NotificationCompat.Builder mBuilder;
    private NotificationManager mNotificationManager;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private Intent broadcastIntent = new Intent(Constants.BROADCAST_ACTION);
    private float step_length_km;
    private SensorManager sensorManager;
    private boolean isMilles;
    private float consum_specific;
    private int weight;
    private boolean activity_online=false;
    private AlarmReceiver alarm = new AlarmReceiver();
    private Sensor sensor;


    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager = (SensorManager) getSystemService(android.app.Activity.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPref.edit();
        mPreviousCounterSteps = sharedPref.getInt(Constants.M_PREVIOUS_COUNTER_STEPS, 0);
        mSteps=mPreviousCounterSteps;
        calcCoefficients();
        //setUpAsForeground();
        sensorManager.registerListener(mListener, sensor, SensorManager.SENSOR_DELAY_NORMAL, BATCH_LATENCY);
        alarm.setAlarm(this, sharedPref.getLong(Constants.CALENDAR_HISTORY, System.currentTimeMillis() - AlarmManager.INTERVAL_DAY));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            String action = intent.getAction();
            if (action.equals(Constants.ACTION_BOOT_START)) {

            } else if (action.equals((Constants.ACTION_ACTIVITY_ONLINE))) {
                activity_online=true;
                updateActivity();
            }  else if (action.equals((Constants.ACTION_ACTIVITY_OFFLINE))) {
                activity_online=false;
            } else if (action.equals((Constants.ACTION_NEW_DAY_ALARM))) {
                new_day_actions();
            } else if (action.equals(Constants.ACTION_SETTINGS_CHANGE)) {
                calcCoefficients();
                updateActivity();
                updateNotif();
            }else if (action.equals(Constants.ACTION_SHUTDOWN)) {
               // new DoInBackground().start(this, Constants.M_PREVIOUS_COUNTER_STEPS, mSteps);
              //  editor.putInt(Constants.M_PREVIOUS_COUNTER_STEPS, mSteps);
               // editor.commit();
            }
        }
        return START_STICKY;
    }


    private void new_day_actions() {
        editor.putLong(Constants.CALENDAR_HISTORY, System.currentTimeMillis() + 2000);
        editor.putInt(Constants.STEPS_HISTORY, mSteps);
        editor.putBoolean(Constants.CAN_CHANGE_IMAGE, true);
        editor.putInt(Constants.M_PREVIOUS_COUNTER_STEPS, 0);
        editor.commit();
        mSteps = 0;
        mCounterSteps = 0;
        mPreviousCounterSteps = 0;
        if(activity_online)
            updateActivity();
        updateNotif();
        new  DownloadFile().start(this, Images.imageUrls[new Random().nextInt(Images.imageUrls.length)], Constants.MY_PHOTO);
    }


    private final SensorEventListener mListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
                if (mCounterSteps < 1) {
                    mCounterSteps = (int) event.values[0];
                }

                mSteps = (int) event.values[0] - mCounterSteps + mPreviousCounterSteps;
               if (mSteps%200==0){
                   editor.putInt(Constants.M_PREVIOUS_COUNTER_STEPS, mSteps);
                   editor.commit();
               }
                if(activity_online)
                    updateActivity();
                updateNotif();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };


    private void calcCoefficients() {
        isMilles = (sharedPref.getInt(Constants.UNIT_POS, 0) == 0);
        int height;
        if (sharedPref.getInt(Constants.HEIGHT_POS, 0) == 0) {   //inch  ft
            int ft = sharedPref.getInt(Constants.HEIGHT_FT, 5);
            int in = sharedPref.getInt(Constants.HEIGHT_IN, 4);
            height = (int) (ft * 30.48f + in * 2.54f);
        } else
            height = sharedPref.getInt(Constants.HEIGHT_CM, 170);
        if (height > 220 || height < 110) height = 160;

        if (sharedPref.getInt(Constants.WEIGHT_POS, 0) == 0)    //lb.
            weight = (int) (sharedPref.getInt(Constants.WEIGHT_LB, 140) * 0.454f);
        else
            weight = sharedPref.getInt(Constants.WEIGHT_KG, 60);
        if (weight > 136 || weight < 20) weight = 60;  //kg

        int age = sharedPref.getInt(Constants.AGE, 30);
        if (age > 88 || age < 4) age = 17;
        consum_specific = 0.74f + 1 / ((float) age);   //  normal 0.78

        float step_length_cm;
        if (sharedPref.getInt(Constants.SEX_POS, 0) == 0)  //man
            step_length_cm = height * 0.415f;
        else                                            //woman
            step_length_cm = height * 0.413f;  //cm
        step_length_km = step_length_cm / 100000;

        //km = mSteps * step_length_km;
    }

    private void updateActivity() {
        broadcastIntent.putExtra(Constants.M_STEPS, mSteps);
        float km = mSteps * step_length_km;
        if (isMilles)
            broadcastIntent.putExtra(Constants.DISTANTA_STATUS, km / 1.61f);
        else broadcastIntent.putExtra(Constants.DISTANTA_STATUS, km);
        broadcastIntent.putExtra(Constants.CAL_STATUS, (int) (weight * km * consum_specific));
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
    }

    private void setUpAsForeground() {
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle(getString(R.string.app_name))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                        // .setContentText("steps")
                .setShowWhen(false);
        /*if (isMilles)
            mBuilder.setContentText(String.format("%.2f", km / 1.61f) + " mi.");
        else
            mBuilder.setContentText(String.format("%.2f", km) + " km");*/
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent notifyIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(notifyIntent);
        mNotification = mBuilder.build();
        startForeground(1, mNotification);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private void updateNotif() {
       /* if (isMilles)
            mBuilder.setContentText(String.valueOf(mSteps) + " steps        " + String.format("%.2f", km / 1.61f) + " mi.");
        else
            mBuilder.setContentText(String.valueOf(mSteps) + " steps        " + String.format("%.2f", km) + " km");
        mNotificationManager.notify(1, mBuilder.build());*/
    }

    @Override
    public void onDestroy() {
        sensorManager.unregisterListener(mListener);
        editor.putInt(Constants.M_PREVIOUS_COUNTER_STEPS, mSteps);
        editor.commit();
        alarm.cancelAlarm(this);
        super.onDestroy();
       // stopForeground(true);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
