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

public class ErrorOccurredDialog extends Dialog {
    private String errorText;
    private LottieAnimationView errorOccurredLottie;
    private AppCompatTextView errorOccurredText;
    private LinearLayout errorOccurredButtonsLinearLayout;
    private AppCompatButton errorOccurredContinue;

    private void initialise(String errorText) {
        this.errorText = errorText;
        errorOccurredLottie = findViewById(R.id.error_occurred_lottie);
        errorOccurredText = findViewById(R.id.error_occurred_text);
        errorOccurredText.setText(errorText);
        errorOccurredButtonsLinearLayout = findViewById(R.id.error_occurred_buttons_linear_layout);
        errorOccurredContinue = findViewById(R.id.error_occurred_continue);
    }

    private void setVisibilityOfViews(int visibility) {
        errorOccurredLottie.setVisibility(visibility);
        if (errorText.equals("")) {
            errorOccurredText.setVisibility(View.GONE);
        } else {
            errorOccurredText.setVisibility(visibility);
        }
        errorOccurredButtonsLinearLayout.setVisibility(visibility);
    }

    public ErrorOccurredDialog(@NonNull Context context, String errorText) {
        super(context, R.style.CustomDialogTheme);
        setContentView(R.layout.dialog_error_occurred);

        initialise(errorText);

        errorOccurredContinue.setOnClickListener(new View.OnClickListener() {
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
}

