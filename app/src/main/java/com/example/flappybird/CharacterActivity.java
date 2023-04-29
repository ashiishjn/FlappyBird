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

public class CharacterActivity extends AppCompatActivity {


    int character_selected_img[] = {R.drawable.a1_selected,R.drawable.a2_selected,R.drawable.a3_selected,
                                R.drawable.a4_selected};

    int character_Purchased_img[] = {R.drawable.a1,R.drawable.a2,R.drawable.a3,
            R.drawable.a4};

    int character_To_Purchased_img[] = {R.drawable.a1_buy,R.drawable.a2_buy,R.drawable.a3_buy,
            R.drawable.a4_buy};

    SQLiteDatabase sqLiteDatabase;


    @Override
    public void onBackPressed() {

        startActivity(new Intent(this, HomeScreenActivity.class));
        finish();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character);

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

    public void insertImages(){
        TextView tv = findViewById(R.id.elixir);
        tv.setText(String.valueOf(GameInfo.elixir));
        GridLayout gridLayout = findViewById(R.id.characterGrid);
        ImageView imageView;
        for(int i=0;i<4;i++){
            imageView = (ImageView) gridLayout.getChildAt(i);
            if(i == GameInfo.character_selected)
                imageView.setImageResource(character_selected_img[i]);
            else if(GameInfo.character_info[i] == 0)
                imageView.setImageResource(character_To_Purchased_img[i]);
            else
                imageView.setImageResource(character_Purchased_img[i]);
        }
    }

    public void Check(View v){
        GridLayout gridLayout = findViewById(R.id.characterGrid);
        int tag = Integer.parseInt(v.getTag().toString());
        ImageView imageView;
        if(GameInfo.character_info[tag] == 1){
            updateDatabase(tag);
            imageView = (ImageView) gridLayout.getChildAt(GameInfo.character_selected);
            imageView.setImageResource(character_Purchased_img[GameInfo.character_selected]);
            imageView = (ImageView) gridLayout.getChildAt(tag);
            imageView.setImageResource(character_selected_img[tag]);
            GameInfo.character_selected = tag;
        }
        else {
            Intent intent = new Intent(this, PurchaseScreenActivity.class);
            intent.putExtra("Type","Character");
            intent.putExtra("Position", String.valueOf(tag));
            startActivity(intent);
            finish();
        }
    }

    public void updateDatabase(int tag){
        if(!GameInfo.guestUser) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                    .child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            reference.child("Character Active").setValue(String.valueOf(tag));
        }
        else{
            sqLiteDatabase.execSQL("Update offlineData set Character_Active = " + Integer.toString(tag)+";");
        }
    }
}