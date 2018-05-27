package com.poojab26.visualsearchtensorflow;


import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.poojab26.visualsearchtensorflow.Fragments.CameraFragment;
import com.poojab26.visualsearchtensorflow.Fragments.ItemListFragment;

import static java.lang.Boolean.TRUE;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    DatabaseReference itemsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getTitle());


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        itemsRef = database.getReference("items");
        setupWidget();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if(extras.get(Const.WidgetLaunch)==TRUE){
                CameraFragment cameraFragment = new CameraFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.activity_main, cameraFragment)
                        .commit();
            }
        }else {
            ItemListFragment itemListFragment = new ItemListFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_main, itemListFragment)
                    .commit();
        }

    }

    public void setupWidget(){
        UploadItemWidget.setWardrobeCount(5);

        Intent widgetIntent = new Intent(MainActivity.this, UploadItemWidget.class);
        widgetIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        int[] ids = AppWidgetManager.getInstance(getApplication())
                .getAppWidgetIds(new ComponentName(getApplication(), UploadItemWidget.class));
        widgetIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(widgetIntent);
    }


}
