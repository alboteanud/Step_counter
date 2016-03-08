package com.ancalutu.step_counter2;

import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;


public class MainActivity extends ActionBarActivity {

    private CountDownTimer mCountDownTimer;
    private InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

      //  initTimer();
    }

 @Override
    protected void onStop() {
     super.onStop();
     if(mCountDownTimer!=null)
        mCountDownTimer.cancel();
    }

    private void requestNewInterstitial(){
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3931793949981809/2126377971");
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mInterstitialAd.show();
            }
        });
        mInterstitialAd.loadAd(new AdRequest.Builder()
                .addTestDevice("D7C3FED6C0273D67D38E0186CFA3B220")
                .build());
    }

    private void initTimer() {
        mCountDownTimer = new CountDownTimer(11000, 11000) {
            @Override
            public void onTick(long millisUnitFinished) {
            }
            @Override
            public void onFinish() {
                  requestNewInterstitial();
            }
        };
        mCountDownTimer.start();
    }

}
