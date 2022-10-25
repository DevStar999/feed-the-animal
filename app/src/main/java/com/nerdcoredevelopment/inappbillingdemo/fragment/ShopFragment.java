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

import java.util.ArrayList;
import java.util.List;

public class ShopFragment extends Fragment {
    public static final String ITEM_PRICES = "itemPrices";
    private Context context;
    private OnShopFragmentInteractionListener mListener;
    private SharedPreferences sharedPreferences;

    /* Views related to this fragment */
    private AppCompatImageView backButton;
    private AppCompatTextView stockLeftTextView;
    private List<ConstraintLayout> shopFeedConstraintLayouts;
    private List<AppCompatButton> shopFeedPurchaseButtons;

    /* Variables related to this fragment */
    private int stockLeft;
    private List<String> itemPrices;

    public ShopFragment() {
        // Required empty public constructor
    }

    public static ShopFragment newInstance(ArrayList<String> itemPrices) {
        ShopFragment fragment = new ShopFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(ITEM_PRICES, itemPrices);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.itemPrices = new ArrayList<>(getArguments().getStringArrayList(ITEM_PRICES));
        }
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

        for (int index = 0; index < shopFeedConstraintLayouts.size(); index++) {
            shopFeedConstraintLayouts.get(index).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onShopFragmentInteractionPurchaseOptionClicked(view.getId());
                    }
                }
            });
        }
        for (int index = 0; index < shopFeedPurchaseButtons.size(); index++) {
            shopFeedPurchaseButtons.get(index).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onShopFragmentInteractionPurchaseOptionClicked(view.getId());
                    }
                }
            });
        }
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
        shopFeedConstraintLayouts = new ArrayList<>();
        for (int level = 1; level <= 4; level++) {
            int layoutResId = context.getResources().getIdentifier("shop_feed_level" + level +
                            "_constraint_layout", "id", context.getPackageName());
            shopFeedConstraintLayouts.add(view.findViewById(layoutResId));
        }
        shopFeedPurchaseButtons = new ArrayList<>();
        for (int level = 1; level <= 4; level++) {
            int layoutResId = context.getResources().getIdentifier("shop_feed_level" + level +
                    "_purchase_button", "id", context.getPackageName());
            shopFeedPurchaseButtons.add(view.findViewById(layoutResId));
        }

        stockLeft = sharedPreferences.getInt("stockLeft", 20);
        stockLeftTextView.setText(String.valueOf(stockLeft));
        if (itemPrices.size() == 4) {
            for (int index = 0; index < shopFeedPurchaseButtons.size(); index++) {
                shopFeedPurchaseButtons.get(index).setText(itemPrices.get(index));
            }
        }

        settingOnClickListeners();

        return view;
    }

    public void updateHayStockShopFragment(int hayUnitsPurchase) {
        stockLeft += hayUnitsPurchase;
        stockLeftTextView.setText(String.valueOf(stockLeft));
        sharedPreferences.edit().putInt("stockLeft", stockLeft).apply();
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
