package com.nerdcoredevelopment.inappbillingdemo.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.nerdcoredevelopment.inappbillingdemo.R;

public class FeedingFragment extends Fragment {
    private Context context;
    private OnFeedingFragmentInteractionListener mListener;
    private SharedPreferences sharedPreferences;

    /* Views related to this fragment */
    private AppCompatImageView backButton;
    private AppCompatTextView feedAnimalTextView;
    private AppCompatTextView consumptionRateTextView;
    private AppCompatTextView stockLeftTextView;
    private ConstraintLayout animalCowConstraintLayout;
    private ConstraintLayout animalRabbitConstraintLayout;
    private ConstraintLayout animalGoatConstraintLayout;
    private ConstraintLayout animalReindeerConstraintLayout;
    private ConstraintLayout animalHorseConstraintLayout;
    private ConstraintLayout animalZebraConstraintLayout;
    private AppCompatImageView animalCowSelectionImageView;
    private AppCompatImageView animalRabbitSelectionImageView;
    private AppCompatImageView animalGoatSelectionImageView;
    private AppCompatImageView animalReindeerSelectionImageView;
    private AppCompatImageView animalHorseSelectionImageView;
    private AppCompatImageView animalZebraSelectionImageView;

    /* Variables related to this fragment */
    private int consumptionRate;
    private int stockLeft;

    public FeedingFragment() {
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
                    mListener.onFeedingFragmentInteractionBackClicked();
                }
            }
        });
        feedAnimalTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Custom Debugging", "onClick: FeedingFragment, Feed Animal button clicked");
            }
        });
        animalCowConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Custom Debugging", "onClick: FeedingFragment, Cow button clicked");
            }
        });
        animalRabbitConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Custom Debugging", "onClick: FeedingFragment, Rabbit button clicked");
            }
        });
        animalGoatConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Custom Debugging", "onClick: FeedingFragment, Goat button clicked");
            }
        });
        animalReindeerConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Custom Debugging", "onClick: FeedingFragment, Reindeer button clicked");
            }
        });
        animalHorseConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Custom Debugging", "onClick: FeedingFragment, Horse button clicked");
            }
        });
        animalZebraConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Custom Debugging", "onClick: FeedingFragment, Zebra button clicked");
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
        consumptionRateTextView = view.findViewById(R.id.consumption_rate_text_view);
        stockLeftTextView = view.findViewById(R.id.stock_left_text_view);

        animalCowConstraintLayout = view.findViewById(R.id.animal_cow_constraint_layout);
        animalRabbitConstraintLayout = view.findViewById(R.id.animal_rabbit_constraint_layout);
        animalGoatConstraintLayout = view.findViewById(R.id.animal_goat_constraint_layout);
        animalReindeerConstraintLayout = view.findViewById(R.id.animal_reindeer_constraint_layout);
        animalHorseConstraintLayout = view.findViewById(R.id.animal_horse_constraint_layout);
        animalZebraConstraintLayout = view.findViewById(R.id.animal_zebra_constraint_layout);

        animalCowSelectionImageView = view.findViewById(R.id.cow_selection_image_view);
        animalRabbitSelectionImageView = view.findViewById(R.id.rabbit_selection_image_view);
        animalGoatSelectionImageView = view.findViewById(R.id.goat_selection_image_view);
        animalReindeerSelectionImageView = view.findViewById(R.id.reindeer_selection_image_view);
        animalHorseSelectionImageView = view.findViewById(R.id.horse_selection_image_view);
        animalZebraSelectionImageView = view.findViewById(R.id.zebra_selection_image_view);

        consumptionRate = sharedPreferences.getInt("consumptionRate", 2);
        consumptionRateTextView.setText(String.valueOf(consumptionRate));
        stockLeft = sharedPreferences.getInt("stockLeft", 50);
        stockLeftTextView.setText(String.valueOf(stockLeft));
        //stockLeft = 10; /* Comment this line when not testing */

        settingOnClickListeners();

        return view;
    }

    public interface OnFeedingFragmentInteractionListener {
        void onFeedingFragmentInteractionBackClicked();
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
