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
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by Dan on 3/8/2015.
 */

public class MainFragment extends Fragment {
    private TextView steps_value, km_value, calories_value;
    private SharedPreferences sharedPref;

    public BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.hasExtra(Constants.M_STEPS)) {
                steps_value.setText(String.valueOf(intent.getIntExtra(Constants.M_STEPS, 0)));
                km_value.setText(String.format("%.2f",intent.getFloatExtra(Constants.DISTANTA_STATUS, 0f)));
                calories_value.setText(String.valueOf(intent.getIntExtra(Constants.CAL_STATUS, 0)));
               // Log.d("tag","broadcastReceived    mSteps "+intent.getIntExtra(Constants.M_STEPS, 0));
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BootReceiver.enableBootReceiver(getActivity());
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            getActivity().startActivity(new Intent(getActivity(), Settings.class));
            return true;
        }else if(id==R.id.action_history) {
            Toast.makeText(getActivity(), String.valueOf(sharedPref.getInt(Constants.STEPS_HISTORY, 0) + " steps"), Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        steps_value = (TextView) getActivity().findViewById(R.id.steps);
        km_value= (TextView) getActivity().findViewById(R.id.km);
        calories_value= (TextView) getActivity().findViewById(R.id.calories);
        setNewImage();
        getActivity().findViewById(R.id.km_mi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(), Settings.class));
            }
        });
      //  if(sharedPref.getBoolean(Constants.CAN_CHANGE_IMAGE, false))
       //     new  DownloadFile().start(getActivity(), Images.imageUrls[new Random().nextInt(Images.imageUrls.length)], Constants.MY_PHOTO);



    }




    private void setNewImage() {
        FrameLayout frame= (FrameLayout) getActivity().findViewById(R.id.frame_photo);
        ImageView img= new ImageView(getActivity());
        try {
            File f=new File(getActivity().getFilesDir(), Constants.MY_PHOTO);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            if(b!=null)
                img.setImageBitmap(b);
            else
                img.setImageResource(R.drawable.landscape);
        } catch (FileNotFoundException e) {

            img.setImageResource(R.drawable.landscape);
        }
        frame.addView(img);
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, new IntentFilter(Constants.BROADCAST_ACTION));
        ((TextView)(getActivity().findViewById(R.id.km_text))).setText(getResources().getStringArray(R.array.units_array)[sharedPref.getInt(Constants.UNIT_POS, 0)]);  //milles default
        getActivity().startService(new Intent(getActivity(), MyService.class).setAction(Constants.ACTION_ACTIVITY_ONLINE));
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().startService(new Intent(getActivity(), MyService.class).setAction(Constants.ACTION_ACTIVITY_OFFLINE));
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
    }







}
