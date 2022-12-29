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

public class UpdateAppStaticUnavailableDialog extends Dialog {
    private AppCompatTextView updateAppStaticUnavailableTitleText;
    private LottieAnimationView updateAppStaticUnavailableLottie;
    private AppCompatTextView updateAppStaticUnavailableText;
    private LinearLayout updateAppStaticUnavailableButtonsLinearLayout;
    private AppCompatButton updateAppStaticUnavailableContinue;

    private void initialise() {
        updateAppStaticUnavailableTitleText = findViewById(R.id.update_app_static_unavailable_dialog_title_text);
        updateAppStaticUnavailableLottie = findViewById(R.id.update_app_static_unavailable_dialog_lottie);
        updateAppStaticUnavailableText = findViewById(R.id.update_app_static_unavailable_dialog_text);
        updateAppStaticUnavailableButtonsLinearLayout = findViewById(R.id.update_app_static_unavailable_dialog_buttons_linear_layout);
        updateAppStaticUnavailableContinue = findViewById(R.id.update_app_static_unavailable_dialog_continue);
    }

    private void setVisibilityOfViews(int visibility) {
        updateAppStaticUnavailableTitleText.setVisibility(visibility);
        updateAppStaticUnavailableLottie.setVisibility(visibility);
        updateAppStaticUnavailableText.setVisibility(visibility);
        updateAppStaticUnavailableButtonsLinearLayout.setVisibility(visibility);
    }

    public UpdateAppStaticUnavailableDialog(@NonNull Context context) {
        super(context, R.style.CustomDialogTheme);
        setContentView(R.layout.dialog_update_app_static_unavailable);

        initialise();

        updateAppStaticUnavailableContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // First, the views will disappear, then the dialog box will close
                setVisibilityOfViews(View.INVISIBLE);
                new CountDownTimer(100, 100) {
                    @Override
                    public void onTick(long millisUntilFinished) {}
                    @Override
                    public void onFinish() {
                        dismiss();
                    }
                }.start();
            }
        });
    }

    @Override
    public void show() {
        // Set the dialog to not focusable.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

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
}
