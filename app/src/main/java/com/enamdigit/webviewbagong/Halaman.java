package com.enamdigit.webviewbagong;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.startapp.android.publish.adsCommon.StartAppAd;

//import static com.enamdigit.webviewbagong.MyAds.interstitialAd;
import static com.enamdigit.webviewbagong.R.id.action_share;

public class Halaman extends AppCompatActivity {

    private WebView mWebView;
    private static String LOG_TAG = "EXAMPLE";
    NativeExpressAdView mAdView;
    VideoController mVideoController;

    private InterstitialAd mInterstitialAd;
    private Boolean showProgress = false;
    private ProgressDialog pd;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_halaman);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        toolbar.setSubtitle(this.getIntent().getStringExtra("pesan2"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);


        mWebView = (WebView) findViewById(R.id.activity_main_webview);
        mWebView.loadUrl(this.getIntent().getStringExtra("pesan"));

        // mengaktifkan webclient agar loadurl webview tampil di lokal apk
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                view.loadUrl(url);
                return true;
            }
        });



        pd = new ProgressDialog(this);

        // Create the InterstitialAd and set the adUnitId.
        mInterstitialAd = new InterstitialAd(this);
        // Defined in res/values/strings.xml
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id02));
        requestInterstitial();

        Button btnPindahHal2 = (Button) findViewById(R.id.hide_button);
        btnPindahHal2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Halaman.this, Halaman2.class);
                startActivity(intent);
                showInterstitial();

            }
        });

        // native ads admob -----------------------------------------------------------------------------

        // Locate the NativeExpressAdView.
        mAdView = (NativeExpressAdView) findViewById(R.id.adView);

        // Set its video options.
        mAdView.setVideoOptions(new VideoOptions.Builder()
                .setStartMuted(true)
                .build());

        // The VideoController can be used to get lifecycle events and info about an ad's video
        // asset. One will always be returned by getVideoController, even if the ad has no video
        // asset.
        mVideoController = mAdView.getVideoController();
        mVideoController.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
            @Override
            public void onVideoEnd() {
                Log.d(LOG_TAG, "Video playback is finished.");
                super.onVideoEnd();
            }
        });

        // Set an AdListener for the AdView, so the Activity can take action when an ad has finished
        // loading.
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                if (mVideoController.hasVideoContent()) {
                    Log.d(LOG_TAG, "Received an ad that contains a video asset.");
                } else {
                    Log.d(LOG_TAG, "Received an ad that does not contain a video asset.");
                }
            }
        });

        mAdView.loadAd(new AdRequest.Builder().build());

        // Banner Ads Admob
        AdView mAdViewban = (AdView) findViewById(R.id.adView_banner);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdViewban.loadAd(adRequest);

    }


    private void requestInterstitial() {

        if (showProgress) {

            pd.setMessage("Please Wait...");
            pd.setCancelable(false);
            pd.show();

            Runnable progressRunnable = new Runnable() {

                @Override
                public void run() {
                    pd.cancel();
                }
            };

            Handler pdCanceller = new Handler();
            pdCanceller.postDelayed(progressRunnable, 3000);
        }

        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);

        mInterstitialAd.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {
                if (showProgress) {
                    pd.dismiss();
                    showInterstitial();
                }
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                if (showProgress) {

                    requestInterstitial();
                }
            }

            @Override
            public void onAdClosed() {
                intent = new Intent(Halaman.this, Halaman2.class);
                startActivity(intent);
            }
        });
    }

    private void showInterstitial() {
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
            showProgress = false;
        } else {
            showProgress = true;
            requestInterstitial();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case action_share:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...

//                        Toast.makeText(Halaman.this, "Share Toolbar Image Icon Clicked", Toast.LENGTH_LONG).show();

                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "AndroidSolved");
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                                "Grab for Free " + getResources().getString(R.string.app_name) + " Now! " + getResources().getString(R.string.url_apps));
                        startActivity(Intent.createChooser(sharingIntent, "Share via"));

                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mInterstitialAd != null) {
            mInterstitialAd.setAdListener(null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mInterstitialAd != null) {
            mInterstitialAd.setAdListener(null);
        }
    }

    @Override
    public void onBackPressed() {
        StartAppAd.onBackPressed(this);
        super.onBackPressed();
    }
}
