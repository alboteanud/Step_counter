package com.ancalutu.step_counter2;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Dan on 2/19/2015.
 */
public class DownloadFile {
   // Context myContext;
    private File file;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    public void start(Context context, String stringUrl, String fileName) {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        sharedPref= PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPref.edit();
        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data
            file = new File(context.getFilesDir(), fileName);
            new DownloadWebpageTask().execute(stringUrl);
        } else {
            // display error
        }
    }

    private class DownloadWebpageTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                Log.w("TAG", "IOException in doInBackground " );
                return null;
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            FileOutputStream fos = null;
            try {
                if (bitmap != null) {
                    fos = new FileOutputStream(file);
                    if(bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos))
                    fos.close();
                    editor.putBoolean(Constants.CAN_CHANGE_IMAGE, false);
                    editor.commit();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    // Given a URL, establishes an HttpUrlConnection and retrieves
// the web page content as a InputStream, which it returns as
// a string.
    private Bitmap downloadUrl(String myurl) throws IOException {
        InputStream is = null;
        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
        //    Log.d("TAG", "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
         //   String contentAsString = readIt(is, len);
           // return contentAsString;

            Bitmap bitmap = BitmapFactory.decodeStream(is);
         //  ImageView imageView = (ImageView) findViewById(R.id.image_view);
        //   imageView.setImageBitmap(bitmap);
            return bitmap;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

}
