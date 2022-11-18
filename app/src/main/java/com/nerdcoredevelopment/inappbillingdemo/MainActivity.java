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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
import com.nerdcoredevelopment.inappbillingdemo.MyApplication.OnShowAdCompleteListener;
import com.nerdcoredevelopment.inappbillingdemo.dialogs.GameExitDialog;
import com.nerdcoredevelopment.inappbillingdemo.fragment.FarmerFragment;
import com.nerdcoredevelopment.inappbillingdemo.fragment.FeedingFragment;
import com.nerdcoredevelopment.inappbillingdemo.fragment.InfoFragment;
import com.nerdcoredevelopment.inappbillingdemo.fragment.NavigationFragment;
import com.nerdcoredevelopment.inappbillingdemo.fragment.SettingsFragment;
import com.nerdcoredevelopment.inappbillingdemo.fragment.ShopFragment;

import java.util.ArrayList;
import java.util.List;

// TODO -> Among the error code block of In-App Billing handle internet connectivity related issues
/* TODO -> After giving entitlement to the rewards after purchase, the changes don't reflect by themselves. They have to be
           triggered by something like a button click etc.
*/
/* TODO -> Whenever we would look into in detail how updates work as in what happens to stored data, settings etc. when a
           user updates the app to the latest version, we need to ensure that the In-App Billing code is still working fine
           without running into any problems
*/
/* TODO -> Revisit the Qonversion Sample code, when we will implement for subscriptions the following:
           (a) [Will probably come in handy when we want a multi-platform (Android, iOS etc.) app ] User auth using Firebase
           (b) [<Same comment as above>] Subscription tracking using the Qonversion mechanism for user id tracking
           (https://documentation.qonversion.io/docs/subscription-management-mode: Step 4 - Set User Id)
           (c) Send notifications to users using Qonversion Firebase integration to send automated notifications to users
           regarding their subscriptions using Qonversion [Here, most likely we will have to do Qonversion integrations to
           integrate Firebase with Qonversion, remember any Qonversion integrations will cost us money, so be mindful of it]
*/
/* TODO -> [Implement this TODO later on, when we reach the point where we want to implement subscriptions in the
           '2048 Champs' app]
           We make do some create our own push-notifications regarding subscriptions using Qonversion data which we get in
           code.
*/
/* TODO -> Set a max retry count & mechanism for the loading of things like any type of Ads; Qonversion offerings,
           permissions etc.
*/
/* TODO -> How to handle the situation when we change prices of the IAPs from Google Play Console without creating new
           products. We may need to think about this if we later decided that we don't want to load fresh prices and IAP
           related details from Qonversion and want to use some effective implementation of local storage.
*/
/* TODO -> Implement Qonversion Subscriptions for Farmer Fragment. Also follow up on the next of Qonversion, we have which
           we had written on board and have a clicked a photo of.
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
    private AdRequest adRequest;
    private InterstitialAd interstitialAd;
    private RewardedAd rewardedAd;
    private int rewardedAdHayUnitsReward;

    private void initialise() {
        sharedPreferences = getSharedPreferences("com.nerdcoredevelopment.inappbillingdemo", Context.MODE_PRIVATE);
        gson = new Gson();
        adRequest = new AdRequest.Builder().build();
        rewardedAdHayUnitsReward = 10;
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
            String testInterstitialAdUnitId = "ca-app-pub-3940256099942544/1033173712";
            String realInterstitialAdUnitId = "ca-app-pub-4247468904518611/7765090821";
            InterstitialAd.load(this, realInterstitialAdUnitId, adRequest,
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
            String testRewardedAdUnitId = "ca-app-pub-3940256099942544/5224354917";
            String realRewardedAdUnitId = "ca-app-pub-4247468904518611/5685722397";
            RewardedAd.load(this, realRewardedAdUnitId,
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

        if (getIntent().getBooleanExtra("comingFromIntroActivity", false)) {
            showAppOpenAd();
        }

        loadRewardedAd();

        NavigationFragment navigationFragment = new NavigationFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.navigation_fragment_container_main_activity, navigationFragment, "NAVIGATION_FRAGMENT")
                .commit();
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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

        ShopFragment shopFragment = new ShopFragment();
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

        ShopFragment shopFragment = new ShopFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right,
                R.anim.enter_from_right, R.anim.exit_to_right);
        transaction.addToBackStack(null);
        transaction.add(R.id.full_screen_fragment_container_main_activity,
                shopFragment, "SHOP_FRAGMENT").commit();
    }

    @Override
    public void onFeedingFragmentInteractionGiveAccessToAnimal(String qonversionId) {
        List<Fragment> fragments = new ArrayList<>(getSupportFragmentManager().getFragments());
        for (int index = 0; index < fragments.size(); index++) {
            Fragment currentFragment = fragments.get(index);
            if (currentFragment != null && currentFragment.getTag() != null
                    && !currentFragment.getTag().isEmpty()) {
                if (currentFragment.getTag().equals("FEEDING_FRAGMENT") ) {
                    if (qonversionId.equals("Horse")) {
                        ((FeedingFragment) currentFragment).unlockAccessToAnimalHorse();
                    } else if (qonversionId.equals("Reindeer")) {
                        ((FeedingFragment) currentFragment).unlockAccessToAnimalReindeer();
                    } else if (qonversionId.equals("Zebra")) {
                        ((FeedingFragment) currentFragment).unlockAccessToAnimalZebra();
                    }
                }
            }
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
    public void onShopFragmentUpdateHayUnits(int stockLeft) {
        updateHayUnits(stockLeft);
    }
}