package com.nerdcoredevelopment.inappbillingdemo.fragment;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.nerdcoredevelopment.inappbillingdemo.AnimalImageSpecs;
import com.nerdcoredevelopment.inappbillingdemo.R;
import com.qonversion.android.sdk.Qonversion;
import com.qonversion.android.sdk.QonversionError;
import com.qonversion.android.sdk.QonversionPermissionsCallback;
import com.qonversion.android.sdk.dto.QPermission;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FeedingFragment extends Fragment {
    private Context context;
    private OnFeedingFragmentInteractionListener mListener;
    private SharedPreferences sharedPreferences;

    /* Views related to this fragment */
    private AppCompatImageView backButton;
    private AppCompatTextView feedAnimalTextView;
    private AppCompatImageView animalImageView;
    private AppCompatTextView stockLeftTextView;
    private ConstraintLayout postFeedingMessageLeft;
    private ConstraintLayout postFeedingMessageRight;
    private ConstraintLayout animalCowConstraintLayout;
    private ConstraintLayout animalRabbitConstraintLayout;
    private ConstraintLayout animalGoatConstraintLayout;
    private ConstraintLayout animalHorseConstraintLayout;
    private ConstraintLayout animalReindeerConstraintLayout;
    private ConstraintLayout animalZebraConstraintLayout;
    private LinearLayout rewardedAdLinearLayout;

    /* Variables related to this fragment */
    private int consumptionRate;
    private int stockLeft;
    private List<AnimalImageSpecs> animalOptions;

    public FeedingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void setAnimalAccessPermissions(Map<String, QPermission> permissions) {
        QPermission horseAccessPermission = permissions.get("AccessToHorse");
        if (horseAccessPermission != null) {
            animalOptions.get(3).setAnimalUnlocked(horseAccessPermission.isActive());
            sharedPreferences.edit().putBoolean("animalHorseIsUnlocked",
                    horseAccessPermission.isActive()).apply();
        }

        QPermission reindeerAccessPermission = permissions.get("AccessToReindeer");
        if (reindeerAccessPermission != null) {
            animalOptions.get(4).setAnimalUnlocked(reindeerAccessPermission.isActive());
            sharedPreferences.edit().putBoolean("animalReindeerIsUnlocked",
                    reindeerAccessPermission.isActive()).apply();
        }

        QPermission zebraAccessPermission = permissions.get("AccessToZebra");
        if (zebraAccessPermission != null) {
            animalOptions.get(5).setAnimalUnlocked(zebraAccessPermission.isActive());
            sharedPreferences.edit().putBoolean("animalZebraIsUnlocked",
                    zebraAccessPermission.isActive()).apply();
        }
    }

    private void checkAnimalAccessPermissions() {
        Qonversion.checkPermissions(new QonversionPermissionsCallback() {
            @Override
            public void onSuccess(@NotNull Map<String, QPermission> permissions) {
                setAnimalAccessPermissions(permissions);
            }
            @Override
            public void onError(@NotNull QonversionError error) {
                checkAnimalAccessPermissions();
            }
        });
    }

    private void initialiseAnimalOptions(View layoutView) {
        animalOptions = new ArrayList<>() {{
            add(new AnimalImageSpecs(R.drawable.animal_cow, layoutView.findViewById(R.id.cow_selection_image_view),
                    1.5f, 1.5f, true,
                    sharedPreferences.getBoolean("cowIsSelected", true),
                    "cowIsSelected"));
            add(new AnimalImageSpecs(R.drawable.animal_rabbit, layoutView.findViewById(R.id.rabbit_selection_image_view),
                    1.0f, 1.0f, true,
                    sharedPreferences.getBoolean("rabbitIsSelected", false),
                    "rabbitIsSelected"));
            add(new AnimalImageSpecs(R.drawable.animal_goat, layoutView.findViewById(R.id.goat_selection_image_view),
                    1.8f, 1.8f, true,
                    sharedPreferences.getBoolean("goatIsSelected", false),
                    "goatIsSelected"));
            add(new AnimalImageSpecs(R.drawable.animal_horse, layoutView.findViewById(R.id.horse_selection_image_view),
                    1.5f, 1.6f,
                    sharedPreferences.getBoolean("animalHorseIsUnlocked", false),
                    sharedPreferences.getBoolean("horseIsSelected", false),
                    "horseIsSelected"));
            add(new AnimalImageSpecs(R.drawable.animal_reindeer, layoutView.findViewById(R.id.reindeer_selection_image_view),
                    1.6f, 1.4f,
                    sharedPreferences.getBoolean("animalReindeerIsUnlocked", false),
                    sharedPreferences.getBoolean("reindeerIsSelected", false),
                    "reindeerIsSelected"));
            add(new AnimalImageSpecs(R.drawable.animal_zebra, layoutView.findViewById(R.id.zebra_selection_image_view),
                    1.4f, 1.35f,
                    sharedPreferences.getBoolean("animalZebraIsUnlocked", false),
                    sharedPreferences.getBoolean("zebraIsSelected", false),
                    "zebraIsSelected"));
        }};

        checkAnimalAccessPermissions();
    }

    private void settingsAnimalOptions() {
        // Setting the selected animal image view
        for (int index = 0; index < animalOptions.size(); index++) {
            if (animalOptions.get(index).isCurrentlySelected()) {
                // Assign the 'Selected' image view for the selection image view
                animalOptions.get(index).getAnimalSelectionImageView()
                        .setImageResource(R.drawable.selected);

                // Assign the animal image view the selected animal
                animalImageView.setImageResource(animalOptions.get(index).getImageSrcDrawableResId());
                animalImageView.setScaleX(animalOptions.get(index).getImageScaleX());
                animalImageView.setScaleY(animalOptions.get(index).getImageScaleY());
            } else {
                if (!animalOptions.get(index).isAnimalUnlocked()) {
                    // Assign the 'Locked' image view for the selection image view
                    animalOptions.get(index).getAnimalSelectionImageView()
                            .setImageResource(R.drawable.access_locked);
                } else {
                    // Assign nothing image view for the selection image view
                    animalOptions.get(index).getAnimalSelectionImageView()
                            .setImageResource(0);
                }
            }
        }
    }

    private void accessLockedAnimal(String qonversionId) {
        Qonversion.purchase((Activity) context, qonversionId, new QonversionPermissionsCallback() {
            @Override
            public void onSuccess(@NotNull Map<String, QPermission> permissions) {
                setAnimalAccessPermissions(permissions);

                if (mListener != null) {
                    mListener.onFeedingFragmentInteractionGiveAccessToAnimal(qonversionId);
                }
            }

            @Override
            public void onError(@NotNull QonversionError error) {
                // TODO -> Create a purchase failed dialog
            }
        });
    }

    private void selectClickedAnimalIfUnlocked(int indexOfSelectedAnimalInAnimalOptions) {
        AnimalImageSpecs userClickedOptionAnimal = animalOptions.get(indexOfSelectedAnimalInAnimalOptions);
        if (!userClickedOptionAnimal.isCurrentlySelected()) {
            if (userClickedOptionAnimal.isAnimalUnlocked()) { // Animal is unlocked
                for (int index = 0; index < animalOptions.size(); index++) {
                    if (index == indexOfSelectedAnimalInAnimalOptions) {
                        userClickedOptionAnimal.getAnimalSelectionImageView()
                                .setImageResource(R.drawable.selected);
                        animalOptions.get(index).setCurrentlySelected(true);
                        sharedPreferences.edit().putBoolean(userClickedOptionAnimal
                                .getSharedPreferencesSelectionKey(), true).apply();
                        animalImageView.setImageResource(userClickedOptionAnimal.getImageSrcDrawableResId());
                        animalImageView.setScaleX(userClickedOptionAnimal.getImageScaleX());
                        animalImageView.setScaleY(userClickedOptionAnimal.getImageScaleY());
                    } else {
                        animalOptions.get(index).setCurrentlySelected(false);
                        sharedPreferences.edit().putBoolean(animalOptions.get(index)
                                .getSharedPreferencesSelectionKey(), false).apply();
                        if (!animalOptions.get(index).isAnimalUnlocked()) {
                            animalOptions.get(index).getAnimalSelectionImageView()
                                    .setImageResource(R.drawable.access_locked);
                        } else {
                            animalOptions.get(index).getAnimalSelectionImageView()
                                    .setImageResource(0);
                        }
                    }
                }
            } else { // Animal is locked
                if (indexOfSelectedAnimalInAnimalOptions == 3) {
                    accessLockedAnimal("Horse");
                } else if (indexOfSelectedAnimalInAnimalOptions == 4) {
                    accessLockedAnimal("Reindeer");
                } else if (indexOfSelectedAnimalInAnimalOptions == 5) {
                    accessLockedAnimal("Zebra");
                }
            }
        }
    }

    private void settingOnClickListeners() {
        backButton.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.onFeedingFragmentInteractionBackClicked();
            }
        });
        animalCowConstraintLayout.setOnClickListener(view -> selectClickedAnimalIfUnlocked(0));
        animalRabbitConstraintLayout.setOnClickListener(view -> selectClickedAnimalIfUnlocked(1));
        animalGoatConstraintLayout.setOnClickListener(view -> selectClickedAnimalIfUnlocked(2));
        animalHorseConstraintLayout.setOnClickListener(view -> selectClickedAnimalIfUnlocked(3));
        animalReindeerConstraintLayout.setOnClickListener(view -> selectClickedAnimalIfUnlocked(4));
        animalZebraConstraintLayout.setOnClickListener(view -> selectClickedAnimalIfUnlocked(5));
        rewardedAdLinearLayout.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.onFeedingFragmentShowRewardedAd();
            }
        });
        feedAnimalTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 1. Appear then fading disappear effect for post feeding messages
                postFeedingMessageLeft.setVisibility(View.VISIBLE);
                postFeedingMessageRight.setVisibility(View.VISIBLE);
                ObjectAnimator messageFadingDisappearLeft = ObjectAnimator
                        .ofFloat(postFeedingMessageLeft, View.ALPHA, 1f, 0f).setDuration(2500);
                ObjectAnimator messageFadingDisappearRight = ObjectAnimator
                        .ofFloat(postFeedingMessageRight, View.ALPHA, 1f, 0f).setDuration(2500);
                AnimatorSet postFeedingMessagesAnimator = new AnimatorSet();
                postFeedingMessagesAnimator
                        .playTogether(messageFadingDisappearLeft, messageFadingDisappearRight);
                postFeedingMessagesAnimator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                        feedAnimalTextView.setEnabled(false);
                        feedAnimalTextView.setText("FEEDING ANIMAL... ⌛");
                    }
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        postFeedingMessageLeft.setVisibility(View.INVISIBLE);
                        postFeedingMessageRight.setVisibility(View.INVISIBLE);
                        postFeedingMessageLeft.setAlpha(0f);
                        postFeedingMessageRight.setAlpha(0f);
                        feedAnimalTextView.setEnabled(true);
                        feedAnimalTextView.setText("FEED ANIMAL");
                    }
                    @Override
                    public void onAnimationCancel(Animator animator) {}
                    @Override
                    public void onAnimationRepeat(Animator animator) {}
                });

                // 2. Handling the consumption of hay by the animal
                if (stockLeft >= consumptionRate) {
                    postFeedingMessagesAnimator.start();
                    stockLeft -= consumptionRate;
                    sharedPreferences.edit().putInt("stockLeft", stockLeft).apply();
                    stockLeftTextView.setText(String.valueOf(stockLeft));
                } else {
                    if (mListener != null) {
                        mListener.onFeedingFragmentInteractionOutOfStock();
                    }
                }
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

        View view = inflater.inflate(R.layout.fragment_feeding, container, false);

        backButton = view.findViewById(R.id.title_back_feeding_fragment_button);
        feedAnimalTextView = view.findViewById(R.id.feed_animal_text_view_feeding_fragment);
        animalImageView = view.findViewById(R.id.animal_imageview_feeding_fragment);
        AppCompatTextView consumptionRateTextView = view.findViewById(R.id.consumption_rate_text_view);
        stockLeftTextView = view.findViewById(R.id.stock_left_text_view_feeding_fragment);
        postFeedingMessageLeft = view.findViewById(R.id.post_feeding_message_left);
        postFeedingMessageRight = view.findViewById(R.id.post_feeding_message_right);

        animalCowConstraintLayout = view.findViewById(R.id.animal_cow_constraint_layout);
        animalRabbitConstraintLayout = view.findViewById(R.id.animal_rabbit_constraint_layout);
        animalGoatConstraintLayout = view.findViewById(R.id.animal_goat_constraint_layout);
        animalHorseConstraintLayout = view.findViewById(R.id.animal_horse_constraint_layout);
        animalReindeerConstraintLayout = view.findViewById(R.id.animal_reindeer_constraint_layout);
        animalZebraConstraintLayout = view.findViewById(R.id.animal_zebra_constraint_layout);

        rewardedAdLinearLayout = view.findViewById(R.id.rewarded_ad_linear_layout_feeding_fragment);

        consumptionRate = sharedPreferences.getInt("consumptionRate", 2);
        consumptionRateTextView.setText(String.valueOf(consumptionRate));
        stockLeft = sharedPreferences.getInt("stockLeft", 20);
        //stockLeft = 10; /* Comment this line when not testing */
        stockLeftTextView.setText(String.valueOf(stockLeft));

        initialiseAnimalOptions(view);

        if (mListener != null) {
            mListener.onFeedingFragmentInteractionHasRewardedAdLoaded();
        }

        settingsAnimalOptions();

        settingOnClickListeners();

        return view;
    }

    public void showRewardedAdOption() {
        if (mListener != null) {
            rewardedAdLinearLayout.setVisibility(View.VISIBLE);
        }
    }

    public void updateHayStockFeedingFragment(int updatedStock) {
        if (mListener != null) {
            stockLeft = updatedStock;
            stockLeftTextView.setText(String.valueOf(stockLeft));
        }
    }

    public void unlockAccessToAnimalHorse() {
        if (mListener != null) {
            int indexOfAnimalHorse = 3;
            animalOptions.get(indexOfAnimalHorse).setAnimalUnlocked(true);
            animalOptions.get(indexOfAnimalHorse).getAnimalSelectionImageView().setImageResource(0);
        }
    }

    public void unlockAccessToAnimalReindeer() {
        if (mListener != null) {
            int indexOfAnimalReindeer = 4;
            animalOptions.get(indexOfAnimalReindeer).setAnimalUnlocked(true);
            animalOptions.get(indexOfAnimalReindeer).getAnimalSelectionImageView().setImageResource(0);
        }
    }

    public void unlockAccessToAnimalZebra() {
        if (mListener != null) {
            int indexOfAnimalZebra = 5;
            animalOptions.get(indexOfAnimalZebra).setAnimalUnlocked(true);
            animalOptions.get(indexOfAnimalZebra).getAnimalSelectionImageView().setImageResource(0);
        }
    }

    public interface OnFeedingFragmentInteractionListener {
        void onFeedingFragmentInteractionHasRewardedAdLoaded();
        void onFeedingFragmentInteractionBackClicked();
        void onFeedingFragmentShowRewardedAd();
        void onFeedingFragmentInteractionOutOfStock();
        void onFeedingFragmentInteractionGiveAccessToAnimal(String qonversionId);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFeedingFragmentInteractionListener) {
            mListener = (OnFeedingFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFeedingFragmentInteractionListener");
        }
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
