package com.example.listify;

import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.listify.data.ListShare;
import com.example.listify.data.Picture;
import org.json.JSONException;

import java.io.*;
import java.util.Properties;

import static com.example.listify.MainActivity.am;

public class ConfirmShareView extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState, @Nullable @org.jetbrains.annotations.Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.share_confirmation);
        TextView shareeEmailView = findViewById(R.id.shareeEmailView);
        final String shareeEmail = (String) getIntent().getSerializableExtra("shareeEmail");
        final String shareeID = (String) getIntent().getSerializableExtra("shareeID");
        final Integer listID = (Integer) getIntent().getSerializableExtra("listID");

        shareeEmailView.setText(shareeEmail);
        Properties configs = new Properties();
        try {
            configs = AuthManager.loadProperties(this, "android.resource://" + getPackageName() + "/raw/auths.json");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        Requestor requestor = new Requestor(am, configs.getProperty("apiKey"));
        SynchronousReceiver<Picture> profilePictureReceiver = new SynchronousReceiver<>();
        ImageView profilePictureView = findViewById(R.id.otherProfilePicture);
        try {
            requestor.getObject("profile", Picture.class, profilePictureReceiver);
            profilePictureView.setImageURI(Uri.fromFile(saveImage(profilePictureReceiver.await().getBase64EncodedImage(), "shareeProfilePicture")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Button confirmShare = (Button) findViewById(R.id.confirmShare);
        Button cancelShare = (Button) findViewById(R.id.cancelShare);

        confirmShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ListShare listShare = new ListShare(listID, shareeEmail, "Read, Write, Delete, Share", null);
                try {
                    requestor.putObject(listShare);
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }
        });

        cancelShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: return to prior view
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
}
