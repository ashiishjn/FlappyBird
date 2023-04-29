package com.example.flappybird;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import com.google.android.gms.ads.rewarded.RewardedAd;

public class HomeScreenActivity extends AppCompatActivity {

    AlertDialog alertDialog;
    private RewardedAd mRewardedAd;

    private final String TAG = "HomeScreenActivity";

    SQLiteDatabase sqLiteDatabase;

    @Override
    public void onBackPressed() {
        finishAffinity();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        if(!isNetworkConnected() && GameInfo.guestUser) {
            // setup the alert builder
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle("Connection Error");
            builder.setMessage("Unable to connect with the server. Please check your internet connection and try again.");
            // add a button
            builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(getApplicationContext(), SplashActivity.class));
                    finish();
                }
            });
            // create and show the alert dialog
            android.app.AlertDialog dialog = builder.create();
            dialog.show();
        }

        sqLiteDatabase = openOrCreateDatabase("Data", MODE_PRIVATE,null);

        loadVolumeImage();

        TextView tv = findViewById(R.id.elixir);
        tv.setText(String.valueOf(GameInfo.elixir));

        loadReward();
//        buildDialog();
        //***************

    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public void gameScreen(View v){
//        if(mRewardedAd == null || GameInfo.totalLife == 4) {
            startActivity(new Intent(this, AndroidLauncher.class));
//            finish();
//        }
//        else
//            alertDialog.show();
    }

    public void characterScreen(View v){
        startActivity(new Intent(this, CharacterActivity.class));
        finish();
    }

    public void backgroundScreen(View v){
        startActivity(new Intent(this, BackgroundActivity.class));
        finish();
    }

    public void soundScreen(View v){
        startActivity(new Intent(this, SoundActivity.class));
        finish();
    }

    public void logOut(View v) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    public void earnCoins(View v){
        if(mRewardedAd != null) {
            Activity activityContext = HomeScreenActivity.this;
            AdsStats();
            mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    // Handle the reward.
                     Log.d(TAG, "The user earned the reward.");
                    GameInfo.elixir += 100;
                    if(!GameInfo.guestUser) {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        reference.child("Elixir").setValue(String.valueOf(GameInfo.elixir));
                    }
                    else{
                        sqLiteDatabase.execSQL("Update offlineData set Elixir = " + Integer.toString(GameInfo.elixir)+";");
                    }
                    TextView elixir = findViewById(R.id.elixir);
                    elixir.setText(String.valueOf(GameInfo.elixir));
                    ImageView img = findViewById(R.id.watchAds);
                    img.setImageResource(R.drawable.earn_ads);
                }
            });
        }
        loadReward();

    }

    public void loadReward(){
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(this, "ca-app-pub-3940256099942544/5224354917",
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.i(TAG, loadAdError.toString());
                        mRewardedAd = null;
                        ImageView img = findViewById(R.id.watchAds);
                        img.setImageResource(R.drawable.earn_ads);
                        loadReward();
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;
                        Log.i(TAG, "Ad was loaded.");
                        ImageView img = findViewById(R.id.watchAds);
                        img.setImageResource(R.drawable.earn_ads_ready);
                    }
                });
    }

    public void AdsStats(){
        mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdClicked() {
                // Called when a click is recorded for an ad.
                Log.d(TAG, "Ad was clicked.");
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                // Set the ad reference to null so you don't show the ad a second time.
                Log.d(TAG, "Ad dismissed fullscreen content.");
                mRewardedAd = null;
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                // Called when ad fails to show.
                Log.e(TAG, "Ad failed to show fullscreen content.");
                mRewardedAd = null;
            }

            @Override
            public void onAdImpression() {
                // Called when an impression is recorded for an ad.
                Log.d(TAG, "Ad recorded an impression.");
            }

            @Override
            public void onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.d(TAG, "Ad showed fullscreen content.");
            }

        });
    }

//    private void buildDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//
//        builder.setTitle("Do you want extra life?")
//                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        extraLife();
//                    }
//                })
//                .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        startActivity(new Intent(HomeScreenActivity.this, AndroidLauncher.class));
////                        finish();
//                    }
//                });
//
//        alertDialog = builder.create();
//    }

//    public void extraLife(){
//        if (mRewardedAd != null) {
//            Activity activityContext = HomeScreenActivity.this;
//            AdsStats();
//            mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
//                @Override
//                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
//                    // Handle the reward.
//                    GameInfo.totalLife = 4;
//                    if(isDestroyed()){
//                        startActivity(new Intent(HomeScreenActivity.this, AndroidLauncher.class));
////                        finish();
//                    }
//                }
//            });
//        }
//        loadReward();
//    }

    @Override
    public void onResume() {
        super.onResume();
        sqLiteDatabase.execSQL("Update offlineData set Elixir = " + Integer.toString(GameInfo.elixir)+";");

        if(!isNetworkConnected() && GameInfo.guestUser) {
            // setup the alert builder
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle("Connection Error");
            builder.setMessage("Unable to connect with the server. Please check your internet connection and try again.");
            // add a button
            builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(getApplicationContext(), SplashActivity.class));
                    finish();
                }
            });
            // create and show the alert dialog
            android.app.AlertDialog dialog = builder.create();
            dialog.show();
        }

        if(GameInfo.guestUser) {
            sqLiteDatabase.execSQL("Update offlineData set Elixir = " + Integer.toString(GameInfo.elixir) + ";");
            sqLiteDatabase.execSQL("Update offlineData set Highest_Score = " + Integer.toString(GameInfo.highestScores) + ";");
        }

        loadVolumeImage();

        TextView tv = findViewById(R.id.elixir);
        tv.setText(String.valueOf(GameInfo.elixir));
        loadReward();
    }

    public void loadVolumeImage(){
        ImageView volume = findViewById(R.id.volume);
        if(GameInfo.volume)
            volume.setImageResource(R.drawable.volume_up);
        else
            volume.setImageResource(R.drawable.volume_off);
    }


    public void volume(View v){
        if(GameInfo.volume){
            GameInfo.volume = false;
        }
        else{
            GameInfo.volume = true;
        }
        loadVolumeImage();
    }


}