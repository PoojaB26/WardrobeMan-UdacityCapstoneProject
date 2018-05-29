package com.poojab26.visualsearchtensorflow.Fragments;


import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Visibility;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.poojab26.visualsearchtensorflow.Adapters.ItemsAdapter;
import com.poojab26.visualsearchtensorflow.Const;
import com.poojab26.visualsearchtensorflow.MainActivity;
import com.poojab26.visualsearchtensorflow.Model.Item;
import com.poojab26.visualsearchtensorflow.R;
import com.poojab26.visualsearchtensorflow.UploadItemWidget;

import java.util.ArrayList;
import java.util.HashMap;


public class ItemListFragment extends Fragment {

    RecyclerView.LayoutManager wardrobeLayoutManager;
    RecyclerView rvItemsList;
    ItemsAdapter itemsAdapter;
    FloatingActionButton fabButtonOpenCamera;
    final ArrayList<Item> itemsList = new ArrayList<Item>();

    DatabaseReference itemsRef;
    ValueEventListener itemDataListener;

    public Context context;

    public ItemListFragment() {
        // Required empty public constructor
    }


    private RecyclerViewReadyCallback recyclerViewReadyCallback;

    public interface RecyclerViewReadyCallback {
        void onLayoutReady();
    }

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        recyclerViewReadyCallback = new RecyclerViewReadyCallback() {
            @Override
            public void onLayoutReady() {
                Const.setCount(itemsAdapter.getItemCount());
                setupWidget(itemsAdapter.getItemCount());
            }
        };

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_item_list, container, false);
        rvItemsList = rootView.findViewById(R.id.rvItems);

        fabButtonOpenCamera = rootView.findViewById(R.id.btnDetectObject);
        fabButtonOpenCamera.setVisibility(View.VISIBLE);

        fabButtonOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraFragment cameraFragment = new CameraFragment();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_left_enter,
                        R.anim.slide_left_exit,
                        R.anim.slide_right_enter,
                        R.anim.slide_right_exit)
                        .replace(R.id.activity_main, cameraFragment)
                        .commit();

                fabButtonOpenCamera.setVisibility(View.GONE);
            }
        });
        setupRecyclerView();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        itemsRef = database.getReference("items");

        loadItemsImages();

        return rootView;
    }

    private void setupRecyclerView() {
        wardrobeLayoutManager = new GridLayoutManager(getActivity(), 2);
        rvItemsList.setLayoutManager(wardrobeLayoutManager);
    }

    private void loadItemsImages() {

        itemDataListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    Item item = childSnapshot.getValue(Item.class);

                    HashMap map = (HashMap) childSnapshot.getValue();
                    if (map != null) {
                        itemsList.add(item);
                    }
                }

                itemsAdapter = new ItemsAdapter(itemsList);
                rvItemsList.setAdapter(itemsAdapter);
                rvItemsList.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (recyclerViewReadyCallback != null) {
                            recyclerViewReadyCallback.onLayoutReady();
                        }
                        rvItemsList.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                databaseError.toException();
            }
        };
        itemsRef.addValueEventListener(itemDataListener);
    }

    public void setupWidget(int count) {
        UploadItemWidget.setWardrobeCount(count);

        Intent widgetIntent = new Intent(context, UploadItemWidget.class);
        widgetIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        int[] ids = AppWidgetManager.getInstance(getActivity().getApplication())
                .getAppWidgetIds(new ComponentName(getActivity().getApplication(), UploadItemWidget.class));
        widgetIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        getActivity().sendBroadcast(widgetIntent);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        itemsRef.removeEventListener(itemDataListener);
    }
}
