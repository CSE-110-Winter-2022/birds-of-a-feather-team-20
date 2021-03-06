package com.example.birds_of_a_feather_team_20;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class EditProfile extends AppCompatActivity {

    String name;
    String photoURL;

    GoogleSignInClient signInClient;
    static final int RC_SIGN_IN = 1;
    boolean signedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Create sign-in object to request basic account information
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestProfile()
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        signInClient = GoogleSignIn.getClient(this, gso);

        setContentView(R.layout.activity_profile);

        getNameAndURL();;

        setContentView(R.layout.activity_profile);
        setTitle("Edit Profile");
        getNameAndURL();
    }

    public void onCoursesClicked(View view) {
//        Intent intent = new Intent(this, EditCourses.class);
//        startActivity(intent);
        saveProfile();
        goToEditCourses();
//        finish();
    }

    @Override
    public void onBackPressed() {
        saveProfile();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    public void getNameAndURL() {
        TextView nameView = (TextView)findViewById(R.id.name_textview);
        name = MyProfile.singleton(getApplicationContext()).getName();
        nameView.setText(name);


        TextView urlView = (TextView)findViewById(R.id.photo_url_textview);
        photoURL = MyProfile.singleton(getApplicationContext()).getPhotoURL();
        urlView.setText(photoURL);
    }


    public void goToEditCourses() {
//        finish();
        Intent intent = new Intent(this, EditCourses.class);
        startActivity(intent);
    }
    public void saveProfile() {
        TextView nameView = (TextView)findViewById(R.id.name_textview);
        name = nameView.getText().toString();
        MyProfile.singleton(getApplicationContext()).setName(name);

        TextView urlView = (TextView)findViewById(R.id.photo_url_textview);
        photoURL = urlView.getText().toString();
        MyProfile.singleton(getApplicationContext()).setPhotoURL(photoURL);
    }

    public void onClickFinish(View view) {
        saveProfile();
        finish();
    }
    public void onClickViewMyProfile(View view) {
        saveProfile();
//        finish();
        Context context = view.getContext();
        Intent intent = new Intent(context, DisplayProfile.class);
        intent.putExtra("index_in_profilesList", -1);
        intent.putExtra("profile_data", MyProfile.singleton(this).serialize());
        context.startActivity(intent);
    }

    public void onLaunchDebugClicked(View view) {
        onLaunchFriendsClicked(view);
        Intent intent = new Intent(this, DebugActivity.class);
        startActivity(intent);
    }

    public void onLaunchFavoritesClicked(View view) {
        onLaunchFriendsClicked(view);
        Intent intent = new Intent(this, FavoriteActivity.class);
        startActivity(intent);
    }

    public void onLaunchFriendsClicked(View view) {
        onClickFinish(view);
    }

    public void onViewSessionsClicked(View view) {
        onLaunchFriendsClicked(view);
        Intent intent = new Intent(this, ViewSessionsActivity.class);
        startActivity(intent);
    }

    /* Code below handles Google login button */
    public void onLoginClicked(View view){
        if(signedIn == true){
            signOut();
        } else {
            signIn();
        }
    }

    private void signIn() {
        Intent signInIntent = signInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        //Get result from the signIn() method
        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask){
        try{
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            signedIn = true;

            //Update greeting
            TextView loginTextview = (TextView) findViewById(R.id.login_status_textview);
            loginTextview.setText("You are signed in as " + account.getEmail());

            Button loginButton = (Button) findViewById(R.id.google_login_button);
            loginButton.setText("Sign Out of Google");

            name = account.getGivenName();
            TextView nameTextview = (TextView) findViewById(R.id.name_textview);
            nameTextview.setText(name);
            MyProfile.singleton(getApplicationContext()).setName(name);

        } catch(ApiException e){
            Log.e("Sign in failure", "Status code: " + e.getStatusCode());
        }
    }

    private void signOut() {
        signInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task){
                        TextView loginTextview = (TextView) findViewById(R.id.login_status_textview);
                        loginTextview.setText("You are not signed in to Google.");

                        Button loginButton = (Button) findViewById(R.id.google_login_button);
                        loginButton.setText("Sign In with Google");

                        signedIn = false;

                        name = null;
                        TextView nameTextview = (TextView) findViewById(R.id.name_textview);
                        nameTextview.setText(name);
                        MyProfile.singleton(getApplicationContext()).setName(name);
                    }
                });
    }

    /* Code above handles google login */

}