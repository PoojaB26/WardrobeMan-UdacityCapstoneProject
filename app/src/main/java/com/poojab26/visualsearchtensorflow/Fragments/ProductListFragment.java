package com.poojab26.visualsearchtensorflow.Fragments;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.poojab26.visualsearchtensorflow.Adapters.ProductAdapter;
import com.poojab26.visualsearchtensorflow.Interface.RetrofitInterface;
import com.poojab26.visualsearchtensorflow.Model.Product;
import com.poojab26.visualsearchtensorflow.Model.Products;
import com.poojab26.visualsearchtensorflow.R;
import com.poojab26.visualsearchtensorflow.Utils.APIClient;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProductListFragment extends Fragment {

    RecyclerView.LayoutManager prodLayoutManager;
    RecyclerView rvItemsList;

    FloatingActionButton fabButtonOpenCamera;
    final ArrayList<Product> itemsList = new ArrayList<Product>();

    DatabaseReference productsRef;

    public ProductListFragment() {
        // Required empty public constructor
    }

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_product_list, container, false);
        rvItemsList = rootView.findViewById(R.id.rvProducts);

        fabButtonOpenCamera = rootView.findViewById(R.id.btnDetectObject);
        fabButtonOpenCamera.setVisibility(View.VISIBLE);

        fabButtonOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraFragment cameraFragment = new CameraFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.activity_main, cameraFragment)
                        .commit();

                fabButtonOpenCamera.setVisibility(View.GONE);
            }
        });
        setupRecyclerView();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        productsRef = database.getReference("products");

        loadProductImage();
        return rootView;
    }

    private void setupRecyclerView() {
        prodLayoutManager = new GridLayoutManager(getActivity(), 2);
        rvItemsList.setLayoutManager(prodLayoutManager);
    }

    private void loadProductImage() {

        ValueEventListener productDataListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    Product product = childSnapshot.getValue(Product.class);

                    HashMap map = (HashMap) childSnapshot.getValue();
                    if (map != null) {
                        itemsList.add(product);
                    }
                }

                rvItemsList.setAdapter(new ProductAdapter(itemsList));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                databaseError.toException();
            }
        };
        productsRef.addValueEventListener(productDataListener);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


}
