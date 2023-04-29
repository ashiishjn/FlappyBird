package com.example.flappybird;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PurchaseScreenActivity extends AppCompatActivity {



    TextView elixir;
    TextView purchase_description;

    TextView status;
    int position;
    String Type;



    SQLiteDatabase sqLiteDatabase;

    @Override
    public void onBackPressed() {

        if(Type.equals("Background")){
            startActivity(new Intent(PurchaseScreenActivity.this, BackgroundActivity.class));
            finish();
        }
        else{
            startActivity(new Intent(PurchaseScreenActivity.this, CharacterActivity.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_screen);

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

        purchase_description = findViewById(R.id.purchase_description);
        elixir = findViewById(R.id.elixir);
        elixir.setText(String.valueOf(GameInfo.elixir));
        status = findViewById(R.id.status);
        Intent intent = getIntent();
        position = Integer.parseInt(intent.getStringExtra("Position"));
        Type = intent.getStringExtra("Type");
        if(Type.equals("Background"))
            setBackground();
        else
            setCharacteer();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public void setBackground(){
        purchase_description.setText("Are you sure you want to purchase this background for "+
                Integer.toString(GameInfo.background_purchase_price[position])+ " elixir?");
        ImageView image = findViewById(R.id.image);
        image.setImageResource(GameInfo.background_Purchased_img[position]);
    }
    public void setCharacteer(){
        purchase_description.setText("Are you sure you want to purchase this background for "+
                Integer.toString(GameInfo.character_purchase_price[position])+ " elixir?");
        ImageView image = findViewById(R.id.image);
        image.setImageResource(GameInfo.character_Purchased_img[position]);
    }
    public void TapYes(View v){
        final Handler handler = new Handler();
        if(Type.equals("Background") && GameInfo.background_info[position] == 0){
            if(GameInfo.elixir >= GameInfo.background_purchase_price[position]){
                GameInfo.background_info[position] = 1;
                status.setText("Thank you purchasing the item");
                GameInfo.elixir -= GameInfo.background_purchase_price[position];
                elixir.setText(String.valueOf(GameInfo.elixir));

                updateDatabase();
            }
            else {
                status.setText("You don't have enough elixir to purchase this item");
            }
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(PurchaseScreenActivity.this, BackgroundActivity.class));
                    finish();
                }
            }, 1000);
        }
        else if(Type.equals("Character") && GameInfo.character_info[position] == 0){
            if(GameInfo.elixir >= GameInfo.character_purchase_price[position]){
                GameInfo.character_info[position] = 1;
                status.setText("Thank you purchasing the item");
                GameInfo.elixir -= GameInfo.character_purchase_price[position];
                elixir.setText(String.valueOf(GameInfo.elixir));

                updateDatabase();
            }
            else {
                status.setText("You don't have enough elixir to purchase this item");
            }
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(PurchaseScreenActivity.this, CharacterActivity.class));
                    finish();
                }
            }, 1000);
        }
    }
    public void TapNo(View v){
        if(Type.equals("Background")){
            startActivity(new Intent(this, BackgroundActivity.class));
            finish();
        }
        else {
            startActivity(new Intent(this, CharacterActivity.class));
            finish();
        }
    }
    public void updateDatabase(){
        if(!GameInfo.guestUser) {
            String key = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(key);
            String message = "";
            if (Type.equals("Background")) {
                for (int i = 0; i < 3; i++) {
                    message += String.valueOf(GameInfo.background_info[i]);
                }
                reference.child("Background Purchased").setValue(message);
            } else {
                for (int i = 0; i < 4; i++) {
                    message += String.valueOf(GameInfo.character_info[i]);
                }
                reference.child("Character Purchased").setValue(message);
            }
            reference.child("Elixir").setValue(String.valueOf(GameInfo.elixir));
        }
        else {
            sqLiteDatabase.execSQL("Update offlineData set Elixir = " + Integer.toString(GameInfo.elixir)+";");
            String message = "";
            if (Type.equals("Background")) {
                for (int i = 0; i < 3; i++) {
                    message += String.valueOf(GameInfo.background_info[i]);
                }
                sqLiteDatabase.execSQL("Update offlineData set Background_Purchased = '" + message +"';");
            } else {
                for (int i = 0; i < 4; i++) {
                    message += String.valueOf(GameInfo.character_info[i]);
                }
                sqLiteDatabase.execSQL("Update offlineData set Character_Purchased ='" + message +"';");
            }
            sqLiteDatabase.execSQL("Update offlineData set Elixir = " + Integer.toString(GameInfo.elixir)+";");
        }
    }
}