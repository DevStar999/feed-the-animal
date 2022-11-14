package com.nerdcoredevelopment.inappbillingdemo;

import android.annotation.SuppressLint;
import android.app.Application;
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
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nerdcoredevelopment.inappbillingdemo.dialogs.GameExitDialog;
import com.nerdcoredevelopment.inappbillingdemo.fragment.FarmerFragment;
import com.nerdcoredevelopment.inappbillingdemo.fragment.FeedingFragment;
import com.nerdcoredevelopment.inappbillingdemo.fragment.InfoFragment;
import com.nerdcoredevelopment.inappbillingdemo.fragment.NavigationFragment;
import com.nerdcoredevelopment.inappbillingdemo.fragment.SettingsFragment;
import com.nerdcoredevelopment.inappbillingdemo.fragment.ShopFragment;
import com.nerdcoredevelopment.inappbillingdemo.MyApplication.OnShowAdCompleteListener;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO -> Among the error code block of In-App Billing handle internet connectivity related issues
/* TODO -> After giving entitlement to the rewards after purchase, the changes don't reflect by themselves. They have to be
           triggered by something like a button click etc.
*/
/* TODO -> Whenever we would look into in detail how updates work as in what happens to stored data, settings etc. when a
           user updates the app to the latest version, we need to ensure that the In-App Billing code is still working fine
           without running into any problems
*/
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
    private BillingClient billingClient;
    private AdRequest adRequest;
    private InterstitialAd interstitialAd;
    private RewardedAd rewardedAd;
    private int rewardedAdHayUnitsReward;

    private void initialise() {
        sharedPreferences = getSharedPreferences("com.nerdcoredevelopment.inappbillingdemo", Context.MODE_PRIVATE);
        gson = new Gson();
        hayUnitsReward = new HashMap<>() {{
            put("hay_level1_v2", 50); put("hay_level2_v2", 100);
            put("hay_level3_v2", 250); put("hay_level4_v2", 500);
        }};
        adRequest = new AdRequest.Builder().build();
        rewardedAdHayUnitsReward = 10;
    }

    private void setupBillingClient() {
        PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
                if ((billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED
                        || billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) && list != null) {
                    for (Purchase purchase : list) {
                        verifyPurchase(purchase, purchase.getSkus().get(0));
                    }
                } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                    // TODO -> Show purchase failed message
                }
            }
        };
        billingClient = BillingClient.newBuilder(this).setListener(purchasesUpdatedListener)
                .enablePendingPurchases().build();
        connectToGooglePlayBilling();
    }

    private void setupBillingClientOnResume() {
        billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP,
            new PurchasesResponseListener() {
                @Override
                public void onQueryPurchasesResponse(@NonNull BillingResult billingResult, @NonNull List<Purchase> list) {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED
                            || billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        for (Purchase purchase : list) {
                            verifyPurchase(purchase, purchase.getSkus().get(0));
                        }
                    } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                        // TODO -> Show purchase failed message
                    }
                }
            }
        );
    }

    private void connectToGooglePlayBilling() {
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {
                connectToGooglePlayBilling();
            }
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    getProductDetails();
                }
            }
        });
    }

    private void getProductDetails() {
        // If we have the SkuDetails for all the hay levels and the 3 locked animals then return
        if (sharedPreferences.getBoolean("areHaySkuDetailsSaved", false)
                && sharedPreferences.getBoolean("areAnimalSkuDetailsSaved", false)) {
            return;
        }

        List<String> productIds = new ArrayList<>();
        for (int level = 1; level <= 4; level++) {
            productIds.add("hay_level" + level + "_v2");
        }
        productIds.add("animal_horse_v2");
        productIds.add("animal_reindeer_v2");
        productIds.add("animal_zebra_v2");

        SkuDetailsParams getProductDetailsQuery = SkuDetailsParams.newBuilder().setSkusList(productIds)
                .setType(BillingClient.SkuType.INAPP).build();
        SkuDetailsResponseListener skuDetailsResponseListener = new SkuDetailsResponseListener() {
            @Override
            public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> list) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    if (list != null && list.size() > 0) {
                        if (!sharedPreferences.getBoolean("areHaySkuDetailsSaved", false)) {
                            Map<String, SkuDetails> hayLevelsSkuDetails = new HashMap<>();
                            for (int index = 0; index < list.size(); index++) {
                                SkuDetails currentItem = list.get(index); String productId = currentItem.getSku();
                                for (int level = 1; level <= 4; level++) {
                                    String skuString = "hay_level" + level + "_v2";
                                    if (productId.equals(skuString)) {
                                        hayLevelsSkuDetails.put(productId, currentItem); break;
                                    }
                                }
                            }
                            List<String> hayItemPrices = new ArrayList<>();
                            for (int level = 1; level <= 4; level++) {
                                String skuString = "hay_level" + level + "_v2";
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
                        if (!sharedPreferences.getBoolean("areAnimalSkuDetailsSaved", false)) {
                            Map<String, SkuDetails> animalSkuDetails = new HashMap<>();
                            for (int index = 0; index < list.size(); index++) {
                                SkuDetails currentItem = list.get(index); String productId = currentItem.getSku();
                                if (productId.equals("animal_horse_v2")
                                        || productId.equals("animal_reindeer_v2")
                                        || productId.equals("animal_zebra_v2")) {
                                    animalSkuDetails.put(currentItem.getSku(), currentItem);
                                }
                            }
                            if (animalSkuDetails.size() == 3) {
                                sharedPreferences.edit().putString("animalSkuDetails", gson.toJson(animalSkuDetails)).apply();
                                sharedPreferences.edit().putBoolean("areAnimalSkuDetailsSaved", true).apply();
                            }
                        }
                    } else {
                        // TODO -> Purchase items were not found
                    }
                } else {
                    // TODO -> This code block means, BillingResponse is not OK
                }
            }
        };

        billingClient.querySkuDetailsAsync(getProductDetailsQuery, skuDetailsResponseListener);
    }

    /**
     * Verifies that the purchase was signed correctly for this developer's public key.
     * Note: It's strongly recommended to perform such check on your backend since hackers can
     * replace this method with "constant true" if they decompile/rebuild your app.
     */
    private boolean verifyValidSignature(String signedData, String signature) {
        try {
            String base64Key = "<Add your key here>";
            return Security.verifyPurchase(base64Key, signedData, signature);
        } catch (IOException e) {
            return false;
        }
    }

    private void updateHayUnits(int stockLeft) {
        sharedPreferences.edit().putInt("stockLeft", stockLeft).apply();
        List<Fragment> fragments = new ArrayList<>(getSupportFragmentManager().getFragments());
        for (int index = 0; index < fragments.size(); index++) {
            Fragment currentFragment = fragments.get(index);
            if (currentFragment != null && currentFragment.getTag() != null
                    && !currentFragment.getTag().isEmpty()) {
                if (currentFragment.getTag().equals("FEEDING_FRAGMENT")) {
                    ((FeedingFragment) currentFragment).updateHayStockFeedingFragment(stockLeft);
                } else if (currentFragment.getTag().equals("SHOP_FRAGMENT")) {
                    ((ShopFragment) currentFragment).updateHayStockShopFragment(stockLeft);
                }
            }
        }
    }

    private void giveHayUnitsReward(String productId, int purchaseQuantity) {
        int stockLeft = sharedPreferences.getInt("stockLeft", 20);
        stockLeft += hayUnitsReward.get(productId) * purchaseQuantity;
        updateHayUnits(stockLeft);
    }

    private void giveAnimalAccessReward(String productId) {
        if (productId.equals("animal_horse_v2")) {
            sharedPreferences.edit().putBoolean("animalHorseIsUnlocked", true).apply();
        } else if (productId.equals("animal_reindeer_v2")) {
            sharedPreferences.edit().putBoolean("animalReindeerIsUnlocked", true).apply();
        } else if (productId.equals("animal_zebra_v2")) {
            sharedPreferences.edit().putBoolean("animalZebraIsUnlocked", true).apply();
        }
        List<Fragment> fragments = new ArrayList<>(getSupportFragmentManager().getFragments());
        for (int index = 0; index < fragments.size(); index++) {
            Fragment currentFragment = fragments.get(index);
            if (currentFragment != null && currentFragment.getTag() != null
                    && !currentFragment.getTag().isEmpty()) {
                if (currentFragment.getTag().equals("FEEDING_FRAGMENT") ) {
                    if (productId.equals("animal_horse_v2")) {
                        ((FeedingFragment) currentFragment).unlockAccessToAnimalHorse();
                    } else if (productId.equals("animal_reindeer_v2")) {
                        ((FeedingFragment) currentFragment).unlockAccessToAnimalReindeer();
                    } else if (productId.equals("animal_zebra_v2")) {
                        ((FeedingFragment) currentFragment).unlockAccessToAnimalZebra();
                    }
                }
            }
        }
    }

    private void verifyPurchase(Purchase purchase, String productId) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            if (!verifyValidSignature(purchase.getOriginalJson(), purchase.getSignature())) {
                // Invalid purchase, show error to the user
                // TODO -> Show an error message to the user in some way or the other
                return;
            }

            boolean isConsumableRecurring = false;
            for (int level = 1; level <= 4; level++) {
                String skuString = "hay_level" + level + "_v2";
                if (productId.equals(skuString)) {
                   isConsumableRecurring = true; break;
                }
            }

            if (isConsumableRecurring) { // Handling recurring consumables in this 'if' block
                if (!purchase.isAcknowledged()) { // Purchase is not acknowledged yet
                    ConsumeParams consumeParams = ConsumeParams.newBuilder().setPurchaseToken(purchase.getPurchaseToken()).build();
                    ConsumeResponseListener consumeResponseListener = new ConsumeResponseListener() {
                        @Override
                        public void onConsumeResponse(@NonNull BillingResult billingResult, @NonNull String s) {
                            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                                for (int level = 1; level <= 4; level++) {
                                    String hayLevel = "hay_level" + level + "_v2";
                                    if (productId.equals(hayLevel)) {
                                        giveHayUnitsReward(productId, purchase.getQuantity());
                                    }
                                }
                            } else {
                                // TODO -> This code block means, BillingResponse is not OK
                            }
                        }
                    };
                    billingClient.consumeAsync(consumeParams, consumeResponseListener);
                }
            } else { // Handling non-recurring consumables in this 'else' block
                if (!purchase.isAcknowledged()) {
                    AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                            .setPurchaseToken(purchase.getPurchaseToken()).build();
                    AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener = new AcknowledgePurchaseResponseListener() {
                        @Override
                        public void onAcknowledgePurchaseResponse(@NonNull BillingResult billingResult) {
                            if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                                giveAnimalAccessReward(productId);
                            } else {
                                // TODO -> This code block means, BillingResponse is not OK
                            }
                        }
                    };
                    billingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
                } else { // Means consumable is valid and also acknowledged
                    if ((productId.equals("animal_horse_v2")
                            && sharedPreferences.getBoolean("animalHorseIsUnlocked", false))
                            || (productId.equals("animal_reindeer_v2")
                            && sharedPreferences.getBoolean("animalReindeerIsUnlocked", false))
                            || (productId.equals("animal_zebra_v2")
                            && sharedPreferences.getBoolean("animalZebraIsUnlocked", false))) {
                        return;
                    }
                    giveAnimalAccessReward(productId);
                }
            }
        }
    }

    private void showAppOpenAd() {
        Application application = getApplication();
        if ((application instanceof MyApplication)) {
            ((MyApplication) application).showAdIfAvailable(this, new OnShowAdCompleteListener() {
                @Override
                public void onShowAdComplete() {}
            });
        }
    }

    private void loadInterstitialAd() {
        if (interstitialAd == null) {
            InterstitialAd.load(this,"ca-app-pub-4247468904518611/7765090821", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The interstitialAd reference will be null until an ad is loaded.
                        MainActivity.this.interstitialAd = interstitialAd;
                        interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                            @Override
                            public void onAdDismissedFullScreenContent() { // Called when ad is dismissed. Set the ad
                                // reference to null so to not show the ad a second time.
                                MainActivity.this.interstitialAd = null;
                                loadInterstitialAd();
                            }
                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) { // When ad fails to show.
                                MainActivity.this.interstitialAd = null;
                            }
                            @Override
                            public void onAdImpression() {/* Called when an impression is recorded for an ad. */}
                            @Override
                            public void onAdShowedFullScreenContent() {/* Called when ad is shown. */}
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) { // Handle the error
                        interstitialAd = null;
                        loadInterstitialAd();
                    }
                }
            );
        }
    }

    private void showInterstitialAd() {
        if (interstitialAd != null) {
            interstitialAd.show(MainActivity.this);
        } else {
            loadInterstitialAd();
        }
    }

    private void loadRewardedAd() {
        if (rewardedAd == null) {
            RewardedAd.load(this, "ca-app-pub-4247468904518611/5685722397",
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        MainActivity.this.rewardedAd = rewardedAd;
                        MainActivity.this.rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() { // Called when ad is dismissed. Set the ad
                                // reference to null so you don't show the ad a 2nd time.
                                MainActivity.this.rewardedAd = null;
                                loadRewardedAd(); // Pre-load the reward ad for the next time
                            }
                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) { // Called when ad fails to show.
                                MainActivity.this.rewardedAd = null;
                            }
                        });
                        FeedingFragment feedingFragment = (FeedingFragment) getSupportFragmentManager()
                                .findFragmentByTag("FEEDING_FRAGMENT");
                        if (feedingFragment != null) {
                            feedingFragment.showRewardedAdOption();
                        }
                    }
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) { // Handle the error.
                        rewardedAd = null;
                        loadRewardedAd();
                    }
                }
            );
        }
    }

    private void showRewardedAd() {
        if (rewardedAd != null) {
            rewardedAd.show(this, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    int stockLeft = sharedPreferences.getInt("stockLeft", 20);
                    stockLeft += rewardedAdHayUnitsReward;
                    updateHayUnits(stockLeft);
                }
            });
        } else {
            // The toast message string contains an acceptable message for the user if reward ad hasn't loaded yet.
            Toast.makeText(this, "This ad is currently unavailable in your area", Toast.LENGTH_SHORT).show();
            loadRewardedAd();
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        showAppOpenAd();

        loadRewardedAd();

        NavigationFragment navigationFragment = new NavigationFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.navigation_fragment_container_main_activity, navigationFragment, "NAVIGATION_FRAGMENT")
                .commit();

        setupBillingClient();
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

        setupBillingClientOnResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(billingClient!=null){
            billingClient.endConnection();
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) { // Back button was pressed from MainActivity
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
        } else { // Back button was pressed from fragment
            int countOfFragments = getSupportFragmentManager().getFragments().size();
            if (countOfFragments > 0) {
                Fragment topMostFragment = getSupportFragmentManager().getFragments().get(countOfFragments - 1);
                if (topMostFragment != null && topMostFragment.getTag() != null && !topMostFragment.getTag().isEmpty()) {
                    if (topMostFragment.getTag().equals("FARMER_FRAGMENT")) {
                        showInterstitialAd();
                    }
                }
            }
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

        showInterstitialAd();
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
    public void onFarmerFragmentInteractionLoadBannerAd(AdView bannerAdView) {
        bannerAdView.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() { // Code to be executed when the user clicks on an ad.
                super.onAdClicked();
                // TODO -> Implement logic to prevent users from clicking the ad too many times in a short period of time
                /* Note: If the user clicks on ad too many times in a short period of time, Google may suspect this as fishy
                         behaviour and this could lead to serious consequences like AdMob account suspension or termination
                         OR suspension or termination of App from Play Store. However, we need not worry too much about this
                         as well, since at first we will be warned by Google about this.
                */
            }
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) { // Code to be executed when an ad request fails.
                super.onAdFailedToLoad(loadAdError);
                bannerAdView.loadAd(adRequest);
            }
        });
        bannerAdView.loadAd(adRequest);
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
    public void onFeedingFragmentInteractionHasRewardedAdLoaded() {
        if (rewardedAd != null) {
            FeedingFragment feedingFragment = (FeedingFragment) getSupportFragmentManager()
                    .findFragmentByTag("FEEDING_FRAGMENT");
            if (feedingFragment != null) {
                feedingFragment.showRewardedAdOption();
            }
        }
    }

    @Override
    public void onFeedingFragmentInteractionBackClicked() {
        onBackPressed();
    }

    @Override
    public void onFeedingFragmentShowRewardedAd() {
        showRewardedAd();
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
            billingClient.launchBillingFlow(this, BillingFlowParams.newBuilder()
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
            if (hayLevelsSkuDetails.containsKey("hay_level1_v2")) {
                billingClient.launchBillingFlow(this, BillingFlowParams.newBuilder()
                        .setSkuDetails(hayLevelsSkuDetails.get("hay_level1_v2")).build());
            }
        } else if (purchaseOptionViewId == R.id.shop_feed_level2_constraint_layout
                || purchaseOptionViewId == R.id.shop_feed_level2_purchase_button) {
            if (hayLevelsSkuDetails.containsKey("hay_level2_v2")) {
                billingClient.launchBillingFlow(this, BillingFlowParams.newBuilder()
                        .setSkuDetails(hayLevelsSkuDetails.get("hay_level2_v2")).build());
            }
        } else if (purchaseOptionViewId == R.id.shop_feed_level3_constraint_layout
                || purchaseOptionViewId == R.id.shop_feed_level3_purchase_button) {
            if (hayLevelsSkuDetails.containsKey("hay_level3_v2")) {
                billingClient.launchBillingFlow(this, BillingFlowParams.newBuilder()
                        .setSkuDetails(hayLevelsSkuDetails.get("hay_level3_v2")).build());
            }
        } else if (purchaseOptionViewId == R.id.shop_feed_level4_constraint_layout
                || purchaseOptionViewId == R.id.shop_feed_level4_purchase_button) {
            if (hayLevelsSkuDetails.containsKey("hay_level4_v2")) {
                billingClient.launchBillingFlow(this, BillingFlowParams.newBuilder()
                        .setSkuDetails(hayLevelsSkuDetails.get("hay_level4_v2")).build());
            }
        }
    }
}