package com.example.listify;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.amplifyframework.auth.AuthException;
import com.example.listify.data.List;
import com.example.listify.data.ListDuplicate;
import com.example.listify.data.ListReposition;
import com.example.listify.data.SearchHistory;
import com.example.listify.ui.LoginPage;
import com.google.android.material.navigation.NavigationView;
import org.json.JSONException;

import java.io.IOException;
import java.time.Instant;
import java.util.Properties;

import static com.example.listify.SplashActivity.showSplash;

public class MainActivity extends AppCompatActivity implements CreateListDialogFragment.OnNewListListener {
    private AppBarConfiguration mAppBarConfiguration;
    public static AuthManager am = new AuthManager();

    @Override
    public void onBackPressed() {
        String prev = getIntent().getStringExtra("prev");

        if (prev == null) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(showSplash) {
            showSplash = false;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(MainActivity.this, SplashActivity.class);
                    startActivity(intent);
                }
            }, 1);
        }

        if(am.getUserToken().equals("")) {
            am.nullify();
            Intent intent = new Intent(MainActivity.this, LoginPage.class);
            startActivity(intent);
        }

        //------------------------------Auth Testing---------------------------------------------//

        boolean testAuth = false;

        if (testAuth) {

            AuthManager authManager = new AuthManager();
            try {
                authManager.signIn("merzn@purdue.edu", "Password123");
                Log.i("Authentication", authManager.getAuthSession().toString());
                Log.i("Token", authManager.getAuthSession().getUserPoolTokens().getValue().getIdToken());
            } catch (AuthException e) {
                Log.i("Authentication", "Login failed. User probably needs to register. Exact error: " + e.getMessage());
                try {
                    authManager.startSignUp("merzn@purdue.edu", "Password123");
                    authManager.confirmSignUp("######");
                } catch (AuthException signUpError) {
                    Log.e("Authentication", "SignUp error: " + signUpError.getMessage());
                }
            }
        }

        //----------------------------------API Testing---------------------------------------------//

        boolean testAPI = false;

        if (testAPI) {
            AuthManager authManager = new AuthManager();
            try {
                authManager.signIn("merzn@purdue.edu", "Password123");
            } catch (AuthException e) {
                e.printStackTrace();
            }
            Properties configs = new Properties();
            try {
                configs = AuthManager.loadProperties(this, "android.resource://" + getPackageName() + "/raw/auths.json");
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            Requestor requestor = new Requestor(authManager, configs.getProperty("apiKey"));
            SynchronousReceiver<SearchHistory> historyReceiver = new SynchronousReceiver<>();
            requestor.getObject("N/A", SearchHistory.class, historyReceiver, historyReceiver);
            try {
                System.out.println(historyReceiver.await());
                requestor.putObject(new ListReposition(291, 1));
                requestor.postObject(new ListDuplicate(290, "yet another list"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            /*
            List testList = new List(-1, "New List", "user filled by lambda", Instant.now().toEpochMilli());
            ListEntry entry = new ListEntry(1, 4, Math.abs(new Random().nextInt()), Instant.now().toEpochMilli(),false);
          
            SynchronousReceiver<Integer> idReceiver = new SynchronousReceiver<>();
            try {
                requestor.postObject(testList, idReceiver, idReceiver);
                System.out.println(idReceiver.await());
                requestor.postObject(entry);
            } 
            catch (Exception e) {
                Log.i("Authentication", e.toString());
                e.printStackTrace();
            }

            SynchronousReceiver<Item> itemReceiver = new SynchronousReceiver<>();
            requestor.getObject("1", Item.class, itemReceiver, itemReceiver);
            SynchronousReceiver<List> listReceiver = new SynchronousReceiver<>();
            requestor.getObject("39", List.class, listReceiver, listReceiver);
            SynchronousReceiver<Integer[]> listIdsReceiver = new SynchronousReceiver<>();
            requestor.getListOfIds(List.class, listIdsReceiver, listIdsReceiver);
            SynchronousReceiver<ItemSearch> itemSearchReceiver = new SynchronousReceiver<>();
            requestor.getObject("r", ItemSearch.class, itemSearchReceiver, itemSearchReceiver);
            try {
                System.out.println(itemReceiver.await());
                System.out.println(listReceiver.await());
                System.out.println(Arrays.toString(listIdsReceiver.await()));
                System.out.println(itemSearchReceiver.await());
            } catch (Exception receiverError) {
                receiverError.printStackTrace();
            }
            */
        }

        //------------------------------------------------------------------------------------------//

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_profile)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        ImageButton searchButton = (ImageButton) findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchResults.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_left, R.anim.exit_from_left);

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    public void onClickSignout(MenuItem m) {
        m.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                try {
                    am.signOutUser();
                    Intent intent = new Intent(MainActivity.this, com.example.listify.ui.LoginPage.class);
                    startActivity(intent);
                }
                catch (Exception e) {
                    Log.i("Authentication", e.toString());
                }
                return false;
            }
        });
    }

    @Override
    public void sendNewListName(String name) {
        Properties configs = new Properties();
        try {
            configs = AuthManager.loadProperties(this, "android.resource://" + getPackageName() + "/raw/auths.json");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        Requestor requestor = new Requestor(am, configs.getProperty("apiKey"));
        SynchronousReceiver<Integer> idReceiver = new SynchronousReceiver<>();

        List newList = new List(-1, name, "user filled by lambda", Instant.now().toEpochMilli(), -1);

        try {
            requestor.postObject(newList, idReceiver, idReceiver);
            System.out.println(idReceiver.await());
            Toast.makeText(this, String.format("%s created", name), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "An error occurred", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}