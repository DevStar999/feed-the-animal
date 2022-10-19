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

public class GameExitDialog extends Dialog {
    private LottieAnimationView gameExitLottie;
    private AppCompatTextView gameExitText;
    private LinearLayout gameExitButtonsLinearLayout;
    private AppCompatButton gameExitExit;
    private AppCompatButton gameExitCancel;
    private GameExitDialogListener gameExitDialogListener;

    private void initialise() {
        gameExitLottie = findViewById(R.id.game_exit_lottie);
        gameExitText = findViewById(R.id.game_exit_text);
        gameExitButtonsLinearLayout = findViewById(R.id.game_exit_buttons_linear_layout);
        gameExitExit = findViewById(R.id.game_exit_exit);
        gameExitCancel = findViewById(R.id.game_exit_cancel);
    }

    private void setVisibilityOfViews(int visibility) {
        gameExitLottie.setVisibility(visibility);
        gameExitText.setVisibility(visibility);
        gameExitButtonsLinearLayout.setVisibility(visibility);
    }

    public GameExitDialog(@NonNull Context context) {
        super(context, R.style.CustomDialogTheme);
        setContentView(R.layout.dialog_game_exit);

        initialise();

        gameExitExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // First, the views will disappear, then the dialog box will close
                setVisibilityOfViews(View.INVISIBLE);
                new CountDownTimer(100, 100) {
                    @Override
                    public void onTick(long millisUntilFinished) {}
                    @Override
                    public void onFinish() {
                        gameExitDialogListener.getResponseOfExitDialog(true);
                        dismiss();
                    }
                }.start();
            }
        });
        gameExitCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // First, the views will disappear, then the dialog box will close
                setVisibilityOfViews(View.INVISIBLE);
                new CountDownTimer(100, 100) {
                    @Override
                    public void onTick(long millisUntilFinished) {}
                    @Override
                    public void onFinish() {
                        gameExitDialogListener.getResponseOfExitDialog(false);
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

    public void setGameExitDialogListener(GameExitDialogListener gameExitDialogListener) {
        this.gameExitDialogListener = gameExitDialogListener;
    }

    public interface GameExitDialogListener {
        void getResponseOfExitDialog(boolean response);
    }
}
