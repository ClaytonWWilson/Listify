package com.example.listify;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.amplifyframework.auth.AuthException;
import com.example.listify.data.List;
import com.example.listify.ui.LoginPage;
import com.google.android.material.navigation.NavigationView;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Properties;

import static com.example.listify.SplashActivity.showSplash;

public class MainActivity extends AppCompatActivity implements CreateListDialogFragment.OnNewListListener {
    private AppBarConfiguration mAppBarConfiguration;
    public static AuthManager am = new AuthManager();
    private File newImageFileLocation = null;
    private final int CAMERA_CAPTURE = 1;
    private final int IMAGE_SELECT = 2;

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

            /*Requestor requestor = new Requestor(authManager, configs.getProperty("apiKey"));
            SynchronousReceiver<SearchHistory> historyReceiver = new SynchronousReceiver<>();
            requestor.getObject("N/A", SearchHistory.class, historyReceiver, historyReceiver);
            try {
                requestor.putObject(new List(293, "Java.py", "me!", 1));
                System.out.println(historyReceiver.await());
                requestor.putObject(new ListReposition(291, 1));
                requestor.postObject(new ListDuplicate(290, "yet another list"));
            } catch (Exception e) {
                e.printStackTrace();
            }

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

        TextView emailView = navigationView.getHeaderView(0).findViewById(R.id.textViewEmailSidebar);
        emailView.setText(am.getEmail());

        ImageView profilePicture = navigationView.getHeaderView(0).findViewById(R.id.imageViewProfilePicture);
        profilePicture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                builder.setView(navigationView.getHeaderView(0));
                builder.setTitle("Change picture");
                builder.setMessage("Please select a method to add a new profile picture.");
                builder.setCancelable(true);
                builder.setPositiveButton("Take picture", (dialog, which) -> {
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File imageFileLocation = getOutputImageFile();
                    Log.i("Profile Picture", "New image file at " + imageFileLocation.getAbsolutePath());
                    newImageFileLocation = imageFileLocation;
                    Uri imageUri = FileProvider.getUriForFile(
                            MainActivity.this,
                            BuildConfig.APPLICATION_ID + ".provider",
                            imageFileLocation);
                    takePicture.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(takePicture, CAMERA_CAPTURE);
                });
                builder.setNegativeButton("Select picture", (dialog, which) -> {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, IMAGE_SELECT);
                });
                builder.setNeutralButton("Cancel", (dialog, which) -> {
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_profile, R.id.nav_logout)
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

    protected void onActivityResult (int requestCode,
                                     int resultCode,
                                     Intent data) {
        Uri selectedImage = null;
        switch (requestCode){
            case CAMERA_CAPTURE:
                Log.i("Profile Picture", "Pulling image file at " + this.newImageFileLocation.getAbsolutePath());
                selectedImage = Uri.fromFile(this.newImageFileLocation);
                break;
            case IMAGE_SELECT:
                if ((data == null) || (data.getData() == null)) {
                    return;
                }
                selectedImage = data.getData();
                break;
        }

        MainActivity.super.onActivityResult(requestCode, resultCode, data);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ImageView profilePicture = navigationView.getHeaderView(0).findViewById(R.id.imageViewProfilePicture);
        profilePicture.setImageURI(selectedImage);
    }

    //getOutputImageFile from https://developer.android.com/guide/topics/media/camera
    private static File getOutputImageFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("File creation", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        Log.i("File creation", mediaFile.toString());
        return mediaFile;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    public void onClickSignout(MenuItem m) {
        try {
            am.signOutUser();
            Intent intent = new Intent(MainActivity.this, com.example.listify.ui.LoginPage.class);
            startActivity(intent);
        }
        catch (Exception e) {
            Log.i("Authentication", e.toString());
        }
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