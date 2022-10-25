package com.nerdcoredevelopment.inappbillingdemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nerdcoredevelopment.inappbillingdemo.dialogs.GameExitDialog;
import com.nerdcoredevelopment.inappbillingdemo.fragment.FarmerFragment;
import com.nerdcoredevelopment.inappbillingdemo.fragment.FeedingFragment;
import com.nerdcoredevelopment.inappbillingdemo.fragment.InfoFragment;
import com.nerdcoredevelopment.inappbillingdemo.fragment.NavigationFragment;
import com.nerdcoredevelopment.inappbillingdemo.fragment.SettingsFragment;
import com.nerdcoredevelopment.inappbillingdemo.fragment.ShopFragment;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements
        InfoFragment.OnInfoFragmentInteractionListener,
        NavigationFragment.OnNavigationFragmentInteractionListener,
        FarmerFragment.OnFarmerFragmentInteractionListener,
        FeedingFragment.OnFeedingFragmentInteractionListener,
        SettingsFragment.OnSettingsFragmentInteractionListener,
        ShopFragment.OnShopFragmentInteractionListener {
    private NavigationFragment navigationFragment;
    private FeedingFragment feedingFragment;
    //private BillingClient recurringConsumablesBillingClient;
    private BillingClient nonRecurringConsumablesBillingClient;
    private SharedPreferences sharedPreferences;
    private Map<String, SkuDetails> animalsSkuDetails;

    private void initialise() {
        navigationFragment = new NavigationFragment();
        feedingFragment = new FeedingFragment();
        sharedPreferences = getSharedPreferences("com.nerdcoredevelopment.inappbillingdemo", Context.MODE_PRIVATE);
        animalsSkuDetails = new HashMap<>();
    }

    private void setupBillingClients() {
        /*
        PurchasesUpdatedListener purchasesUpdatedListenerForRecurringConsumables = (billingResult,
                                                                                    list) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                    && list != null) {
                for (Purchase purchase : list) {
                    verifyPurchase(purchase, true);
                }
            }
        };
        recurringConsumablesBillingClient = BillingClient.newBuilder(this)
                .setListener(purchasesUpdatedListenerForRecurringConsumables)
                .enablePendingPurchases().build();
        connectToGooglePlayBillingForRecurringConsumables();
        */

        PurchasesUpdatedListener purchasesUpdatedListenerForNonRecurringConsumables = (billingResult,
                                                                                    list) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                    && list != null) {
                for (Purchase purchase : list) {
                    verifyPurchase(purchase, false);
                }
            }
        };
        nonRecurringConsumablesBillingClient = BillingClient.newBuilder(this)
                .setListener(purchasesUpdatedListenerForNonRecurringConsumables)
                .enablePendingPurchases().build();
        connectToGooglePlayBillingForNonRecurringConsumables();
    }

    private void setupBillingClientsOnResume() {
        /*
        recurringConsumablesBillingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP,
            new PurchasesResponseListener() {
                @Override
                public void onQueryPurchasesResponse(@NonNull BillingResult billingResult,
                                                     @NonNull List<Purchase> list) {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        for (Purchase purchase : list) {
                            verifyPurchase(purchase, true);
                        }
                    }
                }
            }
        );
        */
        nonRecurringConsumablesBillingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP,
            new PurchasesResponseListener() {
                @Override
                public void onQueryPurchasesResponse(@NonNull BillingResult billingResult,
                                                     @NonNull List<Purchase> list) {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        for (Purchase purchase : list) {
                            verifyPurchase(purchase, false);
                        }
                    }
                }
            }
        );
    }

    /*
    private void connectToGooglePlayBillingForRecurringConsumables() {
        recurringConsumablesBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {
                connectToGooglePlayBillingForRecurringConsumables();
            }
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    getProductDetailsOfRecurringConsumables();
                }
            }
        });
    }
    */

    private void connectToGooglePlayBillingForNonRecurringConsumables() {
        nonRecurringConsumablesBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {
                connectToGooglePlayBillingForNonRecurringConsumables();
            }
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    getProductDetailsOfNonRecurringConsumables();
                }
            }
        });
    }

    /*
    private void getProductDetailsOfRecurringConsumables() {
        List<String> productIds = new ArrayList<>();
        for (int level = 1; level <= 4; level++) {
            productIds.add("hay_level" + level);
        }

        SkuDetailsParams getProductDetailsQuery = SkuDetailsParams
                .newBuilder().setSkusList(productIds)
                .setType(BillingClient.SkuType.INAPP).build();
        Activity activity = this;
        SkuDetailsResponseListener skuDetailsResponseListener = new SkuDetailsResponseListener() {
            @Override
            public void onSkuDetailsResponse(@NonNull BillingResult billingResult,
                                             @Nullable List<SkuDetails> list) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK &&
                        list != null) {

                }
            }
        };

        recurringConsumablesBillingClient.querySkuDetailsAsync(getProductDetailsQuery,
                skuDetailsResponseListener);
    }
    */

    private void getProductDetailsOfNonRecurringConsumables() {
        // If we have the SkuDetails for the 3 locked animals then return
        if (animalsSkuDetails.containsKey("animal_horse")
                && animalsSkuDetails.containsKey("animal_reindeer")
                && animalsSkuDetails.containsKey("animal_zebra")) {
            return;
        }

        List<String> productIds = new ArrayList<>();
        productIds.add("animal_horse");
        productIds.add("animal_reindeer");
        productIds.add("animal_zebra");

        SkuDetailsParams getProductDetailsQuery = SkuDetailsParams.newBuilder().setSkusList(productIds)
                .setType(BillingClient.SkuType.INAPP).build();
        SkuDetailsResponseListener skuDetailsResponseListener = new SkuDetailsResponseListener() {
            @Override
            public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> list) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK &&
                        list != null) {
                    for (int index = 0; index < list.size(); index++) {
                        animalsSkuDetails.put(list.get(index).getSku(), list.get(index));
                    }
                }
            }
        };

        nonRecurringConsumablesBillingClient.querySkuDetailsAsync(getProductDetailsQuery, skuDetailsResponseListener);
    }

    private void verifyPurchase(Purchase purchase, boolean isPurchaseRecurring) {
        String requestUrl = "https://us-central1-feed-the-animal-afe03.cloudfunctions.net/verifyPurchases?" +
                "purchaseToken=" + purchase.getPurchaseToken() + "&" +
                "purchaseTime=" + purchase.getPurchaseTime() + "&" +
                "orderId=" + purchase.getOrderId();

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject purchaseInfoFromServer = new JSONObject(response);
                    if (purchaseInfoFromServer.getBoolean("isValid")) {
                        // Code for Non-Recurring Consumables
                        if (!isPurchaseRecurring && purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED
                                && !purchase.isAcknowledged()) {
                            AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                                    .setPurchaseToken(purchase.getPurchaseToken()).build();
                            AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener
                                = billingResult -> {
                                if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                                    String productId = purchase.getSkus().get(0);
                                    if (productId.equals("animal_horse")) {
                                        feedingFragment.unlockAccessToAnimalHorse();
                                    } else if (productId.equals("animal_reindeer")) {
                                        feedingFragment.unlockAccessToAnimalReindeer();
                                    } else if (productId.equals("animal_zebra")) {
                                        feedingFragment.unlockAccessToAnimalZebra();
                                    }
                                }
                            };

                            nonRecurringConsumablesBillingClient.acknowledgePurchase(acknowledgePurchaseParams,
                                    acknowledgePurchaseResponseListener);
                        }
                    }
                } catch (Exception ignored) {}
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {}
        };

        StringRequest stringRequest = new StringRequest(Request.Method.POST, requestUrl, responseListener, errorListener);

        Volley.newRequestQueue(this).add(stringRequest);
    }

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_FeedTheAnimal);

        /*
        Following lines of code hide the status bar at the very top of the screen which battery
        indicator, network status other icons etc. Note, this is done before setting the layout with
        the line -> setContentView(R.layout.activity_main);
        */
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        // To Disable screen rotation and keep the device in Portrait mode
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // To set the app theme to 'LIGHT' (even if 'DARK' theme is selected, however if user in their
        // settings enables 'DARK' theme for our individual app, then it will override the following line
        // no matter what)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // To hide the navigation bar as default i.e. it will hide by itself if left unused or unattended
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        initialise();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.navigation_fragment_container_main_activity, navigationFragment)
                .commit();

        setupBillingClients();
    }

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onResume() {
        super.onResume();

        /* Persisting the screen settings even if the user leaves the app mid use for when he/she
           returns to use the app again
        */
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        setupBillingClientsOnResume();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            GameExitDialog gameExitDialog = new GameExitDialog(this);
            gameExitDialog.show();
            gameExitDialog.setGameExitDialogListener(new GameExitDialog.GameExitDialogListener() {
                @Override
                public void getResponseOfExitDialog(boolean response) {
                    if (response) {
                        MainActivity.this.finish();
                    }
                }
            });
        } else {
            // Back button was pressed from fragment
            getSupportFragmentManager().popBackStack();
        }
    }

    public void infoButtonClicked(View view) {
        InfoFragment fragment =
                InfoFragment.newInstance("<Add any text you would like to print here>");
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right,
                R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.add(R.id.full_screen_fragment_container_main_activity,
                fragment, "INFO_FRAGMENT").commit();
    }

    @Override
    public void onInfoFragmentInteractionBackClicked() {
        onBackPressed();
    }

    @Override
    public void onNavigationFragmentMeetTheFarmerClicked() {
        FarmerFragment fragment = new FarmerFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right,
                R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.add(R.id.full_screen_fragment_container_main_activity,
                fragment, "FARMER_FRAGMENT").commit();
    }

    @Override
    public void onNavigationFragmentFeedAnimalsClicked() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right,
                R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.add(R.id.full_screen_fragment_container_main_activity,
                feedingFragment, "FEEDING_FRAGMENT").commit();
    }

    @Override
    public void onNavigationFragmentSettingsClicked() {
        SettingsFragment fragment = new SettingsFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right,
                R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.add(R.id.full_screen_fragment_container_main_activity,
                fragment, "SETTINGS_FRAGMENT").commit();
    }

    @Override
    public void onNavigationFragmentShopFeedClicked() {
        ShopFragment fragment = new ShopFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right,
                R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.add(R.id.full_screen_fragment_container_main_activity,
                fragment, "SHOP_FRAGMENT").commit();
    }

    @Override
    public void onFarmerFragmentInteractionBackClicked() {
        onBackPressed();
    }

    @Override
    public void onFarmerFragmentInteractionAboutFarmerClicked() {
        Toast.makeText(this, "About Farmer from Farmer fragment, clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFeedingFragmentInteractionBackClicked() {
        onBackPressed();
    }

    @Override
    public void onFeedingFragmentInteractionOutOfStock() {
        ShopFragment fragment = new ShopFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right,
                R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.add(R.id.full_screen_fragment_container_main_activity,
                fragment, "SHOP_FRAGMENT").commit();
    }

    @Override
    public void onFeedingFragmentInteractionAccessLockedAnimalHorse() {
        if (animalsSkuDetails.containsKey("animal_horse")) {
            nonRecurringConsumablesBillingClient.launchBillingFlow(this, BillingFlowParams.newBuilder()
                            .setSkuDetails(Objects.requireNonNull(animalsSkuDetails.get("animal_horse"))).build());
        }
    }

    @Override
    public void onFeedingFragmentInteractionAccessLockedAnimalReindeer() {
        if (animalsSkuDetails.containsKey("animal_reindeer")) {
            nonRecurringConsumablesBillingClient.launchBillingFlow(this, BillingFlowParams.newBuilder()
                    .setSkuDetails(Objects.requireNonNull(animalsSkuDetails.get("animal_reindeer"))).build());
        }
    }

    @Override
    public void onFeedingFragmentInteractionAccessLockedAnimalZebra() {
        if (animalsSkuDetails.containsKey("animal_zebra")) {
            nonRecurringConsumablesBillingClient.launchBillingFlow(this, BillingFlowParams.newBuilder()
                    .setSkuDetails(Objects.requireNonNull(animalsSkuDetails.get("animal_zebra"))).build());
        }
    }

    @Override
    public void onSettingsFragmentInteractionBackClicked() {
        onBackPressed();
    }

    @Override
    public void onShopFragmentInteractionBackClicked() {
        onBackPressed();
    }
    
    @Override
    public void onShopFragmentInteractionPurchaseOptionClicked(int purchaseOptionViewId) {
        if (purchaseOptionViewId == R.id.shop_feed_level1_constraint_layout
                || purchaseOptionViewId == R.id.shop_feed_level1_purchase_button) {
            Toast.makeText(MainActivity.this, "Shop Option 1 Clicked", Toast.LENGTH_SHORT).show();
        } else if (purchaseOptionViewId == R.id.shop_feed_level2_constraint_layout
                || purchaseOptionViewId == R.id.shop_feed_level2_purchase_button) {
            Toast.makeText(MainActivity.this, "Shop Option 2 Clicked", Toast.LENGTH_SHORT).show();
        } else if (purchaseOptionViewId == R.id.shop_feed_level3_constraint_layout
                || purchaseOptionViewId == R.id.shop_feed_level3_purchase_button) {
            Toast.makeText(MainActivity.this, "Shop Option 3 Clicked", Toast.LENGTH_SHORT).show();
        } else if (purchaseOptionViewId == R.id.shop_feed_level4_constraint_layout
                || purchaseOptionViewId == R.id.shop_feed_level4_purchase_button) {
            Toast.makeText(MainActivity.this, "Shop Option 4 Clicked", Toast.LENGTH_SHORT).show();
        }
    }
}