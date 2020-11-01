package com.example.listify;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.view.WindowManager;

public class LoadingCircleDialog {
    Dialog loadingDialog;

    public LoadingCircleDialog(Context context) {
        loadingDialog = new Dialog(context);

        // Create and show a loading dialog
        loadingDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        loadingDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // layout to display
        loadingDialog.setContentView(R.layout.dialog_loading);

        // set color transpartent
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);
    }

    public void show() {
        loadingDialog.show();
    }

    public void cancel() {
        loadingDialog.cancel();
    }
}
