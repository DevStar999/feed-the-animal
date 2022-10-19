package com.nerdcoredevelopment.inappbillingdemo.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import com.nerdcoredevelopment.inappbillingdemo.R;

public class NavigationFragment extends Fragment {
    private OnNavigationFragmentInteractionListener mListener;

    public NavigationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        View view = inflater.inflate(R.layout.fragment_navigation, container, false);

        AppCompatTextView feedAnimalsTextView = view.findViewById(R.id.feed_animals_text_view_navigation_fragment);
        AppCompatTextView meetTheFarmerTextView = view.findViewById(R.id.meet_the_farmer_text_view_navigation_fragment);
        AppCompatTextView shopFeedTextView = view.findViewById(R.id.shop_feed_text_view_navigation_fragment);
        AppCompatTextView settingsTextView = view.findViewById(R.id.settings_text_view_navigation_fragment);

        feedAnimalsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onNavigationFragmentFeedAnimalsClicked();
                }
            }
        });
        meetTheFarmerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onNavigationFragmentMeetTheFarmerClicked();
                }
            }
        });
        shopFeedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onNavigationFragmentShopFeedClicked();
                }
            }
        });
        settingsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onNavigationFragmentSettingsClicked();
                }
            }
        });

        return view;
    }

    public interface OnNavigationFragmentInteractionListener {
        void onNavigationFragmentFeedAnimalsClicked();
        void onNavigationFragmentMeetTheFarmerClicked();
        void onNavigationFragmentShopFeedClicked();
        void onNavigationFragmentSettingsClicked();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnNavigationFragmentInteractionListener) {
            mListener = (OnNavigationFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnNavigationFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
