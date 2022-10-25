package com.nerdcoredevelopment.inappbillingdemo.fragment;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.nerdcoredevelopment.inappbillingdemo.AnimalImageSpecs;
import com.nerdcoredevelopment.inappbillingdemo.R;

import java.util.ArrayList;
import java.util.List;

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
                if (mListener != null) {
                    if (indexOfSelectedAnimalInAnimalOptions == 3) {
                        mListener.onFeedingFragmentInteractionAccessLockedAnimalHorse();
                    } else if (indexOfSelectedAnimalInAnimalOptions == 4) {
                        mListener.onFeedingFragmentInteractionAccessLockedAnimalReindeer();
                    } else if (indexOfSelectedAnimalInAnimalOptions == 5) {
                        mListener.onFeedingFragmentInteractionAccessLockedAnimalZebra();
                    }
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
                    stockLeftTextView.setText(String.valueOf(stockLeft));
                    sharedPreferences.edit().putInt("stockLeft", stockLeft).apply();
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

        consumptionRate = sharedPreferences.getInt("consumptionRate", 2);
        consumptionRateTextView.setText(String.valueOf(consumptionRate));
        stockLeft = sharedPreferences.getInt("stockLeft", 20);
        //stockLeft = 10; /* Comment this line when not testing */
        stockLeftTextView.setText(String.valueOf(stockLeft));
        animalOptions = new ArrayList<>() {{
            add(new AnimalImageSpecs(R.drawable.animal_cow, view.findViewById(R.id.cow_selection_image_view),
                    1.5f, 1.5f, true,
                    sharedPreferences.getBoolean("cowIsSelected", true),
                    "cowIsSelected"));
            add(new AnimalImageSpecs(R.drawable.animal_rabbit, view.findViewById(R.id.rabbit_selection_image_view),
                    1.0f, 1.0f, true,
                    sharedPreferences.getBoolean("rabbitIsSelected", false),
                    "rabbitIsSelected"));
            add(new AnimalImageSpecs(R.drawable.animal_goat, view.findViewById(R.id.goat_selection_image_view),
                    1.8f, 1.8f, true,
                    sharedPreferences.getBoolean("goatIsSelected", false),
                    "goatIsSelected"));
            add(new AnimalImageSpecs(R.drawable.animal_horse, view.findViewById(R.id.horse_selection_image_view),
                    1.5f, 1.6f,
                    sharedPreferences.getBoolean("animalHorseIsUnlocked", false),
                    sharedPreferences.getBoolean("horseIsSelected", false),
                    "horseIsSelected"));
            add(new AnimalImageSpecs(R.drawable.animal_reindeer, view.findViewById(R.id.reindeer_selection_image_view),
                    1.6f, 1.4f,
                    sharedPreferences.getBoolean("animalReindeerIsUnlocked", false),
                    sharedPreferences.getBoolean("reindeerIsSelected", false),
                    "reindeerIsSelected"));
            add(new AnimalImageSpecs(R.drawable.animal_zebra, view.findViewById(R.id.zebra_selection_image_view),
                    1.4f, 1.35f,
                    sharedPreferences.getBoolean("animalZebraIsUnlocked", false),
                    sharedPreferences.getBoolean("zebraIsSelected", false),
                    "zebraIsSelected"));
        }};

        settingsAnimalOptions();

        settingOnClickListeners();

        return view;
    }

    public void unlockAccessToAnimalHorse() {
        int indexOfAnimalHorse = 3;
        animalOptions.get(indexOfAnimalHorse).setAnimalUnlocked(true);
        sharedPreferences.edit().putBoolean("animalHorseIsUnlocked", true).apply();
        animalOptions.get(indexOfAnimalHorse).getAnimalSelectionImageView().setImageResource(0);
    }

    public void unlockAccessToAnimalReindeer() {
        int indexOfAnimalReindeer = 4;
        animalOptions.get(indexOfAnimalReindeer).setAnimalUnlocked(true);
        sharedPreferences.edit().putBoolean("animalReindeerIsUnlocked", true).apply();
        animalOptions.get(indexOfAnimalReindeer).getAnimalSelectionImageView().setImageResource(0);
    }

    public void unlockAccessToAnimalZebra() {
        int indexOfAnimalZebra = 5;
        animalOptions.get(indexOfAnimalZebra).setAnimalUnlocked(true);
        sharedPreferences.edit().putBoolean("animalZebraIsUnlocked", true).apply();
        animalOptions.get(indexOfAnimalZebra).getAnimalSelectionImageView().setImageResource(0);
    }

    public interface OnFeedingFragmentInteractionListener {
        void onFeedingFragmentInteractionBackClicked();
        void onFeedingFragmentInteractionOutOfStock();
        void onFeedingFragmentInteractionAccessLockedAnimalHorse();
        void onFeedingFragmentInteractionAccessLockedAnimalReindeer();
        void onFeedingFragmentInteractionAccessLockedAnimalZebra();
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
