package com.nerdcoredevelopment.inappbillingdemo.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.nerdcoredevelopment.inappbillingdemo.R;

public class ShopFragment extends Fragment {
    private Context context;
    private OnShopFragmentInteractionListener mListener;
    private SharedPreferences sharedPreferences;

    /* Views related to this fragment */
    private AppCompatImageView backButton;
    private AppCompatTextView stockLeftTextView;
    private ConstraintLayout shopFeedLevel1ConstraintLayout;
    private ConstraintLayout shopFeedLevel2ConstraintLayout;
    private ConstraintLayout shopFeedLevel3ConstraintLayout;
    private ConstraintLayout shopFeedLevel4ConstraintLayout;
    private AppCompatButton shopFeedLevel1PurchaseButton;
    private AppCompatButton shopFeedLevel2PurchaseButton;
    private AppCompatButton shopFeedLevel3PurchaseButton;
    private AppCompatButton shopFeedLevel4PurchaseButton;

    /* Variables related to this fragment */
    private int stockLeft;

    public ShopFragment() {
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
                    mListener.onShopFragmentInteractionBackClicked();
                }
            }
        });

        shopFeedLevel1ConstraintLayout.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.onShopFragmentInteractionPurchaseOptionClicked(view.getId());
            }
        });
        shopFeedLevel2ConstraintLayout.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.onShopFragmentInteractionPurchaseOptionClicked(view.getId());
            }
        });
        shopFeedLevel3ConstraintLayout.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.onShopFragmentInteractionPurchaseOptionClicked(view.getId());
            }
        });
        shopFeedLevel4ConstraintLayout.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.onShopFragmentInteractionPurchaseOptionClicked(view.getId());
            }
        });

        shopFeedLevel1PurchaseButton.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.onShopFragmentInteractionPurchaseOptionClicked(view.getId());
            }
        });
        shopFeedLevel2PurchaseButton.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.onShopFragmentInteractionPurchaseOptionClicked(view.getId());
            }
        });
        shopFeedLevel3PurchaseButton.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.onShopFragmentInteractionPurchaseOptionClicked(view.getId());
            }
        });
        shopFeedLevel4PurchaseButton.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.onShopFragmentInteractionPurchaseOptionClicked(view.getId());
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

        View view = inflater.inflate(R.layout.fragment_shop, container, false);

        backButton = view.findViewById(R.id.title_back_shop_fragment_button);

        stockLeftTextView = view.findViewById(R.id.stock_left_text_view_shop_fragment);
        shopFeedLevel1ConstraintLayout = view.findViewById(R.id.shop_feed_level1_constraint_layout);
        shopFeedLevel2ConstraintLayout = view.findViewById(R.id.shop_feed_level2_constraint_layout);
        shopFeedLevel3ConstraintLayout = view.findViewById(R.id.shop_feed_level3_constraint_layout);
        shopFeedLevel4ConstraintLayout = view.findViewById(R.id.shop_feed_level4_constraint_layout);

        shopFeedLevel1PurchaseButton = view.findViewById(R.id.shop_feed_level1_purchase_button);
        shopFeedLevel2PurchaseButton = view.findViewById(R.id.shop_feed_level2_purchase_button);
        shopFeedLevel3PurchaseButton = view.findViewById(R.id.shop_feed_level3_purchase_button);
        shopFeedLevel4PurchaseButton = view.findViewById(R.id.shop_feed_level4_purchase_button);

        stockLeft = sharedPreferences.getInt("stockLeft", 50);
        stockLeftTextView.setText(String.valueOf(stockLeft));

        settingOnClickListeners();

        return view;
    }

    public interface OnShopFragmentInteractionListener {
        void onShopFragmentInteractionBackClicked();
        void onShopFragmentInteractionPurchaseOptionClicked(int purchaseOptionViewId);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnShopFragmentInteractionListener) {
            mListener = (OnShopFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnShopFragmentInteractionListener");
        }
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
