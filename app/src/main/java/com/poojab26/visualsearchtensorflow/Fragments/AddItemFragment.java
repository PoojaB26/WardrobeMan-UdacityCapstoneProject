package com.poojab26.visualsearchtensorflow.Fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.poojab26.visualsearchtensorflow.R;
import com.squareup.picasso.Picasso;

public class AddItemFragment extends Fragment {
    Bitmap bitmapFromCamera;
    String label;

    Button mYesButton, mNoButton;
    ImageView imgCamera;
    TextView tvAskUser;

    public AddItemFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_item, container, false);

        setupUI(rootView);
        imgCamera.setImageBitmap(bitmapFromCamera);
        tvAskUser.setText("Is this " + label);

        return rootView;
    }

    private void setupUI(View rootview) {
        tvAskUser = rootview.findViewById(R.id.tvAskUser);
        mYesButton = rootview.findViewById(R.id.btnYes);
        mNoButton = rootview.findViewById(R.id.btnNo);
        imgCamera = rootview.findViewById(R.id.imgCamera);
    }



    public void setBitmap(Bitmap bitmap) {
        bitmapFromCamera = bitmap;
    }

    public void setTopResult(String result) {
        label = result;
    }

}
