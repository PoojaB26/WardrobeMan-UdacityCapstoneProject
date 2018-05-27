package com.poojab26.visualsearchtensorflow.Fragments;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.transition.AutoTransition;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.poojab26.visualsearchtensorflow.Const;
import com.poojab26.visualsearchtensorflow.MainActivity;
import com.poojab26.visualsearchtensorflow.Model.Item;
import com.poojab26.visualsearchtensorflow.R;
import com.poojab26.visualsearchtensorflow.Transitions.CustomTransition;
import com.poojab26.visualsearchtensorflow.UploadItemWidget;

import java.io.ByteArrayOutputStream;

import static android.content.Context.MODE_PRIVATE;

public class AddItemFragment extends Fragment {
    Bitmap bitmapFromCamera;
    String label;

    Button btnYes, btnNo, btnUserUpload;
    ImageView imgCamera;
    TextView tvAskUser;
    LinearLayout mLinearSpinLayout, mLinearAskUser;
    Spinner spinnerLabels;
    ProgressBar progressUploadData;


    DatabaseReference itemsRef;
    StorageReference imageRef;
    String itemId;

    public AddItemFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            label = bundle.getString(Const.Label, "");
            bitmapFromCamera = bundle.getParcelable(Const.CameraBitmap);
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        itemsRef = database.getReference("items");
        itemId = itemsRef.push().getKey();

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        imageRef = storageRef.child("items/"+ itemId +".jpg");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_item, container, false);

        setupUI(rootView);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        itemsRef = database.getReference("items");

        imgCamera.setImageBitmap(bitmapFromCamera);
        tvAskUser.setText(String.format(getString(R.string.tv_askuser), label));

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadData();

            }
        });
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLinearAskUser.setVisibility(View.GONE);
                mLinearSpinLayout.setVisibility(View.VISIBLE);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                        R.array.labels_array, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerLabels.setAdapter(adapter);



            }
        });
        btnUserUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                label = spinnerLabels.getSelectedItem().toString();
                progressUploadData.setVisibility(View.VISIBLE);
                uploadData();
            }
        });

        return rootView;
    }

    private void setupUI(View rootview) {
        tvAskUser = rootview.findViewById(R.id.tvAskUser);
        btnYes = rootview.findViewById(R.id.btnYes);
        btnNo = rootview.findViewById(R.id.btnNo);
        btnUserUpload = rootview.findViewById(R.id.btnUserUpload);
        spinnerLabels = rootview.findViewById(R.id.spinLabels);
        mLinearSpinLayout = rootview.findViewById(R.id.llShowSpinner);
        mLinearAskUser = rootview.findViewById(R.id.llAskUser);
        imgCamera = rootview.findViewById(R.id.imgCamera);
        progressUploadData = rootview.findViewById(R.id.progressUserUpload);

    }

    public void uploadData(){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmapFromCamera.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        final byte[] cameraByteArray = baos.toByteArray();
        UploadTask uploadTask = imageRef.putBytes(cameraByteArray);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                Uri uri = taskSnapshot.getDownloadUrl();
                Item item = new Item(label, uri.toString());
                itemsRef.child(itemId).setValue(item);
                progressUploadData.setVisibility(View.GONE);
                Const.setCount(Const.getCount()+1);
                Log.d("act", "additem " + Const.getCount() );
                SharedPreferences.Editor editor = getActivity().getSharedPreferences(Const.PreferencesCount, MODE_PRIVATE).edit();
                editor.putInt(Const.PreferencesCount, Const.getCount());
                editor.apply();
                attachListFragment();

            }
        });
    }

    public void attachListFragment(){
        setupWidget();

        Fade exitFade = new Fade();
        exitFade.setDuration(5);
        setExitTransition(exitFade);

        ItemListFragment itemListFragment = new ItemListFragment();

        TransitionSet enterTransitionSet = new TransitionSet();
        enterTransitionSet.addTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.move));
        enterTransitionSet.setDuration(5);
        enterTransitionSet.setStartDelay(1);
        itemListFragment.setSharedElementEnterTransition(enterTransitionSet);


        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .addSharedElement(imgCamera, imgCamera.getTransitionName())
                .replace(R.id.activity_main, itemListFragment, null)
                .commit();
    }

    public void setupWidget() {
        UploadItemWidget.setWardrobeCount(Const.getCount());

        Intent widgetIntent = new Intent(getActivity(), UploadItemWidget.class);
        widgetIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        int[] ids = AppWidgetManager.getInstance(getActivity().getApplication())
                .getAppWidgetIds(new ComponentName(getActivity().getApplication(), UploadItemWidget.class));
        widgetIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        getActivity().sendBroadcast(widgetIntent);
    }

}
