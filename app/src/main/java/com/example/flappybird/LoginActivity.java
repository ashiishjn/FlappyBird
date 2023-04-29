package com.example.flappybird;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LoginActivity extends AppCompatActivity{


    AlertDialog alertDialog;

    SignInButton btSignIn;
    GoogleSignInClient googleSignInClient;
    FirebaseAuth firebaseAuth;

    FirebaseDatabase database;

    //SQLDatabase
    SQLiteDatabase sqLiteDatabase;

    @Override
    public void onBackPressed() {
        finishAffinity();
        finish();
    }

    int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        checkNetwork();

        //SQLLiteDatabse
        sqLiteDatabase = openOrCreateDatabase("Data",MODE_PRIVATE,null);


//        sqLiteDatabase.execSQL("Drop TABLE offlineData");/
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS offlineData(id VARCHAR PRIMARY KEY, Elixir VARCHAR, " +
                "Background_Active VARCHAR, Background_Purchased VARCHAR, Character_Active VARCHAR, Character_Purchased VARCHAR, " +
                "Sound_Active VARCHAR, Sound_Purchased VARCHAR, Sound_Listen_Ad VARCHAR, Highest_Score VARCHAR);");


        // Initialize firebase auth
        firebaseAuth=FirebaseAuth.getInstance();
        // Initialize firebase user
        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
        // Check condition
        if(firebaseUser!=null)
        {
            // When user already sign in
            // redirect to profile activity
            startActivity(new Intent(LoginActivity.this,HomeScreenActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }


        buildDialog();
        btSignIn=findViewById(R.id.google_sign_in_button);

        // Initialize sign in options
        // the client-id is copied form
        // google-services.json file
        GoogleSignInOptions googleSignInOptions=new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN
        ).requestIdToken("846655374669-nqa92oghq2vd0h9h8u6h1fl47ad0e1du.apps.googleusercontent.com")
                .requestEmail()
                .build();

        // Initialize sign in client

        googleSignInClient= GoogleSignIn.getClient(
                LoginActivity.this
                ,googleSignInOptions);
        googleSignInClient.signOut();

        btSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Initialize sign in intent
                Intent intent=googleSignInClient.getSignInIntent();
                // Start activity for result
                startActivityForResult(intent,100);
            }
        });

    }


    public void checkNetwork(){
        if(!isNetworkConnected()) {
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check condition
        if(requestCode==100)
        {
            // When request code is equal to 100
            // Initialize task
            checkNetwork();
            Task<GoogleSignInAccount> signInAccountTask= GoogleSignIn
                    .getSignedInAccountFromIntent(data);

            // check condition
            if(signInAccountTask.isSuccessful())
            {
                // When google sign in successful
                // Initialize sign in account
                try {
                    // Initialize sign in account
                    GoogleSignInAccount googleSignInAccount=signInAccountTask
                            .getResult(ApiException.class);
                    // Check condition
                    if(googleSignInAccount!=null)
                    {
                        // When sign in account is not equal to null
                        // Initialize auth credential
                        AuthCredential authCredential= GoogleAuthProvider
                                .getCredential(googleSignInAccount.getIdToken()
                                        ,null);
                        // Check credential
                        firebaseAuth.signInWithCredential(authCredential)
                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        // Check condition
                                        if(task.isSuccessful())
                                        {
                                           displayToast( "Sign in successful");
                                            boolean isNew = task.getResult().getAdditionalUserInfo().isNewUser();
                                            if(isNew){
                                                GameInfo.highestScores = 0;
                                                GameInfo.elixir = 1000;
                                                GameInfo.character_selected = 0;
                                                GameInfo.background_selected = 0;
                                                for(int i=1;i<4;i++){
                                                    GameInfo.character_info[i]=0;
                                                }
                                                for(int i=1;i<3;i++){
                                                    GameInfo.background_info[i]=0;
                                                }
                                                for(int i=1;i<30;i++){
                                                    GameInfo.sound_listen_ad[i]=0;
                                                    GameInfo.sound_info[i]=0;
                                                }
                                                GameInfo.background_info[0]=1;
                                                GameInfo.character_info[0]=1;
                                                GameInfo.sound_listen_ad[0]=1;
//                                                Toast.makeText(LoginActivity.this, "SignUp successful!", Toast.LENGTH_SHORT).show();
                                                Intent i = new Intent(getApplicationContext(), HomeScreenActivity.class);
                                                startActivity(i);
                                                finish();
                                                DatabaseReference reference = database.getInstance().getReference("Users");
                                                String key = firebaseAuth.getUid();
                                                reference.child(key).child("Mail Id").setValue(firebaseAuth.getCurrentUser().getEmail());
                                                reference.child(key).child("Elixir").setValue("1000");
                                                reference.child(key).child("Character Purchased").setValue("1000");
                                                reference.child(key).child("Background Purchased").setValue("100");
                                                reference.child(key).child("Character Active").setValue("0");
                                                reference.child(key).child("Background Active").setValue("0");
                                                reference.child(key).child("Sound Purchased").setValue("100000000000000000000000000000000000000000000000000000000000");
                                                reference.child(key).child("Sound Listen Ad").setValue("100000000000000000000000000000000000000000000000000000000000");
                                                reference.child(key).child("Sound Active").setValue("0");
                                                reference.child(key).child("Highest Score").setValue("0");
                                            }
                                            else {
                                                fetchData();
                                            }
                                        }
                                        else
                                        {
                                            googleSignInClient.signOut();
                                            // When task is unsuccessful
                                            // Display Toast
                                            displayToast("Failed to logIn. Try again after some time.");
                                            Log.i("Error",task.getException().getMessage());
                                        }
                                    }
                                });
                    }
                }
                catch (ApiException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private void displayToast(String s) {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
    }

    public void fetchData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().
                child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
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
                    GameInfo.elixir = Integer.parseInt(snapshot.child("Elixir").getValue(String.class));
                    GameInfo.sound_selected = Integer.parseInt(snapshot.child("Sound Active").getValue(String.class));
                    String s_Purchased = snapshot.child("Sound Purchased").getValue(String.class);
                    String s_watch_ad = snapshot.child("Sound Listen Ad").getValue(String.class);
                    for (int i = 3; i >= 0; i--) {
                        GameInfo.character_info[i] = c_Purchased % 10;
                        c_Purchased /= 10;
                    }
                    for (int i = 2; i >= 0; i--) {
                        GameInfo.background_info[i] = b_Purchased % 10;
                        b_Purchased /= 10;
                    }
                    for(int i = 0; i<30; i++){
                        GameInfo.sound_info[i] = (int)(s_Purchased.charAt(i)-'0');
                        GameInfo.sound_listen_ad[i] = (int)(s_watch_ad.charAt(i)-'0');
                    }
                    startActivity(new Intent(getApplicationContext(), HomeScreenActivity.class));
                    finish();
                } catch (Exception e) {
                    Toast.makeText(LoginActivity.this,
                            "There seems to be some error. Please retry after some time.",
                            Toast.LENGTH_SHORT).show();
                    finishAffinity();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this,
                        "There seems to be some error. Please retry after some time.",
                        Toast.LENGTH_SHORT).show();
                finishAffinity();
                finish();
            }
        });

    }
        public void guest(View v){
        alertDialog.show();

    }

    private void buildDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Your data won't be saved")
                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        GameInfo.guestUser = true;

                        long count = DatabaseUtils.queryNumEntries(sqLiteDatabase, "offlineData");
                        if(count == 0){
                            sqLiteDatabase.execSQL("INSERT INTO offlineData VALUES('101','1000', '0', '100', '0', '1000', " +
                                    "'0', '100000000000000000000000000000000000000000000000000000000000', " +
                                         "'100000000000000000000000000000000000000000000000000000000000', '0');");

                            GameInfo.highestScores = 0;
                            GameInfo.elixir = 1000;
                            GameInfo.character_selected = 0;
                            GameInfo.background_selected = 0;
                            GameInfo.sound_selected = 0;
                            for(int i=1;i<4;i++){
                                GameInfo.character_info[i]=0;
                            }
                            for(int i=1;i<3;i++){
                                GameInfo.background_info[i]=0;
                            }
                            for(int i=1;i<30;i++){
                                GameInfo.sound_info[i]=0;
                                GameInfo.sound_listen_ad[i]=0;
                            }
                            GameInfo.background_info[0]=1;
                            GameInfo.character_info[0]=1;
                            GameInfo.sound_listen_ad[0]=1;
                            GameInfo.sound_info[0]=1;
                        }
                        else{
                            Cursor c = sqLiteDatabase.rawQuery("Select * from offlineData;", null);
                            c.moveToFirst();
                            GameInfo.elixir = Integer.parseInt(c.getString(1));
                            GameInfo.character_selected = Integer.parseInt(c.getString(4));
                            GameInfo.background_selected = Integer.parseInt(c.getString(2));
                            GameInfo.sound_selected = Integer.parseInt(c.getString(6));
                            int c_Purchased = Integer.parseInt(c.getString(5));
                            int b_Purchased = Integer.parseInt(c.getString(3));
                            String s_Purchased = c.getString(7);
                            String s_watch_ad = c.getString(8);
                            GameInfo.highestScores = Integer.parseInt(c.getString(9));
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
                            c.close();
                        }

                        startActivity(new Intent(LoginActivity.this, HomeScreenActivity.class));
                    }
                })
                .setNegativeButton("Sign In", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        GameInfo.guestUser = false;

                    }
                });

        alertDialog = builder.create();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

}