package com.ancalutu.step_counter2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity {
    boolean setviceOn = true;
    View coordinatorLayout;
    FloatingActionButton fab;
    private TextView steps_value, km_value, calories_value;
    public BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra(Constants.M_STEPS)) {
                steps_value.setText(String.valueOf(intent.getIntExtra(Constants.M_STEPS, 0)));
                km_value.setText(String.format("%.2f", intent.getFloatExtra(Constants.DISTANTA_STATUS, 0f)));
                calories_value.setText(String.valueOf(intent.getIntExtra(Constants.CAL_STATUS, 0)));
            }
        }
    };
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        final Intent serviceIntent = new Intent(this, MainService.class);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (setviceOn) {
                    stopService(serviceIntent);
                    setviceOn = false;
                    Snackbar.make(view, getString(R.string.goodbye), Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                    fab.setImageResource(android.R.drawable.button_onoff_indicator_off);
                } else {
                    startService(serviceIntent.setAction(Constants.ACTION_ACTIVITY_ONLINE));
                    setviceOn = true;
                    Snackbar.make(view, getString(R.string.startedMessage), Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                    fab.setImageResource(android.R.drawable.button_onoff_indicator_on);
                }

            }
        });
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        steps_value = (TextView) findViewById(R.id.steps);
        km_value = (TextView) findViewById(R.id.km);
        calories_value = (TextView) findViewById(R.id.calories);
        setNewImage();
        BootReceiver.enableBootReceiver(this);
        load_AdBanner();


    }

    public void goToSettings(View v) {
        startActivity(new Intent(this, Settings.class));
    }

    private void load_AdBanner() {
        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(getString(R.string.test_device))
                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, Settings.class));
            return true;
        } else if (id == R.id.action_history) {
            Snackbar.make(coordinatorLayout, sharedPref.getInt(Constants.STEPS_HISTORY, 0) + " " + getString(R.string.steps_label), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
//            Toast.makeText(this, String.valueOf(sharedPref.getInt(Constants.STEPS_HISTORY, 0) + " steps"), Toast.LENGTH_LONG).show();
//            new EndpointsAsyncTask().execute(new Pair<Context, String>(this, "Dan"));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setNewImage() {
//        FrameLayout frame= (FrameLayout) getActivity().findViewById(R.id.frame_photo);
        ImageView img = (ImageView) findViewById(R.id.backgroundImageView);
        try {
            File f = new File(this.getFilesDir(), Constants.MY_PHOTO);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            if (b != null)
                img.setImageBitmap(b);
            else
                img.setImageResource(R.drawable.landscape);
        } catch (FileNotFoundException e) {

            img.setImageResource(R.drawable.landscape);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter(Constants.BROADCAST_ACTION));
        ((TextView) (findViewById(R.id.km_text))).setText(getResources().getStringArray(R.array.units_array)[sharedPref.getInt(Constants.UNIT_POS, getResources().getInteger(R.integer.Imperial_Units))]);  //milles default
        startService(new Intent(this, MainService.class).setAction(Constants.ACTION_ACTIVITY_ONLINE));
        fab.setImageResource(android.R.drawable.button_onoff_indicator_on);
        setviceOn = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (setviceOn)
            startService(new Intent(this, MainService.class).setAction(Constants.ACTION_ACTIVITY_OFFLINE));
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }
}
