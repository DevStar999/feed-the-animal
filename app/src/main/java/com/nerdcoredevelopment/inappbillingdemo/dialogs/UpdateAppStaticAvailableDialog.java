package com.nerdcoredevelopment.inappbillingdemo.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import com.airbnb.lottie.LottieAnimationView;
import com.nerdcoredevelopment.inappbillingdemo.R;

public class UpdateAppStaticAvailableDialog extends Dialog {
    private AppCompatTextView updateAppStaticAvailableTitleText;
    private LottieAnimationView updateAppStaticAvailableLottie;
    private LinearLayout updateAppStaticAvailableVersionLinearLayout;
    private AppCompatTextView updateVersionStartTextView;
    private AppCompatTextView updateVersionEndTextView;
    private AppCompatTextView updateAppStaticAvailableText;
    private LinearLayout updateAppStaticAvailableButtonsLinearLayout;
    private AppCompatButton updateAppStaticAvailableNoThanks;
    private AppCompatButton updateAppStaticAvailableUpdateNow;
    private UpdateAppStaticAvailableDialogListener updateAppStaticAvailableDialogListener;

    private void initialise() {
        updateAppStaticAvailableTitleText = findViewById(R.id.update_app_static_available_dialog_title_text);
        updateAppStaticAvailableLottie = findViewById(R.id.update_app_static_available_dialog_lottie);
        updateAppStaticAvailableVersionLinearLayout = findViewById(R.id.update_app_static_available_dialog_versions_linear_layout);
        updateVersionStartTextView = findViewById(R.id.update_app_static_available_dialog_versions_start);
        updateVersionEndTextView = findViewById(R.id.update_app_static_available_dialog_versions_end);
        updateAppStaticAvailableText = findViewById(R.id.update_app_static_available_dialog_text);
        updateAppStaticAvailableButtonsLinearLayout = findViewById(R.id.update_app_static_available_dialog_buttons_linear_layout);
        updateAppStaticAvailableNoThanks = findViewById(R.id.update_app_static_available_dialog_no_thanks);
        updateAppStaticAvailableUpdateNow = findViewById(R.id.update_app_static_available_dialog_update_now);
    }

    private void setVisibilityOfViews(int visibility) {
        updateAppStaticAvailableTitleText.setVisibility(visibility);
        updateAppStaticAvailableLottie.setVisibility(visibility);
        updateAppStaticAvailableVersionLinearLayout.setVisibility(visibility);
        updateAppStaticAvailableText.setVisibility(visibility);
        updateAppStaticAvailableButtonsLinearLayout.setVisibility(visibility);
    }

    public UpdateAppStaticAvailableDialog(@NonNull Context context, String oldVersion, String newVersion) {
        super(context, R.style.CustomDialogTheme);
        setContentView(R.layout.dialog_update_app_static_available);

        initialise();
        updateVersionStartTextView.setText("Update Version: " + oldVersion);
        updateVersionEndTextView.setText(newVersion);

        updateAppStaticAvailableNoThanks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // First, the views will disappear, then the dialog box will close
                setVisibilityOfViews(View.INVISIBLE);
                new CountDownTimer(100, 100) {
                    @Override
                    public void onTick(long millisUntilFinished) {}
                    @Override
                    public void onFinish() {
                        updateAppStaticAvailableDialogListener.getResponseOfUpdateAppStaticAvailableDialogDialog(false);
                        dismiss();
                    }
                }.start();
            }
        });
        updateAppStaticAvailableUpdateNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // First, the views will disappear, then the dialog box will close
                setVisibilityOfViews(View.INVISIBLE);
                new CountDownTimer(100, 100) {
                    @Override
                    public void onTick(long millisUntilFinished) {}
                    @Override
                    public void onFinish() {
                        updateAppStaticAvailableDialogListener.getResponseOfUpdateAppStaticAvailableDialogDialog(true);
                        dismiss();
                    }
                }.start();
            }
        });
    }

    @Override
    public void show() {
        // Set the dialog to not focusable.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        // Show the dialog!
        super.show();

        // Set the dialog to immersive sticky mode
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        // Clear the not focusable flag from the window
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        // First, the dialog box will open, then the views will show
        new CountDownTimer(400, 400) {
            @Override
            public void onTick(long millisUntilFinished) {}
            @Override
            public void onFinish() {
                setVisibilityOfViews(View.VISIBLE);
            }
        }.start();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        updateAppStaticAvailableDialogListener.getResponseOfUpdateAppStaticAvailableDialogDialog(false);
    }

    public void setUpdateAppStaticAvailableDialogListener
            (UpdateAppStaticAvailableDialogListener updateAppStaticAvailableDialogListener) {
        this.updateAppStaticAvailableDialogListener = updateAppStaticAvailableDialogListener;
    }

    public interface UpdateAppStaticAvailableDialogListener {
        void getResponseOfUpdateAppStaticAvailableDialogDialog(boolean response);
    }
}
