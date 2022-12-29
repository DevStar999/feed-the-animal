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
import com.nerdcoredevelopment.inappbillingdemo.UpdateAppPopUpDialogOptions;

public class UpdateAppPopUpDialog extends Dialog {
    private AppCompatTextView updateAppPopUpTitleText;
    private LottieAnimationView updateAppPopUpLottie;
    private LinearLayout updateAppPopUpVersionLinearLayout;
    private AppCompatTextView updateVersionStartTextView;
    private AppCompatTextView updateVersionEndTextView;
    private AppCompatTextView updateAppPopUpText;
    private LinearLayout updateAppPopUpButtonsLinearLayout;
    private AppCompatButton updateAppPopUpRemindLater;
    private AppCompatButton updateAppPopUpSkipVersion;
    private AppCompatButton updateAppPopUpUpdateNow;
    private UpdateAppPopUpDialog.UpdateAppPopUpDialogListener updateAppPopUpDialogListener;

    private void initialise() {
        updateAppPopUpTitleText = findViewById(R.id.update_app_pop_up_dialog_title_text);
        updateAppPopUpLottie = findViewById(R.id.update_app_pop_up_dialog_lottie);
        updateAppPopUpVersionLinearLayout = findViewById(R.id.update_app_pop_up_dialog_versions_linear_layout);
        updateVersionStartTextView = findViewById(R.id.update_app_pop_up_dialog_versions_start);
        updateVersionEndTextView = findViewById(R.id.update_app_pop_up_dialog_versions_end);
        updateAppPopUpText = findViewById(R.id.update_app_pop_up_dialog_text);
        updateAppPopUpButtonsLinearLayout = findViewById(R.id.update_app_pop_up_dialog_buttons_linear_layout);
        updateAppPopUpRemindLater = findViewById(R.id.update_app_pop_up_dialog_remind_later);
        updateAppPopUpSkipVersion = findViewById(R.id.update_app_pop_up_dialog_skip_version);
        updateAppPopUpUpdateNow = findViewById(R.id.update_app_pop_up_dialog_update_now);
    }

    private void setVisibilityOfViews(int visibility) {
        updateAppPopUpTitleText.setVisibility(visibility);
        updateAppPopUpLottie.setVisibility(visibility);
        updateAppPopUpVersionLinearLayout.setVisibility(visibility);
        updateAppPopUpText.setVisibility(visibility);
        updateAppPopUpButtonsLinearLayout.setVisibility(visibility);
    }

    public UpdateAppPopUpDialog(@NonNull Context context, String oldVersion, String newVersion) {
        super(context, R.style.CustomDialogTheme);
        setContentView(R.layout.dialog_update_app_pop_up);

        initialise();
        updateVersionStartTextView.setText("Update Version: " + oldVersion);
        updateVersionEndTextView.setText(newVersion);

        updateAppPopUpRemindLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // First, the views will disappear, then the dialog box will close
                setVisibilityOfViews(View.INVISIBLE);
                new CountDownTimer(100, 100) {
                    @Override
                    public void onTick(long millisUntilFinished) {}
                    @Override
                    public void onFinish() {
                        updateAppPopUpDialogListener.getResponseOfUpdateAppPopUpDialogDialog
                                (UpdateAppPopUpDialogOptions.REMIND_LATER);
                        dismiss();
                    }
                }.start();
            }
        });
        updateAppPopUpSkipVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // First, the views will disappear, then the dialog box will close
                setVisibilityOfViews(View.INVISIBLE);
                new CountDownTimer(100, 100) {
                    @Override
                    public void onTick(long millisUntilFinished) {}
                    @Override
                    public void onFinish() {
                        updateAppPopUpDialogListener.getResponseOfUpdateAppPopUpDialogDialog
                                (UpdateAppPopUpDialogOptions.SKIP_VERSION);
                        dismiss();
                    }
                }.start();
            }
        });
        updateAppPopUpUpdateNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // First, the views will disappear, then the dialog box will close
                setVisibilityOfViews(View.INVISIBLE);
                new CountDownTimer(100, 100) {
                    @Override
                    public void onTick(long millisUntilFinished) {}
                    @Override
                    public void onFinish() {
                        updateAppPopUpDialogListener.getResponseOfUpdateAppPopUpDialogDialog
                                (UpdateAppPopUpDialogOptions.UPDATE_NOW);
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
        updateAppPopUpDialogListener.getResponseOfUpdateAppPopUpDialogDialog
                (UpdateAppPopUpDialogOptions.REMIND_LATER);
    }

    public void setUpdateAppPopUpDialogListener(UpdateAppPopUpDialog.UpdateAppPopUpDialogListener updateAppPopUpDialogListener) {
        this.updateAppPopUpDialogListener = updateAppPopUpDialogListener;
    }

    public interface UpdateAppPopUpDialogListener {
        void getResponseOfUpdateAppPopUpDialogDialog(UpdateAppPopUpDialogOptions response);
    }
}
