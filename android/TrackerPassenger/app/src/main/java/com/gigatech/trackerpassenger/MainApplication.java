package com.gigatech.trackerpassenger;

import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

public class MainApplication extends Application {

    private static final String TAG = "Application";
    private static MainApplication application;
    private static Gson gson;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }

    @Override
    public void onTerminate() {
        ApiService.destroy();
        Account.destroy();
        TrackService.destroy();
        super.onTerminate();
    }

    public static Context getContext() {
        return application.getApplicationContext();
    }

    public static Gson getGson() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }

    public static Dialog getDialog(Context context, String message) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog);

        TextView tv = (TextView) dialog.findViewById(R.id.message);
        tv.setText(message);

        dialog.findViewById(R.id.btn_container).setVisibility(View.GONE);
        return dialog;
    }

    public static Dialog getDialogWithPositiveButton(Context context, String message, final View.OnClickListener l) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog);

        dialog.findViewById(R.id.progress_bar).setVisibility(View.GONE);

        TextView tv = (TextView) dialog.findViewById(R.id.message);
        tv.setText(message);

        dialog.findViewById(R.id.btn_negative).setVisibility(View.GONE);
        Button btn = (Button) dialog.findViewById(R.id.btn_positive);
        btn.setText(R.string.ok);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (l != null) l.onClick(view);
            }
        });
        return dialog;
    }

    public static Dialog getDialogWithYesNoButtons(Context context, String message, final View.OnClickListener l_positive, final View.OnClickListener l_negative) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog);

        dialog.findViewById(R.id.progress_bar).setVisibility(View.GONE);

        TextView tv = (TextView) dialog.findViewById(R.id.message);
        tv.setText(message);

        dialog.findViewById(R.id.btn_positive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (l_positive != null) l_positive.onClick(view);
            }
        });

        dialog.findViewById(R.id.btn_negative).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (l_negative != null) l_negative.onClick(view);
            }
        });
        return dialog;
    }
}
