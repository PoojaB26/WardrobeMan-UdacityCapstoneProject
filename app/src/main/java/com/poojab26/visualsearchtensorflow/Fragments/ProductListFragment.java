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


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProductListFragment extends Fragment {

    RecyclerView.LayoutManager topLayoutManager, secondLayoutManager;
    RecyclerView rvTopProducts, rvSecondProducts;
    public RetrofitInterface retrofitInterface;
    TextView tvProductCategory, tvSecondCategory;
    String topResult = null, secondResult=null;
    DatabaseReference productReference = null;
    Boolean mSimilarItems = false;
    Boolean mFromCamera = false;
    FloatingActionButton fabButtonOpenCamera;

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
        if(mSimilarItems){
            tvProductCategory.setText("View similar products");
            tvSecondCategory.setText("You can also view");
          //  if(!secondResult.equalsIgnoreCase("all"))

        }
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
        return rootView;
    }

    private void setupRecyclerView() {
        topLayoutManager = new GridLayoutManager(getActivity(), 2);
        secondLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        rvTopProducts.setLayoutManager(topLayoutManager);
        rvSecondProducts.setLayoutManager(secondLayoutManager);
    }

    private void loadProductImage(final String topResultArg) {

        retrofitInterface = APIClient.getClient().create(RetrofitInterface.class);

        Call<Products> call = retrofitInterface.getProductList();
        call.enqueue(new Callback<Products>() {
            @Override
            public void onResponse(Call<Products> call, Response<Products> response) {
                Products products = response.body();
                ArrayList<Product> customProducts;
                customProducts = new ArrayList();
                for(int i=0; i<products.getProducts().size(); i++){
                    if (topResultArg.equalsIgnoreCase("all")){
                        customProducts = products.getProducts();
                        break;
                    }
                    else if(products.getProducts().get(i).getProductLabel().equalsIgnoreCase(topResultArg))
                    {
                        customProducts.add(products.getProducts().get(i));
                    }
                }

                if(mFromCamera) {
                    ValueEventListener topicListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            ArrayList<Product> products1 = new ArrayList<Product>();

                            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                Product product = childSnapshot.getValue(Product.class);

                                HashMap map = (HashMap) childSnapshot.getValue();
                                if (map != null) {
                                    products1.add(product);
                                }
                                //or
                                // String name = (String) childSnapshot.child("storytext").getValue();


                            }

                            String label = products1.get(0).getProductLabel();


                        /*// Get Post object and use the values to update the UI
                        for (DataSnapshot productSnapshot: dataSnapshot.getChildren()) {
                            Product product = productSnapshot.getValue(Product.class);
                            Log.d("Get Data", product.getProductLabel());
                            products1.setProducts();

                        }*/
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Getting Post failed, log a message
                            databaseError.toException();
                            // ...
                        }
                    };
                    productReference.addValueEventListener(topicListener);

                }

                if(topResultArg.equalsIgnoreCase("none")){
                    Toast.makeText(getContext() ,"No similar items available!", Toast.LENGTH_SHORT).show();
                    rvTopProducts.setAdapter(new ProductAdapter(products.getProducts(), false));

                }else{
                    rvTopProducts.setAdapter(new ProductAdapter(customProducts, false));
                }
            }

            @Override
            public void onFailure(Call<Products> call, Throwable t) {
                Log.d("Error", t.getMessage());
            }
        });
    }
    private void loadSecondResultsImage(final String secondResultArg) {

        retrofitInterface = APIClient.getClient().create(RetrofitInterface.class);

        Call<Products> call = retrofitInterface.getProductList();
        call.enqueue(new Callback<Products>() {
            @Override
            public void onResponse(Call<Products> call, Response<Products> response) {
                Products products = response.body();
                ArrayList<Product> customProducts;
                customProducts = new ArrayList();
                for(int i=0; i<products.getProducts().size(); i++){
                    if(products.getProducts().get(i).getProductLabel().equalsIgnoreCase(secondResultArg)){
                        customProducts.add(products.getProducts().get(i));
                    }
                }
                if(!secondResultArg.equalsIgnoreCase("all"))
                    rvSecondProducts.setAdapter(new ProductAdapter(customProducts, true));
                else
                    rvSecondProducts.setAdapter(new ProductAdapter(products.getProducts(), true));

            }

            @Override
            public void onFailure(Call<Products> call, Throwable t) {
                Log.d("Error", t.getMessage());
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    public void setTopResult(String result) {
        topResult = result;
        loadProductImage(topResult);
    }

    public void setSecondResult(String result) {
        secondResult = result;
        loadSecondResultsImage(secondResult);
    }

    public void setSimilarItems(boolean similarItems) {
        mSimilarItems = similarItems;
    }

    public void setFromCamera(boolean fromCamera) {
        mFromCamera = fromCamera;
    }


    public void setProductReference(DatabaseReference productReference) {
        this.productReference = productReference;
    }
}
