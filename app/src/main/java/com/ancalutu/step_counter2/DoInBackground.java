package com.ancalutu.step_counter2;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

/**
 * Created by Dan on 3/28/2015.
 */
public class DoInBackground{
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private String mKey;

    public void start(Context context, String key, int value) {
        sharedPref= PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPref.edit();
        mKey=key;
            new AsincTask().execute(value);

    }

    private class AsincTask extends AsyncTask<Integer, Void, Void>{
        @Override
        protected Void doInBackground(Integer... value) {
            editor.putInt(mKey, value[0]);
            editor.commit();
            return null;
        }
    }

}
