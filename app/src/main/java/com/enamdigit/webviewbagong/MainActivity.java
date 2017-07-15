package com.enamdigit.webviewbagong;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.NativeExpressAdView;
import com.startapp.android.publish.ads.splash.SplashConfig;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.StartAppSDK;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int ITEMS_PER_AD = 4;
    private static final int NATIVE_EXPRESS_AD_HEIGHT = 150;

    private RecyclerView mRecyclerView;
    private List<Object> klikButton = new ArrayList<>();

    SharedPreferences appPreferences;
    boolean isAppInstalled = false;

    boolean internetStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addShortcut();

        internetStatus=isOnline();
        if(!internetStatus){
            notifNoOnline();
        }else{
            Toast.makeText(this, "WELCOME.. :)", Toast.LENGTH_SHORT).show();
        }

        // Startapp ------------------------------------------------------------------------------------
        StartAppSDK.init(this, getResources().getString(R.string.startapp_id), true);
        StartAppAd.showSplash(this, savedInstanceState,
                new SplashConfig()
                        .setTheme(SplashConfig.Theme.USER_DEFINED)
                        .setCustomScreen(R.layout.activity_splashscreen)
//                        .setLogo(R.drawable.splashscreen)
        );

        setContentView(R.layout.activity_main);

        // Admob ---------------------------------------------------------------------------------------
        // Banner Ads Admob

//        AdView mAdViewban = (AdView) findViewById(R.id.adView_banner);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdViewban.loadAd(adRequest);


        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        // TODO BAGIAN INI UNTUK DIEDIT SESUIAKAN DENGAN KONTEN ARTIKEL YANG MAU DIBUAT ----------------------

        klikButton.add(new MenuItemBagong("title01", "file:///android_asset/www/bagongkonten/halaman01.html"));
        klikButton.add(new MenuItemBagong("title02", "file:///android_asset/www/bagongkonten/halaman02.html"));
        klikButton.add(new MenuItemBagong("title03", "file:///android_asset/www/bagongkonten/halaman03.html"));
        klikButton.add(new MenuItemBagong("title04", "file:///android_asset/www/bagongkonten/halaman04.html"));
        klikButton.add(new MenuItemBagong("title05", "file:///android_asset/www/bagongkonten/halaman05.html"));
        klikButton.add(new MenuItemBagong("title06", "file:///android_asset/www/bagongkonten/halaman06.html"));
        klikButton.add(new MenuItemBagong("title07", "file:///android_asset/www/bagongkonten/halaman07.html"));
        klikButton.add(new MenuItemBagong("title08", "file:///android_asset/www/bagongkonten/halaman08.html"));
        klikButton.add(new MenuItemBagong("title09", "file:///android_asset/www/bagongkonten/halaman09.html"));
        klikButton.add(new MenuItemBagong("title10", "file:///android_asset/www/bagongkonten/halaman10.html"));


        // Update the RecyclerView item's list with menu items and Native Express ads.
//        addMenuItemsFromJson();
        addNativeExpressAds();
        setUpAndLoadNativeExpressAds();

        // Specify an adapter.
        RecyclerView.Adapter adapter = new BagongAdapter(this, klikButton);
        mRecyclerView.setAdapter(adapter);


// Banner Ads Admob
        AdView mAdViewban = (AdView) findViewById(R.id.adView_banner);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdViewban.loadAd(adRequest);


    }


    // ini method untuk startapp interstitial
    public void displayInterstitialStartapp() {
        StartAppAd.showAd(this);
    }

    // ini method untuk membuat shortcut otomatis di desktop android setelah install apk
    private void addShortcut() {
        appPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        isAppInstalled = appPreferences.getBoolean("isAppInstalled", false);
        if (!isAppInstalled) {
            Intent shortcutIntent = new Intent(getApplicationContext(), SplashActivity.class);
            shortcutIntent.setAction(Intent.ACTION_MAIN);
            Intent intent = new Intent();
            intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
            intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name));
            intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                    Intent.ShortcutIconResource.fromContext(getApplicationContext(),
                            R.mipmap.ic_launcher));

            intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
            getApplicationContext().sendBroadcast(intent);

            SharedPreferences.Editor editor = appPreferences.edit();
            editor.putBoolean("isAppInstalled", true);
            editor.commit();
        }

    }


    // Method Override untuk pop-up exit confirmation
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.this.finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    /**
     * Adds Native Express ads to the items list.
     */
    private void addNativeExpressAds() {

        // Loop through the items array and place a new Native Express ad in every ith position in
        // the items List.
        for (int i = 0; i <= klikButton.size(); i += ITEMS_PER_AD) {
            final NativeExpressAdView adView = new NativeExpressAdView(MainActivity.this);
            klikButton.add(i, adView);
        }
    }

    /**
     * Sets up and loads the Native Express ads.
     */
    private void setUpAndLoadNativeExpressAds() {
        // Use a Runnable to ensure that the RecyclerView has been laid out before setting the
        // ad size for the Native Express ad. This allows us to set the Native Express ad's
        // width to match the full width of the RecyclerView.
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                final float scale = MainActivity.this.getResources().getDisplayMetrics().density;
                // Set the ad size and ad unit ID for each Native Express ad in the items list.
                for (int i = 0; i <= klikButton.size(); i += ITEMS_PER_AD) {
                    final NativeExpressAdView adView =
                            (NativeExpressAdView) klikButton.get(i);
                    final CardView cardView = (CardView) findViewById(R.id.ad_card_view);
                    final int adWidth = cardView.getWidth() - cardView.getPaddingLeft()
                            - cardView.getPaddingRight();
                    AdSize adSize = new AdSize((int) (adWidth / scale), NATIVE_EXPRESS_AD_HEIGHT);
                    adView.setAdSize(adSize);
                    adView.setAdUnitId(getResources().getString(R.string.native_ad_unit_id));
                }

                // Load the first Native Express ad in the items list.
                loadNativeExpressAd(0);
            }
        });
    }

    /**
     * Loads the Native Express ads in the items list.
     */
    private void loadNativeExpressAd(final int index) {

        if (index >= klikButton.size()) {
            return;
        }

        Object item = klikButton.get(index);
        if (!(item instanceof NativeExpressAdView)) {
            throw new ClassCastException("Expected item at index " + index + " to be a Native"
                    + " Express ad.");
        }

        final NativeExpressAdView adView = (NativeExpressAdView) item;

        // Set an AdListener on the NativeExpressAdView to wait for the previous Native Express ad
        // to finish loading before loading the next ad in the items list.
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                // The previous Native Express ad loaded successfully, call this method again to
                // load the next ad in the items list.
                loadNativeExpressAd(index + ITEMS_PER_AD);
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // The previous Native Express ad failed to load. Call this method again to load
                // the next ad in the items list.
                Log.e("MainActivity", "The previous Native Express ad failed to load. Attempting to"
                        + " load the next Native Express ad in the items list.");
                loadNativeExpressAd(index + ITEMS_PER_AD);
            }
        });

        // Load the Native Express ad.
        adView.loadAd(new AdRequest.Builder().build());
    }


    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        else return false;
    }

    public void notifNoOnline() {
        new AlertDialog.Builder(this)
                .setMessage("Please connect to the Internet to continue!")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
//                        MainActivity.this.finish();

                        Intent intent = new Intent(MainActivity.this, SplashActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    }
                })
//                .setNegativeButton("No", null)
                .show();
    }
}
