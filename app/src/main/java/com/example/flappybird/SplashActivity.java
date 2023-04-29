package com.example.flappybird;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    int progressStatus = 0;

    ProgressBar progressBar;
    TextView textView;

    Handler handler;

    @Override
    public void onBackPressed() {
        finishAffinity();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();
//        mAuth = null;
//        if(mAuth.getCurrentUser() == null){
//            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
//            finish();
//        }
//        mAuth.signOut();
        //****************************************

        handler = new Handler();

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        textView = (TextView) findViewById(R.id.textView);

    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(isNetworkConnected()) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (currentUser == null) {
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        finish();
                    }
                    else {
//                    startActivity(new Intent(getApplicationContext(), HomeScreenActivity.class));
//                    finish();
                        textView.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.VISIBLE);
                        updateProgressBar(50);
                        fetchData();
                    }
                }
            }, 1000);
        }
        else {
            // setup the alert builder
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
            AlertDialog dialog = builder.create();
            dialog.show();

        }
    }

    public void fetchData(){
        DatabaseReference reference = FirebaseDatabase.getInstance().
                getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    GameInfo.highestScores = Integer.parseInt(snapshot.child("Highest Score").getValue(String.class));
                    GameInfo.elixir = Integer.parseInt(snapshot.child("Elixir").getValue(String.class));
                    GameInfo.background_selected = Integer.parseInt(snapshot.child("Background Active").getValue(String.class));
                    int b_Purchased = Integer.parseInt(snapshot.child("Background Purchased").getValue(String.class));
                    GameInfo.character_selected = Integer.parseInt(snapshot.child("Character Active").getValue(String.class));
                    int c_Purchased = Integer.parseInt(snapshot.child("Character Purchased").getValue(String.class));
                    GameInfo.sound_selected = Integer.parseInt(snapshot.child("Sound Active").getValue(String.class));
                    String s_Purchased = snapshot.child("Sound Purchased").getValue(String.class);
                    String s_watch_ad = snapshot.child("Sound Listen Ad").getValue(String.class);
                    for(int i = 3; i>=0; i--){
                        GameInfo.character_info[i] = c_Purchased % 10;
                        c_Purchased/=10;
                    }
                    for(int i = 2; i>=0; i--){
                        GameInfo.background_info[i] = b_Purchased % 10;
                        b_Purchased/=10;
                    }
                    for(int i = 0; i<30; i++){
                        GameInfo.sound_info[i] = (int)(s_Purchased.charAt(i)-'0');
                        GameInfo.sound_listen_ad[i] = (int)(s_watch_ad.charAt(i)-'0');
                    }
                    updateProgressBar(100);
                }
                catch (Exception e){
                    Toast.makeText(SplashActivity.this,
                            "There seems to be some error. Please retry after some time.",
                            Toast.LENGTH_SHORT).show();
                    Log.i("Error", e.getMessage());
                    finishAffinity();
                    finish();
                }

//                startActivity(new Intent(getApplicationContext(), HomeScreenActivity.class));
//                finish();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SplashActivity.this,
                        "There seems to be some error. Please retry after some time.",
                        Toast.LENGTH_SHORT).show();
                Log.i("Error", error.getMessage());
                finishAffinity();
                finish();
            }
        });
    }

    public void updateProgressBar(int n){
        new Thread(new Runnable() {
            public void run() {
                while (progressStatus < n) {
                    progressStatus += 1;
                    // Update the progress bar and display the
                    //current value in the text view
                    handler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progressStatus);
                            textView.setText(progressStatus+"%");
                        }
                    });
                    try {
                        // Sleep for 200 milliseconds.
                        Thread.sleep(25);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if(progressStatus == 100) {
                    Intent intent = new Intent(SplashActivity.this, HomeScreenActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        }).start();
    }
}