package com.example.listify;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.amplifyframework.auth.AuthException;
import com.google.android.material.navigation.NavigationView;
import org.json.JSONException;

import java.io.IOException;
import java.util.Properties;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //------------------------------Auth Testing---------------------------------------------//

        /*AuthManager authManager = new AuthManager();
        try {
            authManager.signIn("merzn@purdue.edu", "Password123");
            Log.i("Authentication", authManager.getAuthSession().toString());
            Log.i("Token", authManager.getAuthSession().getUserPoolTokens().getValue().getIdToken());
        }
        catch (AuthException e) {
            Log.i("Authentication", "Login failed. User probably needs to register. Exact error: " + e.getMessage());
            try {
                authManager.startSignUp("merzn@purdue.edu", "Password123");
                authManager.confirmSignUp("######");
            } catch (AuthException signUpError) {
                Log.e("Authentication", "SignUp error: " + signUpError.getMessage());
            }
        }*/

        //----------------------------------API Testing---------------------------------------------//

        /*Properties configs = new Properties();
        try {
            configs = AuthManager.loadProperties(this, "android.resource://" + getPackageName() + "/raw/auths.json");
        }
        catch (IOException|JSONException e) {
            e.printStackTrace();
        }*/

        /*Requestor requestor = new Requestor(this, authManager,configs.getProperty("apiKey"));
        List testList = new List("IAmATestList");
        try {
            requestor.postObject(testList);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }*/

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /*FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });*/
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_lists)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Handle search button click
        ImageButton searchButton = (ImageButton) findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchResults.class);
                // Send user to SearchResults activity
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_left, R.anim.exit_from_left);

            }
        });
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }
}