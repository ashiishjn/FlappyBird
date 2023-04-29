package com.example.flappybird;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SoundActivity extends AppCompatActivity {

    AlertDialog alertDialog1, alertDialog2;
    LinearLayout sound_box_layout;
    TextView elixir;

    boolean edit = false;

    float dpHeight, dpWidth;

    DisplayMetrics displayMetrics;

    SQLiteDatabase sqLiteDatabase;

    @Override
    public void onBackPressed() {

        startActivity(new Intent(this, HomeScreenActivity.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound);

//        ScrollView ll = findViewById(R.id.linearLayout);
        displayMetrics = this.getResources().getDisplayMetrics();
        dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        dpWidth = (float)displayMetrics.widthPixels / (float)displayMetrics.density;
        if(dpWidth < 400)
            edit = true;


        if(!isNetworkConnected() && GameInfo.guestUser) {
            // setup the alert builder
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle("Connection Error");
            builder.setMessage("Unable to connect with the server. Please check your internet connection and try again.");
            // add a button
            builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // do something like...
//                    launchMissile();
                    startActivity(new Intent(getApplicationContext(), SplashActivity.class));
                    finish();
                }
            });
            // create and show the alert dialog
            android.app.AlertDialog dialog = builder.create();
            dialog.show();
        }

        sqLiteDatabase = openOrCreateDatabase("Data",MODE_PRIVATE,null);

        loadReward();
        sound_box_layout = findViewById(R.id.sound_box_layout);

        elixir = findViewById(R.id.elixir);

        elixir.setText(Integer.toString(GameInfo.elixir));

        for(int i=0;i<30;i++){
            addCard(i);
        }




    }

    private void buildDialogTowatchAd(int tag) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Watch an Ad if you want to listen to sound without purchasing it.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        watchAd(tag);
                        }
                });

        alertDialog2 = builder.create();

    }

    private void buildDialogForPurchase(int tag) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Do you want to purchase this sound for "+Integer.toString(tag*500)+" elixir?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(GameInfo.elixir >= tag*500) {
                            Toast.makeText(SoundActivity.this, "Thank you for the purchase", Toast.LENGTH_SHORT).show();
                            sound_box_layout.removeAllViews();
                            GameInfo.elixir -= (tag*500);
                            GameInfo.sound_info[tag] = 1;
                            GameInfo.sound_listen_ad[tag] = 1;
                            elixir.setText(Integer.toString(GameInfo.elixir));
                            soundPurchasedUpdateDatabase();
                            soundListenAd();
                            for (int i = 0; i < 30; i++) {
                                addCard(i);
                            }
                        }
                        else
                            Toast.makeText(SoundActivity.this, "You don't have enough elixir", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        alertDialog1 = builder.create();
    }


    private void addCard(int n) {
        final View view = getLayoutInflater().inflate(R.layout.sound_card_layout, null);

        view.setTag(String.valueOf(n));

        ImageView music_main_background = view.findViewById(R.id.music_main_background);
        ImageView music_play_pause = view.findViewById(R.id.music_play_pause);
        ImageView music_purchase = view.findViewById(R.id.music_purchase);

        if(edit){
            music_main_background.getLayoutParams().width = (int)((dpWidth-40) * displayMetrics.density);
            music_main_background.getLayoutParams().height = (int)(((dpWidth-40) / 4) * displayMetrics.density);


            music_play_pause.getLayoutParams().width = (int)((((dpWidth-40) / 4) * 0.7) * displayMetrics.density);
            music_play_pause.getLayoutParams().height = (int)((((dpWidth-40) / 4) * 0.7) * displayMetrics.density);

            ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(music_play_pause.getLayoutParams());
            marginParams.setMargins(
                    (int)((((dpWidth-40) / 4) * 0.15) * displayMetrics.density),
                    (int)((((dpWidth-40) / 4) * 0.15) * displayMetrics.density),
                    (int)((((dpWidth-40) / 4) * 0.15) * displayMetrics.density),
                    (int)((((dpWidth-40) / 4) * 0.15) * displayMetrics.density)
            );
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
            music_play_pause.setLayoutParams(layoutParams);


            music_purchase.getLayoutParams().height = (int)((((dpWidth-40) / 4) * 0.5) * displayMetrics.density);
            music_purchase.getLayoutParams().width = (int)((((dpWidth-40) / 4) * 0.5) * displayMetrics.density);

            marginParams = new ViewGroup.MarginLayoutParams(music_purchase.getLayoutParams());
            marginParams.setMargins(
                    (int)((dpWidth - 40 - (((dpWidth-40) / 4) * 0.75)) * displayMetrics.density),
                    (int)((((dpWidth-40) / 4) * 0.25) * displayMetrics.density),
                    (int)((((dpWidth-40) / 4) * 0.25) * displayMetrics.density),
                    (int)((((dpWidth-40) / 4) * 0.25) * displayMetrics.density)
            );

            layoutParams = new RelativeLayout.LayoutParams(marginParams);
            music_purchase.setLayoutParams(layoutParams);
        }

        music_purchase.setTag(String.valueOf(n));
        music_main_background.setTag(String.valueOf(n));
        music_play_pause.setTag(String.valueOf(n));

        if(GameInfo.sound_selected == n){
            music_main_background.setImageResource(GameInfo.sound_selected_img[n]);
        }
        else if(GameInfo.sound_info[n] == 1){
            music_main_background.setImageResource(GameInfo.sound_Purchased_img[n]);
        }
        else {
            music_main_background.setImageResource(GameInfo.sound_Purchased_img[n]);
            music_purchase.setImageResource(R.drawable.lock);
        }
        music_play_pause.setImageResource(R.drawable.play_sound);

        music_purchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int tag = Integer.parseInt(view.getTag().toString());
                if(GameInfo.sound_info[tag] == 0) {
//                    x = tag;
                    buildDialogForPurchase(tag);
                    alertDialog1.show();

                }
            }
        });

        music_main_background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int tag = Integer.parseInt(view.getTag().toString());
                if(GameInfo.sound_info[tag] == 1){
                    GameInfo.sound_selected = tag;
                    soundActiveUpdateOnDatabase(tag);
                    sound_box_layout.removeAllViews();
                    for(int i=0;i<30;i++){
                        addCard(i);
                    }
                }
            }
        });

        music_play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int tag = Integer.parseInt(view.getTag().toString());
                if(GameInfo.sound_listen_ad[tag] == 1)
                    playTapSound(tag);
                else{
//                    x=tag;
                    buildDialogTowatchAd(tag);
                    alertDialog2.show();
//                    alertDialog2.getWindow().setLayout(1000, 600);
                }
            }
        });

        sound_box_layout.addView(view);
    }

    MediaPlayer media;

    public void playTapSound(int i) {
        if(media!= null && media.isPlaying())
        { media.stop();
            media.reset();
            media.release();
            media = null;}
        media=MediaPlayer.create(SoundActivity.this,GameInfo.sounds[i]);
        media.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                media.start();
            }
        });
