package com.poojab26.visualsearchtensorflow.Fragments;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.poojab26.visualsearchtensorflow.Const;
import com.poojab26.visualsearchtensorflow.Model.Item;
import com.poojab26.visualsearchtensorflow.R;

import java.io.ByteArrayOutputStream;

public class AddItemFragment extends Fragment {
    Bitmap bitmapFromCamera;
    String label;

    Button btnYes, btnNo;
    ImageView imgCamera;
    TextView tvAskUser;

    DatabaseReference productsRef;
    StorageReference imageRef;
    String prodId;
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
        productsRef = database.getReference("items");
        prodId = productsRef.push().getKey();

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        imageRef = storageRef.child("items/"+prodId+".jpg");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_item, container, false);

        setupUI(rootView);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        productsRef = database.getReference("items");

        imgCamera.setImageBitmap(bitmapFromCamera);
        tvAskUser.setText("Is this " + label + "?");

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickYesButton();

            }
        });

        return rootView;
    }

    private void setupUI(View rootview) {
        tvAskUser = rootview.findViewById(R.id.tvAskUser);
        btnYes = rootview.findViewById(R.id.btnYes);
        btnNo = rootview.findViewById(R.id.btnNo);
        imgCamera = rootview.findViewById(R.id.imgCamera);
    }

    public void onClickYesButton(){
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
                productsRef.child(prodId).setValue(item);
                ItemListFragment itemListFragment = new ItemListFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.activity_main, itemListFragment, null)
                        .commit();

            }
        });
    }

}
