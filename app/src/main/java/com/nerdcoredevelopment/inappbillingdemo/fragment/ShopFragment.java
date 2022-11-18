package com.nerdcoredevelopment.inappbillingdemo.fragment;

import android.app.Activity;
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
import com.qonversion.android.sdk.Qonversion;
import com.qonversion.android.sdk.QonversionError;
import com.qonversion.android.sdk.QonversionErrorCode;
import com.qonversion.android.sdk.QonversionOfferingsCallback;
import com.qonversion.android.sdk.QonversionPermissionsCallback;
import com.qonversion.android.sdk.dto.QPermission;
import com.qonversion.android.sdk.dto.offerings.QOffering;
import com.qonversion.android.sdk.dto.offerings.QOfferings;
import com.qonversion.android.sdk.dto.products.QProduct;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopFragment extends Fragment {
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
    private Map<String, Integer> hayUnitsReward;

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
    }

    private void handlePurchaseOfHay(QProduct qProduct, String productIdPrefix) {
        Qonversion.purchase((Activity) context, qProduct, new QonversionPermissionsCallback() {
            @Override
            public void onSuccess(@NotNull Map<String, QPermission> permissions) {
                stockLeft += hayUnitsReward.get(productIdPrefix);
                if (mListener != null) {
                    mListener.onShopFragmentUpdateHayUnits(stockLeft);
                }
            }
            @Override
            public void onError(@NotNull QonversionError error) {
                // TODO -> Create a purchase failed dialog
            }
        });
    }

    private void loadItemPrices() {
        Qonversion.offerings(new QonversionOfferingsCallback() {
            @Override
            public void onSuccess(@NotNull QOfferings offerings) {
                if (!offerings.getAvailableOfferings().isEmpty()) {
                    for (QOffering currentOffering: offerings.getAvailableOfferings()) {
                        QProduct qProduct = currentOffering.getProducts().get(0);
                        String storeId = qProduct.getStoreID();
                        String prefix = "hay_level";
                        if (storeId != null && storeId.startsWith(prefix)) {
                            int level = Integer.parseInt(String.valueOf(storeId.charAt(prefix.length())));
                            level--;
                            if (qProduct.getSkuDetail() != null && !qProduct.getSkuDetail().getPrice().isEmpty()) {
                                shopFeedPurchaseButtons.get(level).setText(qProduct.getSkuDetail().getPrice());
                            }
                            String finalPrefix = prefix + (level+1);
                            shopFeedConstraintLayouts.get(level)
                                    .setOnClickListener(view -> handlePurchaseOfHay(qProduct, finalPrefix));
                            shopFeedPurchaseButtons.get(level)
                                    .setOnClickListener(view -> handlePurchaseOfHay(qProduct, finalPrefix));
                        }
                    }
                }
            }
            @Override
            public void onError(@NotNull QonversionError error) {
                loadItemPrices();
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
        hayUnitsReward = new HashMap<>() {{
            put("hay_level1", 50); put("hay_level2", 100);
            put("hay_level3", 250); put("hay_level4", 500);
        }};

        settingOnClickListeners();

        loadItemPrices();

        return view;
    }

    public void updateHayStockShopFragment(int updatedStock) {
        if (mListener != null) {
            stockLeft = updatedStock;
            stockLeftTextView.setText(String.valueOf(stockLeft));
        }
    }

    public interface OnShopFragmentInteractionListener {
        void onShopFragmentInteractionBackClicked();
        void onShopFragmentUpdateHayUnits(int stockLeft);
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
