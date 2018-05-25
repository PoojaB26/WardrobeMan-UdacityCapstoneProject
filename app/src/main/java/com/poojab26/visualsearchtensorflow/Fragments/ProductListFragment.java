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

    RecyclerView.LayoutManager topLayoutManager, secondLayoutManager;
    RecyclerView rvTopProducts, rvSecondProducts;
    TextView tvProductCategory, tvSecondCategory;
    String topResult = null, secondResult = null;
    DatabaseReference productReference = null;
    Boolean mFromCamera = false;
    FloatingActionButton fabButtonOpenCamera;
    final ArrayList<Product> products1 = new ArrayList<Product>();

    public ProductListFragment() {
        // Required empty public constructor
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_product_list, container, false);
        rvTopProducts = rootView.findViewById(R.id.rvProducts);
        rvSecondProducts = rootView.findViewById(R.id.rvSecondProducts);

        tvProductCategory = rootView.findViewById(R.id.tvProductCategory);
        tvSecondCategory = rootView.findViewById(R.id.tvSecondCategory);
        tvProductCategory.setText("View similar products");
        tvSecondCategory.setText("You can also view");


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
        loadProductImage();
        return rootView;
    }

    private void setupRecyclerView() {
        topLayoutManager = new GridLayoutManager(getActivity(), 2);
        secondLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        rvTopProducts.setLayoutManager(topLayoutManager);
        rvSecondProducts.setLayoutManager(secondLayoutManager);

        //  rvTopProducts.setAdapter(new ProductAdapter(products1, false));

    }

    private void loadProductImage() {

        ValueEventListener topicListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    Product product = childSnapshot.getValue(Product.class);

                    HashMap map = (HashMap) childSnapshot.getValue();
                    if (map != null) {
                        products1.add(product);
                    }
                }

                rvTopProducts.setAdapter(new ProductAdapter(products1, false));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                databaseError.toException();
            }
        };
        productReference.addValueEventListener(topicListener);
    }

    private void loadSecondResultsImage(final String secondResultArg) {


    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


   /* public void setTopResult(String result) {
        topResult = result;
        loadProductImage();
    }

    public void setSecondResult(String result) {
        secondResult = result;
        loadSecondResultsImage(secondResult);
    }*/


    public void setFromCamera(boolean fromCamera) {
        mFromCamera = fromCamera;
    }


    public void setProductReference(DatabaseReference productReference) {
        this.productReference = productReference;
        //   loadProductImage();
    }
}
