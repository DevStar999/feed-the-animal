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

public class RateUsPromptDialog extends Dialog {
    private AppCompatTextView rateUsTitleText;
    private LottieAnimationView rateUsLottie;
    private AppCompatTextView rateUsText;
    private LinearLayout rateUsButtonsLinearLayout;
    private AppCompatButton rateUsDismissOption;
    private AppCompatButton rateUsRateUs;
    private RateUsPromptDialogListener rateUsPromptDialogListener;
    private RateUsDialogStages rateUsDialogStage;

    private void initialise(RateUsDialogStages rateUsDialogStage) {
        rateUsTitleText = findViewById(R.id.rate_us_dialog_title_text);
        rateUsLottie = findViewById(R.id.rate_us_dialog_lottie);
        rateUsText = findViewById(R.id.rate_us_dialog_text);
        rateUsButtonsLinearLayout = findViewById(R.id.rate_us_dialog_buttons_linear_layout);
        rateUsDismissOption = findViewById(R.id.rate_us_dialog_dismiss_option);
        if (rateUsDialogStage == RateUsDialogStages.STATIC_DIALOG) {
            rateUsDismissOption.setText("NO, THANKS");
        } else if (rateUsDialogStage == RateUsDialogStages.POP_UP_DIALOG_STAGE_1) {
            rateUsDismissOption.setText("REMIND LATER");
        } else if (rateUsDialogStage == RateUsDialogStages.POP_UP_DIALOG_STAGE_2) {
            rateUsDismissOption.setText("ALREADY DONE");
        }
        rateUsRateUs = findViewById(R.id.rate_us_dialog_rate_us);
        this.rateUsDialogStage = rateUsDialogStage;
    }

    private void setVisibilityOfViews(int visibility) {
        rateUsTitleText.setVisibility(visibility);
        rateUsLottie.setVisibility(visibility);
        rateUsText.setVisibility(visibility);
        rateUsButtonsLinearLayout.setVisibility(visibility);
    }

    public RateUsPromptDialog(@NonNull Context context, RateUsDialogStages rateUsDialogStage) {
        super(context, R.style.CustomDialogTheme);
        setContentView(R.layout.dialog_rate_us_prompt);

        initialise(rateUsDialogStage);

        rateUsDismissOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // First, the views will disappear, then the dialog box will close
                setVisibilityOfViews(View.INVISIBLE);
                new CountDownTimer(100, 100) {
                    @Override
                    public void onTick(long millisUntilFinished) {}
                    @Override
                    public void onFinish() {
                        rateUsPromptDialogListener.getResponseOfRateUsDialog(false, rateUsDialogStage,
                                true);
                        dismiss();
                    }
                }.start();
            }
        });
        rateUsRateUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // First, the views will disappear, then the dialog box will close
                setVisibilityOfViews(View.INVISIBLE);
                new CountDownTimer(100, 100) {
                    @Override
                    public void onTick(long millisUntilFinished) {}
                    @Override
                    public void onFinish() {
                        rateUsPromptDialogListener.getResponseOfRateUsDialog(true, rateUsDialogStage,
                                true);
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

    @Override
    public void dismiss() {
        super.dismiss();
        rateUsPromptDialogListener.getResponseOfRateUsDialog(false, rateUsDialogStage, false);
    }

    public void setRateUsPromptDialogListener(RateUsPromptDialogListener rateUsPromptDialogListener) {
        this.rateUsPromptDialogListener = rateUsPromptDialogListener;
    }

    public interface RateUsPromptDialogListener {
        void getResponseOfRateUsDialog(boolean response, RateUsDialogStages rateUsDialogStage, boolean didUserRespond);
    }
}
