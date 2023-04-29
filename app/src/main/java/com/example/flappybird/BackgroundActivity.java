package com.example.flappybird;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BackgroundActivity extends AppCompatActivity {

    int background_selected_img[] = {
            R.drawable.forest_selected,  R.drawable.sunnyday_selected, R.drawable.underground_selected
    };

    int background_Purchased_img[] = {
            R.drawable.forest,  R.drawable.sunnyday, R.drawable.underground
    };

    int background_To_Purchased_img[] = {
            R.drawable.forest_buy,  R.drawable.sunnyday_buy, R.drawable.underground_buy
    };

    SQLiteDatabase sqLiteDatabase;

    @Override
    public void onBackPressed() {

        startActivity(new Intent(this, HomeScreenActivity.class));
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background);

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

        insertImages();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public void insertImages(){
        TextView tv = findViewById(R.id.elixir);
        tv.setText(String.valueOf(GameInfo.elixir));
        GridLayout gridLayout = findViewById(R.id.backgroundGrid);
        ImageView imageView;
        for(int i=0;i<3;i++){
            imageView = (ImageView) gridLayout.getChildAt(i);
            if(i == GameInfo.background_selected)
                imageView.setImageResource(background_selected_img[i]);
            else if(GameInfo.background_info[i] == 0)
                imageView.setImageResource(background_To_Purchased_img[i]);
            else
                imageView.setImageResource(background_Purchased_img[i]);
        }
    }

    public void Check(View v){
        GridLayout gridLayout = findViewById(R.id.backgroundGrid);
        int tag = Integer.parseInt(v.getTag().toString());
        ImageView imageView;
        if(GameInfo.background_info[tag] == 1){
            updateDatabase(tag);
            imageView = (ImageView) gridLayout.getChildAt(GameInfo.background_selected);
            imageView.setImageResource(background_Purchased_img[GameInfo.background_selected]);
            imageView = (ImageView) gridLayout.getChildAt(tag);
            imageView.setImageResource(background_selected_img[tag]);
            GameInfo.background_selected = tag;
        }
        else {
            Intent intent = new Intent(this, PurchaseScreenActivity.class);
            intent.putExtra("Type","Background");
            intent.putExtra("Position", String.valueOf(tag));
            startActivity(intent);
            finish();
        }
    }

    public void updateDatabase(int tag){
        if(!GameInfo.guestUser) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                    .child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            reference.child("Background Active").setValue(String.valueOf(tag));
        }
        else{
            sqLiteDatabase.execSQL("Update offlineData set Background_Active = " + Integer.toString(tag)+";");
        }
    }
}