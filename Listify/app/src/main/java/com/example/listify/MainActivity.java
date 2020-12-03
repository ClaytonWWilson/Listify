package com.example.listify;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Base64OutputStream;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.amplifyframework.auth.AuthException;
import com.example.listify.data.List;
import com.example.listify.data.Picture;
import com.example.listify.ui.LoginPage;
import com.google.android.material.navigation.NavigationView;
import org.json.JSONException;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import static com.example.listify.SplashActivity.showSplash;

public class MainActivity extends AppCompatActivity implements CreateListDialogFragment.OnNewListListener {
    private AppBarConfiguration mAppBarConfiguration;
    public static AuthManager am = new AuthManager();
    public static ArrayList<String> searchHistory;
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

        if (showSplash) {
            showSplash = false;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(MainActivity.this, SplashActivity.class);
                    startActivity(intent);
                }
            }, 1);
        }

        if (am.getUserToken().equals("")) {
            am.nullify();
            Intent intent = new Intent(MainActivity.this, LoginPage.class);
            startActivity(intent);
        }

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("CHECKING", "WORKS");
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    0
            );
        }
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location != null) {
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
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

        Properties configs = new Properties();
        try {
            configs = AuthManager.loadProperties(this, "android.resource://" + getPackageName() + "/raw/auths.json");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        Requestor requestor = new Requestor(am, configs.getProperty("apiKey"));
        TextView emailView = navigationView.getHeaderView(0).findViewById(R.id.textViewEmailSidebar);
        emailView.setText(am.getEmail(requestor));
        SynchronousReceiver<Picture> profilePictureReceiver = new SynchronousReceiver<>();
        ImageView profilePictureView = navigationView.getHeaderView(0).findViewById(R.id.imageViewProfilePicture);
        try {
            requestor.getObject("profile", Picture.class, profilePictureReceiver);
            profilePictureView.setImageURI(Uri.fromFile(saveImage(profilePictureReceiver.await().getBase64EncodedImage(), "profilePicture")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        profilePictureView.setOnClickListener(new View.OnClickListener() {

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

    //From: https://stackoverflow.com/questions/30005815/convert-encoded-base64-image-to-file-object-in-android
    public File saveImage(final String imageData, String prefix) throws IOException {
        final byte[] imgBytesData = android.util.Base64.decode(imageData,
                android.util.Base64.DEFAULT);

        final File file = File.createTempFile(prefix, null, this.getCacheDir());
        final FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
                fileOutputStream);
        try {
            bufferedOutputStream.write(imgBytesData);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                bufferedOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    protected void onActivityResult (int requestCode,
                                     int resultCode,
                                     Intent data) {
        Uri selectedImage = null;
        Properties configs = new Properties();
        try {
            configs = AuthManager.loadProperties(this, "android.resource://" + getPackageName() + "/raw/auths.json");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        Requestor requestor = new Requestor(am, configs.getProperty("apiKey"));
        switch (requestCode){
            case CAMERA_CAPTURE:
                Log.i("Profile Picture", "Pulling image file at " + this.newImageFileLocation.getAbsolutePath());
                selectedImage = Uri.fromFile(this.newImageFileLocation);
                try {
                    requestor.putObject(new Picture(fileToString(this.newImageFileLocation)));
                } catch (JSONException | IOException jsonException) {
                    jsonException.printStackTrace();
                }

                break;
            case IMAGE_SELECT:
                if ((data == null) || (data.getData() == null)) {
                    return;
                }
                selectedImage = data.getData();
                try {
                    requestor.putObject(new Picture(fileToString(new File(getRealPathFromUri(this, selectedImage)))));
                } catch (JSONException | IOException exception) {
                    exception.printStackTrace();
                }
                break;
        }

        MainActivity.super.onActivityResult(requestCode, resultCode, data);
        if (selectedImage == null) {
            return;
        }
        NavigationView navigationView = findViewById(R.id.nav_view);

        ImageView profilePicture = navigationView.getHeaderView(0).findViewById(R.id.imageViewProfilePicture);
        profilePicture.setImageURI(selectedImage);

    }

    //From:  https://stackoverflow.com/questions/20028319/how-to-convert-content-media-external-images-media-y-to-file-storage-sdc
    private static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    //From: https://stackoverflow.com/questions/27784230/convert-a-file-100mo-in-base64-on-android
    private String fileToString(File toStringify) throws IOException {
        InputStream inputStream = new FileInputStream(toStringify.getAbsolutePath());
        byte[] buffer = new byte[8192];
        int bytesRead;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Base64OutputStream output64 = new Base64OutputStream(output, Base64.DEFAULT);
        try {
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output64.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        output64.close();

        return output.toString();
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