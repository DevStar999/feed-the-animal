package com.nerdcoredevelopment.inappbillingdemo.fragment;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import com.nerdcoredevelopment.inappbillingdemo.BuildConfig;
import com.nerdcoredevelopment.inappbillingdemo.R;
import com.nerdcoredevelopment.inappbillingdemo.RateUsDialogStages;
import com.nerdcoredevelopment.inappbillingdemo.dialogs.RateUsPromptDialog;

public class SettingsFragment extends Fragment {
    private final static String FACEBOOK_URL = "http://www.facebook.com/Nerdcore-Development-109351035183956";
    private final static String FACEBOOK_PAGE_ID = "Nerdcore-Development-109351035183956";
    private final static String INSTAGRAM_USERNAME = "nerdcoredev";
    private final static String TWITTER_USERNAME = "NerdcoreDev";
    private final static String DEVELOPER_MAIL_ID = "nerdcoredevelopment@gmail.com";
    private final static String FEEDBACK_MAIL_SUBJECT = "Feedback - Feed the Animal";
    private final static String FEEDBACK_MAIL_BODY = "Hi Nerdcore Team,\n\n";
    private Context context;
    private OnSettingsFragmentInteractionListener mListener;
    private SharedPreferences sharedPreferences;
    private AppCompatImageView backButton;
    private LinearLayout facebookLinearLayout;
    private LinearLayout instagramLinearLayout;
    private LinearLayout twitterLinearLayout;
    private LinearLayout checkUpdatesLayout;
    private LinearLayout checkUpdatesPopUpLayout;
    private LinearLayout rateUsLinearLayout;
    private LinearLayout rateUsInAppLinearLayout;
    private LinearLayout feedbackLinearLayout;
    private LinearLayout privacyLinearLayout;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void settingOnClickListeners() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onSettingsFragmentInteractionBackClicked();
                }
            }
        });
        facebookLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uriFacebook = "";
                PackageManager packageManager = context.getPackageManager();
                try {
                    ApplicationInfo applicationInfo = packageManager.getApplicationInfo("com.facebook.katana", 0);

                    if (applicationInfo.enabled) {
                        int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
                        if (versionCode >= 3002850) { // newer versions of fb app
                            uriFacebook = "fb://facewebmodal/f?href=" + FACEBOOK_URL;
                        } else { // older versions of fb app
                            uriFacebook = "fb://page/" + FACEBOOK_PAGE_ID;
                        }
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uriFacebook)));
                    }
                } catch (Exception e) {
                    uriFacebook = FACEBOOK_URL; // normal web url
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uriFacebook)));
                }
            }
        });
        instagramLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("http://instagram.com/_u/" + INSTAGRAM_USERNAME);

                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setPackage("com.instagram.android");

                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://instagram.com/" + INSTAGRAM_USERNAME)));
                }
            }
        });
        twitterLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("twitter://user?screen_name=" + TWITTER_USERNAME);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setPackage("com.twitter.android");

                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://twitter.com/#!/" + TWITTER_USERNAME)));
                }
            }
        });
        checkUpdatesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onSettingsFragmentInteractionCheckUpdatesStaticClicked();
                }
            }
        });
        checkUpdatesPopUpLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onSettingsFragmentInteractionCheckUpdatesPopUpClicked();
                }
            }
        });
        rateUsLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RateUsPromptDialog rateUsPromptDialog = new RateUsPromptDialog(context, RateUsDialogStages.STATIC_DIALOG);
                rateUsPromptDialog.show();
                rateUsPromptDialog.setRateUsPromptDialogListener(new RateUsPromptDialog.RateUsPromptDialogListener() {
                    @Override
                    public void getResponseOfRateUsDialog(boolean response, RateUsDialogStages rateUsDialogStage,
                                                          boolean didUserRespond) {
                        if (response) {
                            if (!sharedPreferences.getBoolean("hasUserClickedRateUsAtleastOnce", false)) {
                                sharedPreferences.edit().putBoolean("hasUserClickedRateUsAtleastOnce", true).apply();
                                if (mListener != null) {
                                    mListener.onSettingsFragmentInteractionRateUsInAppClicked();
                                    return;
                                }
                            }

                            Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                            String packageName = "com.nerdcoredevelopment.inappbillingdemo";
                            Uri uriForApp = Uri.parse("market://details?id=" + packageName);
                            Uri uriForBrowser = Uri.parse("http://play.google.com/store/apps/details?id="
                                    + packageName);

                            try {
                                browserIntent.setData(uriForApp);
                                startActivity(browserIntent);
                            } catch (ActivityNotFoundException exception) {
                                browserIntent.setData(uriForBrowser);
                                startActivity(browserIntent);
                            }
                        }
                    }
                });
            }
        });
        rateUsInAppLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RateUsDialogStages rateUsDialogStage;
                if (sharedPreferences.getInt("rateUsPopUpDialogStage", RateUsDialogStages.POP_UP_DIALOG_STAGE_1.ordinal())
                        == RateUsDialogStages.POP_UP_DIALOG_STAGE_1.ordinal()) {
                    rateUsDialogStage = RateUsDialogStages.POP_UP_DIALOG_STAGE_1;
                } else if (sharedPreferences.getInt("rateUsPopUpDialogStage", RateUsDialogStages.POP_UP_DIALOG_STAGE_1.ordinal())
                        == RateUsDialogStages.POP_UP_DIALOG_STAGE_2.ordinal()) {
                    rateUsDialogStage = RateUsDialogStages.POP_UP_DIALOG_STAGE_2;
                } else { // Means we are at Stage 3
                    Toast.makeText(context, "You informed us that you already submitted the review", Toast.LENGTH_LONG).show();
                    return;
                }
                RateUsPromptDialog rateUsPromptDialog = new RateUsPromptDialog(context, rateUsDialogStage);
                rateUsPromptDialog.show();
                rateUsPromptDialog.setRateUsPromptDialogListener(new RateUsPromptDialog.RateUsPromptDialogListener() {
                    @Override
                    public void getResponseOfRateUsDialog(boolean response, RateUsDialogStages rateUsDialogStage,
                                                          boolean didUserRespond) {
                        if (response) {
                            if (rateUsDialogStage == RateUsDialogStages.POP_UP_DIALOG_STAGE_1) {
                                sharedPreferences.edit().putInt("rateUsPopUpDialogStage",
                                        RateUsDialogStages.POP_UP_DIALOG_STAGE_2.ordinal()).apply();
                            } else if (rateUsDialogStage == RateUsDialogStages.POP_UP_DIALOG_STAGE_2) {
                                // User clicked on 'RATE US' at Stage 2, so we do nothing
                            }

                            if (rateUsDialogStage == RateUsDialogStages.POP_UP_DIALOG_STAGE_1) {
                                if (!sharedPreferences.getBoolean("hasUserClickedRateUsAtleastOnce", false)) {
                                    sharedPreferences.edit().putBoolean("hasUserClickedRateUsAtleastOnce", true).apply();
                                    if (mListener != null) {
                                        mListener.onSettingsFragmentInteractionRateUsInAppClicked();
                                        return;
                                    }
                                }
                            }

                            Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                            String packageName = "com.nerdcoredevelopment.inappbillingdemo";
                            Uri uriForApp = Uri.parse("market://details?id=" + packageName);
                            Uri uriForBrowser = Uri.parse("http://play.google.com/store/apps/details?id="
                                    + packageName);

                            try {
                                browserIntent.setData(uriForApp);
                                startActivity(browserIntent);
                            } catch (ActivityNotFoundException exception) {
                                browserIntent.setData(uriForBrowser);
                                startActivity(browserIntent);
                            }
                        } else {
                            if (rateUsDialogStage == RateUsDialogStages.POP_UP_DIALOG_STAGE_1) {
                                // User clicked on 'REMIND LATER', so we do nothing
                            } else if (rateUsDialogStage == RateUsDialogStages.POP_UP_DIALOG_STAGE_2) {
                                if (didUserRespond) {
                                    sharedPreferences.edit().putInt("rateUsPopUpDialogStage",
                                            RateUsDialogStages.POP_UP_DIALOG_STAGE_3.ordinal()).apply();
                                }
                            }
                        }
                    }
                });
            }
        });
        feedbackLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String versionName = BuildConfig.VERSION_NAME;

                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{DEVELOPER_MAIL_ID});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, FEEDBACK_MAIL_SUBJECT + " v" + versionName);
                emailIntent.putExtra(Intent.EXTRA_TEXT, FEEDBACK_MAIL_BODY);
                emailIntent.setSelector(new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:")));

                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        });
        privacyLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://nerdcoredevelopment.com/privacy-policy.html"));
                startActivity(browserIntent);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        requireActivity().getWindow().getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        sharedPreferences = context.getSharedPreferences("com.nerdcoredevelopment.inappbillingdemo", Context.MODE_PRIVATE);

        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        backButton = view.findViewById(R.id.title_back_settings_fragment_button);
        facebookLinearLayout = view.findViewById(R.id.facebook_linear_layout);
        instagramLinearLayout = view.findViewById(R.id.instagram_linear_layout);
        twitterLinearLayout = view.findViewById(R.id.twitter_linear_layout);
        checkUpdatesLayout = view.findViewById(R.id.check_updates_linear_layout);
        checkUpdatesPopUpLayout = view.findViewById(R.id.check_updates_pop_up_linear_layout);
        rateUsLinearLayout = view.findViewById(R.id.rate_us_linear_layout);
        rateUsInAppLinearLayout = view.findViewById(R.id.rate_us_in_app_linear_layout);
        feedbackLinearLayout = view.findViewById(R.id.feedback_linear_layout);
        privacyLinearLayout = view.findViewById(R.id.privacy_policy_linear_layout);

        settingOnClickListeners();

        return view;
    }

    public interface OnSettingsFragmentInteractionListener {
        void onSettingsFragmentInteractionBackClicked();
        void onSettingsFragmentInteractionRateUsInAppClicked();
        void onSettingsFragmentInteractionCheckUpdatesStaticClicked();
        void onSettingsFragmentInteractionCheckUpdatesPopUpClicked();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSettingsFragmentInteractionListener) {
            mListener = (OnSettingsFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSettingsFragmentInteractionListener");
        }
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