//        media= MediaPlayer.create(SoundActivity.this, sound[i]);
//        media.start();
//        media.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            public void onCompletion(MediaPlayer mp) {
//                mp.reset();
//                mp.release();
//            }
//        });
    }



    public void soundActiveUpdateOnDatabase(int tag){
        if(!GameInfo.guestUser) {
            String key = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(key);
            reference.child("Sound Active").setValue(String.valueOf(tag));
        }
        else{
            sqLiteDatabase.execSQL("Update offlineData set Sound_Active = " + Integer.toString(tag)+";");
        }
    }

    public void soundPurchasedUpdateDatabase(){
        String message = "";
        for (int i = 0; i < 30; i++) {
            message += String.valueOf(GameInfo.sound_info[i]);
        }
        if(!GameInfo.guestUser) {
            String key = FirebaseAuth.getInstance().getCurrentUser().getUid();

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(key);
            reference.child("Elixir").setValue(String.valueOf(GameInfo.elixir));

            reference.child("Sound Purchased").setValue(message);
        }
        else{
            sqLiteDatabase.execSQL("Update offlineData set Elixir = " + Integer.toString(GameInfo.elixir)+";");
            sqLiteDatabase.execSQL("Update offlineData set Sound_Purchased = '" + message+"';");
        }
    }

    public void soundListenAd(){
        String message = "";
        for (int i = 0; i < 30; i++) {
            message += String.valueOf(GameInfo.sound_listen_ad[i]);
        }
        if(!GameInfo.guestUser) {
            String key = FirebaseAuth.getInstance().getCurrentUser().getUid();

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(key);

            reference.child("Sound Listen Ad").setValue(message);
        }
        else{
            sqLiteDatabase.execSQL("Update offlineData set Sound_Listen_Ad = '" + message+"';");
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private RewardedAd mRewardedAd;

    public void loadReward(){
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(this, "ca-app-pub-3940256099942544/5224354917",
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
//                        Log.i(TAG, loadAdError.toString());
                        mRewardedAd = null;
//                        ImageView img = findViewById(R.id.watchAds);
//                        img.setImageResource(R.drawable.earn_ads);
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;
//                        Log.i(TAG, "Ad was loaded.");
//                        ImageView img = findViewById(R.id.watchAds);
//                        img.setImageResource(R.drawable.earn_ads_ready);
                    }
                });
    }

    public void AdsStats(){
        mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdClicked() {
                // Called when a click is recorded for an ad.
//                Log.d(TAG, "Ad was clicked.");
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                // Set the ad reference to null so you don't show the ad a second time.
//                Log.d(TAG, "Ad dismissed fullscreen content.");
                mRewardedAd = null;
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                // Called when ad fails to show.
//                Log.e(TAG, "Ad failed to show fullscreen content.");
                mRewardedAd = null;
            }

            @Override
            public void onAdImpression() {
                // Called when an impression is recorded for an ad.
//                Log.d(TAG, "Ad recorded an impression.");
            }

            @Override
            public void onAdShowedFullScreenContent() {
                // Called when ad is shown.
//                Log.d(TAG, "Ad showed fullscreen content.");
            }

        });
    }

    public void watchAd(int x){
        if (mRewardedAd != null) {
            Activity activityContext = SoundActivity.this;
            AdsStats();
            mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    // Handle the reward.
//                    Log.d(TAG, "The user earned the reward.");
                    GameInfo.sound_listen_ad[x] = 1;
                    soundListenAd();
                }
            });
        }
        else {
            Toast.makeText(this, "There's no ad available", Toast.LENGTH_SHORT).show();
//            Log.d(TAG, "The rewarded ad wasn't ready yet.");
        }
        loadReward();

    }

}