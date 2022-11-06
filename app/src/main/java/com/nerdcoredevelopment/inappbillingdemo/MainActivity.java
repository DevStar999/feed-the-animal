package com.nerdcoredevelopment.inappbillingdemo;

import android.annotation.SuppressLint;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nerdcoredevelopment.inappbillingdemo.dialogs.GameExitDialog;
import com.nerdcoredevelopment.inappbillingdemo.fragment.FarmerFragment;
import com.nerdcoredevelopment.inappbillingdemo.fragment.FeedingFragment;
import com.nerdcoredevelopment.inappbillingdemo.fragment.InfoFragment;
import com.nerdcoredevelopment.inappbillingdemo.fragment.NavigationFragment;
import com.nerdcoredevelopment.inappbillingdemo.fragment.SettingsFragment;
import com.nerdcoredevelopment.inappbillingdemo.fragment.ShopFragment;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO -> Among the error code block of In-App Billing handle internet connectivity related issues
public class MainActivity extends AppCompatActivity implements
        InfoFragment.OnInfoFragmentInteractionListener,
        NavigationFragment.OnNavigationFragmentInteractionListener,
        FarmerFragment.OnFarmerFragmentInteractionListener,
        FeedingFragment.OnFeedingFragmentInteractionListener,
        SettingsFragment.OnSettingsFragmentInteractionListener,
        ShopFragment.OnShopFragmentInteractionListener {
    private SharedPreferences sharedPreferences;
    private Gson gson;
    private Map<String, Integer> hayUnitsReward;
    private BillingClient recurringConsumablesBillingClient;
    private BillingClient nonRecurringConsumablesBillingClient;

    private void initialise() {
        sharedPreferences = getSharedPreferences("com.nerdcoredevelopment.inappbillingdemo", Context.MODE_PRIVATE);
        gson = new Gson();
        hayUnitsReward = new HashMap<>() {{
            put("hay_level1", 50); put("hay_level2", 100);
            put("hay_level3", 250); put("hay_level4", 500);
        }};
    }

    private void setupBillingClients() {
        PurchasesUpdatedListener purchasesUpdatedListenerForRecurringConsumables = (billingResult, list) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                    && list != null) {
                for (Purchase purchase : list) {
                    if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged()) {
                        verifyPurchase(purchase, true);
                    }
                }
            }
        };
        recurringConsumablesBillingClient = BillingClient.newBuilder(this)
                .setListener(purchasesUpdatedListenerForRecurringConsumables)
                .enablePendingPurchases().build();
        connectToGooglePlayBillingForRecurringConsumables();

        PurchasesUpdatedListener purchasesUpdatedListenerForNonRecurringConsumables = (billingResult, list) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                    && list != null) {
                for (Purchase purchase : list) {
                    if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged()) {
                        verifyPurchase(purchase, false);
                    }
                }
            }
        };
        nonRecurringConsumablesBillingClient = BillingClient.newBuilder(this)
                .setListener(purchasesUpdatedListenerForNonRecurringConsumables)
                .enablePendingPurchases().build();
        connectToGooglePlayBillingForNonRecurringConsumables();
    }

    private void setupBillingClientsOnResume() {
        recurringConsumablesBillingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP,
            new PurchasesResponseListener() {
                @Override
                public void onQueryPurchasesResponse(@NonNull BillingResult billingResult,
                                                     @NonNull List<Purchase> list) {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        for (Purchase purchase : list) {
                            if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged()) {
                                verifyPurchase(purchase, true);
                            }
                        }
                    }
                }
            }
        );

        nonRecurringConsumablesBillingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP,
            new PurchasesResponseListener() {
                @Override
                public void onQueryPurchasesResponse(@NonNull BillingResult billingResult,
                                                     @NonNull List<Purchase> list) {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        for (Purchase purchase : list) {
                            if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged()) {
                                verifyPurchase(purchase, false);
                            }
                        }
                    }
                }
            }
        );
    }

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

    private void getProductDetailsOfRecurringConsumables() {
        // If we have the SkuDetails for all the hay levels then return
        if (sharedPreferences.getBoolean("areHaySkuDetailsSaved", false)) {
            return;
        }

        List<String> productIds = new ArrayList<>();
        for (int level = 1; level <= 4; level++) {
            productIds.add("hay_level" + level);
        }

        SkuDetailsParams getProductDetailsQuery = SkuDetailsParams.newBuilder().setSkusList(productIds)
                .setType(BillingClient.SkuType.INAPP).build();
        SkuDetailsResponseListener skuDetailsResponseListener = new SkuDetailsResponseListener() {
            @Override
            public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> list) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK &&
                        list != null) {
                    if (!sharedPreferences.getBoolean("areHaySkuDetailsSaved", false)) {
                        Map<String, SkuDetails> hayLevelsSkuDetails = new HashMap<>();
                        for (int index = 0; index < list.size(); index++) {
                            SkuDetails currentItem = list.get(index);
                            hayLevelsSkuDetails.put(currentItem.getSku(), currentItem);
                        }
                        List<String> hayItemPrices = new ArrayList<>();
                        for (int level = 1; level <= 4; level++) {
                            String skuString = "hay_level" + level;
                            if (hayLevelsSkuDetails.containsKey(skuString)) {
                                if (!hayLevelsSkuDetails.get(skuString).getPrice().isEmpty()) {
                                    hayItemPrices.add(hayLevelsSkuDetails.get(skuString).getPrice());
                                }
                            }
                        }
                        if (hayItemPrices.size() == 4) {
                            sharedPreferences.edit().putString("hayItemPrices", gson.toJson(hayItemPrices)).apply();
                            sharedPreferences.edit().putString("hayLevelsSkuDetails", gson.toJson(hayLevelsSkuDetails)).apply();
                            sharedPreferences.edit().putBoolean("areHaySkuDetailsSaved", true).apply();
                            List<Fragment> fragments = new ArrayList<>(getSupportFragmentManager().getFragments());
                            for (int index = 0; index < fragments.size(); index++) {
                                Fragment currentFragment = fragments.get(index);
                                if (currentFragment != null && currentFragment.getTag() != null
                                        && !currentFragment.getTag().isEmpty()) {
                                    if (currentFragment.getTag().equals("SHOP_FRAGMENT")) {
                                        ((ShopFragment) currentFragment).updateHayItemPrices(hayItemPrices);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        };

        recurringConsumablesBillingClient.querySkuDetailsAsync(getProductDetailsQuery, skuDetailsResponseListener);
    }

    private void getProductDetailsOfNonRecurringConsumables() {
        // If we have the SkuDetails for the 3 locked animals then return
        if (sharedPreferences.getBoolean("areAnimalSkuDetailsSaved", false)) {
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
                    if (!sharedPreferences.getBoolean("areAnimalSkuDetailsSaved", false)) {
                        Map<String, SkuDetails> animalSkuDetails = new HashMap<>();
                        for (int index = 0; index < list.size(); index++) {
                            SkuDetails currentItem = list.get(index);
                            animalSkuDetails.put(currentItem.getSku(), currentItem);
                        }
                        if (animalSkuDetails.size() == 3) {
                            sharedPreferences.edit().putString("animalSkuDetails", gson.toJson(animalSkuDetails)).apply();
                            sharedPreferences.edit().putBoolean("areAnimalSkuDetailsSaved", true).apply();
                        }
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
                        if (!isPurchaseRecurring) { // Code for Non-Recurring Consumables
                            AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                                    .setPurchaseToken(purchase.getPurchaseToken()).build();
                            AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener
                                = billingResult -> {
                                if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                                    String productId = purchase.getSkus().get(0);
                                    List<Fragment> fragments = new ArrayList<>(getSupportFragmentManager().getFragments());
                                    for (int index = 0; index < fragments.size(); index++) {
                                        Fragment currentFragment = fragments.get(index);
                                        if (currentFragment != null && currentFragment.getTag() != null
                                                && !currentFragment.getTag().isEmpty()) {
                                            if (currentFragment.getTag().equals("FEEDING_FRAGMENT") ) {
                                                if (productId.equals("animal_horse")) {
                                                    sharedPreferences.edit()
                                                            .putBoolean("animalHorseIsUnlocked", true).apply();
                                                    ((FeedingFragment) currentFragment).unlockAccessToAnimalHorse();
                                                } else if (productId.equals("animal_reindeer")) {
                                                    sharedPreferences.edit()
                                                            .putBoolean("animalReindeerIsUnlocked", true).apply();
                                                    ((FeedingFragment) currentFragment).unlockAccessToAnimalReindeer();
                                                } else if (productId.equals("animal_zebra")) {
                                                    sharedPreferences.edit()
                                                            .putBoolean("animalZebraIsUnlocked", true).apply();
                                                    ((FeedingFragment) currentFragment).unlockAccessToAnimalZebra();
                                                }
                                            }
                                        }
                                    }
                                }
                            };
                            nonRecurringConsumablesBillingClient.acknowledgePurchase(acknowledgePurchaseParams,
                                    acknowledgePurchaseResponseListener);
                        } else { // Code for Recurring Consumables
                            ConsumeParams consumeParams = ConsumeParams.newBuilder().
                                    setPurchaseToken(purchase.getPurchaseToken()).build();
                            ConsumeResponseListener consumeResponseListener = (billingResult, s) -> {
                                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                                    String productId = purchase.getSkus().get(0);
                                    for (int level = 1; level <= 4; level++) {
                                        String hayLevel = "hay_level" + level;
                                        if (productId.equals(hayLevel)) {
                                            int stockLeft = sharedPreferences.getInt("stockLeft", 20);
                                            stockLeft += hayUnitsReward.get(hayLevel) * purchase.getQuantity();
                                            sharedPreferences.edit().putInt("stockLeft", stockLeft).apply();
                                            List<Fragment> fragments =
                                                    new ArrayList<>(getSupportFragmentManager().getFragments());
                                            for (int index = 0; index < fragments.size(); index++) {
                                                Fragment currentFragment = fragments.get(index);
                                                if (currentFragment != null && currentFragment.getTag() != null
                                                        && !currentFragment.getTag().isEmpty()) {
                                                    if (currentFragment.getTag().equals("FEEDING_FRAGMENT")) {
                                                        ((FeedingFragment) currentFragment)
                                                                .updateHayStockFeedingFragment(stockLeft);
                                                    } else if (currentFragment.getTag().equals("SHOP_FRAGMENT")) {
                                                        ((ShopFragment) currentFragment)
                                                                .updateHayStockShopFragment(stockLeft);
                                                    }
                                                }
                                            }
                                            break;
                                        }
                                    }
                                }
                            };
                            recurringConsumablesBillingClient.consumeAsync(consumeParams, consumeResponseListener);
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

        NavigationFragment navigationFragment = new NavigationFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.navigation_fragment_container_main_activity, navigationFragment, "NAVIGATION_FRAGMENT")
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
        // If InfoFragment was opened and is currently on top, then return
        int countOfFragments = getSupportFragmentManager().getFragments().size();
        if (countOfFragments > 0) {
            Fragment topMostFragment = getSupportFragmentManager().getFragments().get(countOfFragments-1);
            if (topMostFragment != null && topMostFragment.getTag() != null && !topMostFragment.getTag().isEmpty()
                    && topMostFragment.getTag().equals("INFO_FRAGMENT")) {
                return;
            }
        }

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
        // If FarmerFragment was opened and is currently on top, then return
        int countOfFragments = getSupportFragmentManager().getFragments().size();
        if (countOfFragments > 0) {
            Fragment topMostFragment = getSupportFragmentManager().getFragments().get(countOfFragments-1);
            if (topMostFragment != null && topMostFragment.getTag() != null && !topMostFragment.getTag().isEmpty()
                    && topMostFragment.getTag().equals("FARMER_FRAGMENT")) {
                return;
            }
        }

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
        // If FeedingFragment was opened and is currently on top, then return
        int countOfFragments = getSupportFragmentManager().getFragments().size();
        if (countOfFragments > 0) {
            Fragment topMostFragment = getSupportFragmentManager().getFragments().get(countOfFragments-1);
            if (topMostFragment != null && topMostFragment.getTag() != null && !topMostFragment.getTag().isEmpty()
                    && topMostFragment.getTag().equals("FEEDING_FRAGMENT")) {
                return;
            }
        }

        FeedingFragment feedingFragment = new FeedingFragment();
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
        // If SettingsFragment was opened and is currently on top, then return
        int countOfFragments = getSupportFragmentManager().getFragments().size();
        if (countOfFragments > 0) {
            Fragment topMostFragment = getSupportFragmentManager().getFragments().get(countOfFragments-1);
            if (topMostFragment != null && topMostFragment.getTag() != null && !topMostFragment.getTag().isEmpty()
                    && topMostFragment.getTag().equals("SETTINGS_FRAGMENT")) {
                return;
            }
        }

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
        // If ShopFragment was opened and is currently on top, then return
        int countOfFragments = getSupportFragmentManager().getFragments().size();
        if (countOfFragments > 0) {
            Fragment topMostFragment = getSupportFragmentManager().getFragments().get(countOfFragments-1);
            if (topMostFragment != null && topMostFragment.getTag() != null && !topMostFragment.getTag().isEmpty()
                    && topMostFragment.getTag().equals("SHOP_FRAGMENT")) {
                return;
            }
        }

        ShopFragment shopFragment;
        if (sharedPreferences.getBoolean("areHaySkuDetailsSaved", false)) {
            String jsonRetrieveHayItemPrices = sharedPreferences.getString("hayItemPrices",
                    gson.toJson(new ArrayList<>()));
            Type typeHayItemPrices = new TypeToken<List<String>>(){}.getType();
            shopFragment = ShopFragment.newInstance(gson.fromJson(jsonRetrieveHayItemPrices, typeHayItemPrices));
        } else {
            shopFragment = new ShopFragment();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right,
                R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.add(R.id.full_screen_fragment_container_main_activity,
                shopFragment, "SHOP_FRAGMENT").commit();
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
        // If ShopFragment was opened and is currently on top, then return
        int countOfFragments = getSupportFragmentManager().getFragments().size();
        if (countOfFragments > 0) {
            Fragment topMostFragment = getSupportFragmentManager().getFragments().get(countOfFragments-1);
            if (topMostFragment != null && topMostFragment.getTag() != null && !topMostFragment.getTag().isEmpty()
                    && topMostFragment.getTag().equals("SHOP_FRAGMENT")) {
                return;
            }
        }

        ShopFragment shopFragment;
        if (sharedPreferences.getBoolean("areHaySkuDetailsSaved", false)) {
            String jsonRetrieveHayItemPrices = sharedPreferences.getString("hayItemPrices",
                    gson.toJson(new ArrayList<>()));
            Type typeHayItemPrices = new TypeToken<List<String>>(){}.getType();
            shopFragment = ShopFragment.newInstance(gson.fromJson(jsonRetrieveHayItemPrices, typeHayItemPrices));
        } else {
            shopFragment = new ShopFragment();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right,
                R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.add(R.id.full_screen_fragment_container_main_activity,
                shopFragment, "SHOP_FRAGMENT").commit();
    }

    @Override
    public void onFeedingFragmentInteractionAccessLockedAnimal(String animalKey) {
        if (!sharedPreferences.getBoolean("areAnimalSkuDetailsSaved", false)) {
            return;
        }

        String jsonRetrieveAnimalSkuDetails = sharedPreferences.getString("animalSkuDetails",
                gson.toJson(new HashMap<>()));
        Type typeAnimalSkuDetails = new TypeToken<Map<String, SkuDetails>>(){}.getType();
        Map<String, SkuDetails> animalSkuDetails = new HashMap<>(gson.fromJson(jsonRetrieveAnimalSkuDetails,
                typeAnimalSkuDetails));

        if (animalSkuDetails.containsKey(animalKey)) {
            nonRecurringConsumablesBillingClient.launchBillingFlow(this, BillingFlowParams.newBuilder()
                            .setSkuDetails(animalSkuDetails.get(animalKey)).build());
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
        if (!sharedPreferences.getBoolean("areHaySkuDetailsSaved", false)) {
            return;
        }

        String jsonRetrieveHayLevelsSkuDetails = sharedPreferences.getString("hayLevelsSkuDetails",
                gson.toJson(new HashMap<>()));
        Type typeHayLevelsSkuDetails = new TypeToken<Map<String, SkuDetails>>(){}.getType();
        Map<String, SkuDetails> hayLevelsSkuDetails = new HashMap<>(gson.fromJson(jsonRetrieveHayLevelsSkuDetails,
                typeHayLevelsSkuDetails));

        if (purchaseOptionViewId == R.id.shop_feed_level1_constraint_layout
                || purchaseOptionViewId == R.id.shop_feed_level1_purchase_button) {
            if (hayLevelsSkuDetails.containsKey("hay_level1")) {
                recurringConsumablesBillingClient.launchBillingFlow(this, BillingFlowParams.newBuilder()
                        .setSkuDetails(hayLevelsSkuDetails.get("hay_level1")).build());
            }
        } else if (purchaseOptionViewId == R.id.shop_feed_level2_constraint_layout
                || purchaseOptionViewId == R.id.shop_feed_level2_purchase_button) {
            if (hayLevelsSkuDetails.containsKey("hay_level2")) {
                recurringConsumablesBillingClient.launchBillingFlow(this, BillingFlowParams.newBuilder()
                        .setSkuDetails(hayLevelsSkuDetails.get("hay_level2")).build());
            }
        } else if (purchaseOptionViewId == R.id.shop_feed_level3_constraint_layout
                || purchaseOptionViewId == R.id.shop_feed_level3_purchase_button) {
            if (hayLevelsSkuDetails.containsKey("hay_level3")) {
                recurringConsumablesBillingClient.launchBillingFlow(this, BillingFlowParams.newBuilder()
                        .setSkuDetails(hayLevelsSkuDetails.get("hay_level3")).build());
            }
        } else if (purchaseOptionViewId == R.id.shop_feed_level4_constraint_layout
                || purchaseOptionViewId == R.id.shop_feed_level4_purchase_button) {
            if (hayLevelsSkuDetails.containsKey("hay_level4")) {
                recurringConsumablesBillingClient.launchBillingFlow(this, BillingFlowParams.newBuilder()
                        .setSkuDetails(hayLevelsSkuDetails.get("hay_level4")).build());
            }
        }
    }
}