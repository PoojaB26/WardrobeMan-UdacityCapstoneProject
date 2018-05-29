package com.poojab26.visualsearchtensorflow.Fragments;


import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.transition.Explode;
import android.transition.Visibility;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.poojab26.visualsearchtensorflow.Classifier;
import com.poojab26.visualsearchtensorflow.Const;
import com.poojab26.visualsearchtensorflow.R;
import com.poojab26.visualsearchtensorflow.TensorFlowImageClassifier;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class CameraFragment extends Fragment {

    private String topResult;

    private CameraView cameraView;
    private FloatingActionButton fabCamera;
    ProgressBar progressBar;


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
        Explode exitFade = new Explode();
        exitFade.setDuration(1000);
        exitFade.setStartDelay(20);
        exitFade.setMode(Visibility.MODE_OUT);

        setEnterTransition(exitFade);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_camera, container, false);
        cameraView = rootView.findViewById(R.id.cameraView);
        fabCamera = rootView.findViewById(R.id.fabClick);
        progressBar = rootView.findViewById(R.id.progressCamera);

        fabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.start();
                cameraView.captureImage();
                progressBar.setVisibility(View.VISIBLE);
            }
        });
        initTensorFlowAndLoadModel(rootView);

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
                cameraView.stop();
                topResult = getClassifierResult();

                Bundle bundle = new Bundle();
                bundle.putString(Const.Label, topResult);
                bundle.putParcelable(Const.CameraBitmap, cameraBitmap);

                AddItemFragment addItemFragment = new AddItemFragment();
                addItemFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.activity_main, addItemFragment, null)
                        .commit();
                progressBar.setVisibility(View.GONE);
                }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        });


        return rootView;
    }

    public String getClassifierResult() {
        final List<Classifier.Recognition> results = classifier.recognizeImage(cameraBitmap);
        return results.get(0).getTitle();
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
