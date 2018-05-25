package com.poojab26.visualsearchtensorflow.Fragments;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.poojab26.visualsearchtensorflow.Classifier;
import com.poojab26.visualsearchtensorflow.Model.Product;
import com.poojab26.visualsearchtensorflow.R;
import com.poojab26.visualsearchtensorflow.TensorFlowImageClassifier;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class CameraFragment extends Fragment {

    /*Firebase variables*/
    DatabaseReference productsRef;
    FirebaseStorage storage;
    /**/

    private String topResult, secondResult = "all";
    private Float topResultConfidence, secondResultConfidence;

    private CameraView cameraView;
    private FloatingActionButton fabCamera;


    /*Tensorflow Variables*/
    private static final int INPUT_SIZE = 224;
    private static final int IMAGE_MEAN = 128;
    private static final float IMAGE_STD = 128.0f;
    private static final String INPUT_NAME = "input";
    private static final String OUTPUT_NAME = "final_result";

    private static final String MODEL_FILE = "file:///android_asset/graph.pb";
    private static final String LABEL_FILE = "file:///android_asset/labels.txt";
    /*
    *
    * */
    Bitmap cameraBitmap;
    private Classifier classifier;

    private Executor executor = Executors.newSingleThreadExecutor();


    public CameraFragment() {
        // Required empty public constructor
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_camera, container, false);
        cameraView = rootView.findViewById(R.id.cameraView);
        fabCamera = rootView.findViewById(R.id.fabClick);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        productsRef = database.getReference("products");
        final String prodId = productsRef.push().getKey();


        storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        final StorageReference imageRef = storageRef.child("items/"+prodId+".jpg");



        cameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {

            }

            @Override
            public void onError(CameraKitError cameraKitError) {

            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {

                cameraBitmap = cameraKitImage.getBitmap();
                cameraBitmap = Bitmap.createScaledBitmap(cameraBitmap, INPUT_SIZE, INPUT_SIZE, false);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                cameraBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();
                UploadTask uploadTask = imageRef.putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Uri uri = taskSnapshot.getDownloadUrl();
                        setup(uri, prodId);

                    }
                });





            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        });

        fabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.captureImage();
            }
        });

        initTensorFlowAndLoadModel(rootView);

        return rootView;
    }

    public void setup(Uri uri, String prodId) {
        final List<Classifier.Recognition> results = classifier.recognizeImage(cameraBitmap);

        topResult = results.get(0).getTitle();
        topResultConfidence = results.get(0).getConfidence();
        Log.d("LOL first", topResult + topResultConfidence);

        int size = results.size()-1;
        if(size>=1) {
            secondResult = results.get(1).getTitle();
            secondResultConfidence = results.get(1).getConfidence();

            if(secondResultConfidence<0.5) {
                secondResult = "all";
            }
        }

        if(topResultConfidence<0.7) {
            topResult = "none";
        }



        Product product = new Product(topResult, uri.toString());
        productsRef.child(prodId).setValue(product);

        getActivity().getSupportFragmentManager().beginTransaction().remove(CameraFragment.this).commit();

        ProductListFragment productListFragment = new ProductListFragment();
        productListFragment.setTopResult(topResult);
        productListFragment.setSecondResult(secondResult);
        productListFragment.setProductReference(productsRef);
        productListFragment.setFromCamera(true);
        if(topResult.equalsIgnoreCase("none")) {
            productListFragment.setSimilarItems(false);
        }
        else
            productListFragment.setSimilarItems(true);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.activity_main, productListFragment, null)
                .commit();


    }

    @Override
    public void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    public void onPause() {
        cameraView.stop();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                classifier.close();
            }
        });
    }

    private void initTensorFlowAndLoadModel(final View rootview) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {

                    classifier =
                            TensorFlowImageClassifier.create(
                                    rootview.getContext().getAssets(),
                                    MODEL_FILE,
                                    LABEL_FILE,
                                    INPUT_SIZE,
                                    IMAGE_MEAN,
                                    IMAGE_STD,
                                    INPUT_NAME,
                                    OUTPUT_NAME);


                } catch (final Exception e) {
                    throw new RuntimeException("Error initializing TensorFlow!", e);
                }
            }
        });
    }



}
