package com.nerdcoredevelopment.inappbillingdemo.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import com.nerdcoredevelopment.inappbillingdemo.R;

public class FarmerFragment extends Fragment {
    private OnFarmerFragmentInteractionListener mListener;
    private AppCompatImageView backButton;
    private LinearLayout aboutFarmerLinearLayout;

    public FarmerFragment() {
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
                    mListener.onFarmerFragmentInteractionBackClicked();
                }
            }
        });
        aboutFarmerLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onFarmerFragmentInteractionAboutFarmerClicked();
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

        View view = inflater.inflate(R.layout.fragment_farmer, container, false);

        backButton = view.findViewById(R.id.title_back_farmer_fragment_button);
        aboutFarmerLinearLayout = view.findViewById(R.id.about_farmer_linear_layout);

        settingOnClickListeners();

        return view;
    }

    public interface OnFarmerFragmentInteractionListener {
        void onFarmerFragmentInteractionBackClicked();
        void onFarmerFragmentInteractionAboutFarmerClicked();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFarmerFragmentInteractionListener) {
            mListener = (OnFarmerFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFarmerFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
