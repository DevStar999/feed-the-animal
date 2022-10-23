package com.nerdcoredevelopment.inappbillingdemo.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import com.nerdcoredevelopment.inappbillingdemo.R;

public class InfoFragment extends Fragment {
    public static final String INFO_TEXT = "infoText";
    private String infoText;
    private OnInfoFragmentInteractionListener mListener;
    private AppCompatImageView backButton;

    public InfoFragment() {
        // Required empty public constructor
    }

    public static InfoFragment newInstance(String infoText) {
        InfoFragment fragment = new InfoFragment();
        Bundle args = new Bundle();
        args.putString(INFO_TEXT, infoText);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.infoText = getArguments().getString(INFO_TEXT);
        }
    }

    private void settingOnClickListeners() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onInfoFragmentInteractionBackClicked();
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

        View view = inflater.inflate(R.layout.fragment_info, container, false);

        backButton = view.findViewById(R.id.title_back_info_fragment_button);
        AppCompatTextView infoTextView = view.findViewById(R.id.info_text_view_info_fragment);
        infoTextView.setText(infoText);

        settingOnClickListeners();

        return view;
    }

    public interface OnInfoFragmentInteractionListener {
        void onInfoFragmentInteractionBackClicked();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnInfoFragmentInteractionListener) {
            mListener = (OnInfoFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnInfoFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
