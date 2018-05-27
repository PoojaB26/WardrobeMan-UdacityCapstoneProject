package com.poojab26.visualsearchtensorflow;


import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.poojab26.visualsearchtensorflow.Adapters.ItemsAdapter;
import com.poojab26.visualsearchtensorflow.Fragments.CameraFragment;
import com.poojab26.visualsearchtensorflow.Fragments.ItemListFragment;
import com.poojab26.visualsearchtensorflow.Model.Item;

import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Boolean.TRUE;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    DatabaseReference itemsRef;
    AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getTitle());


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        itemsRef = database.getReference("items");


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.get(Const.WidgetLaunch) == TRUE) {
                CameraFragment cameraFragment = new CameraFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.activity_main, cameraFragment)
                        .commit();
            }
        } else {
            ItemListFragment itemListFragment = new ItemListFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_main, itemListFragment)
                    .commit();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences(Const.Preferences, MODE_PRIVATE);
        if(prefs.contains(Const.PreferencesCount)) {
            Const.setCount(prefs.getInt(Const.PreferencesCount, 0));
            setupWidget();
        }
    }


    public void setupWidget() {
        UploadItemWidget.setWardrobeCount(Const.getCount());

        Intent widgetIntent = new Intent(MainActivity.this, UploadItemWidget.class);
        widgetIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        int[] ids = AppWidgetManager.getInstance(getApplication())
                .getAppWidgetIds(new ComponentName(getApplication(), UploadItemWidget.class));
        widgetIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(widgetIntent);
    }
}


