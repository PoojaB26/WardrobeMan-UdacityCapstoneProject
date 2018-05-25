package com.poojab26.visualsearchtensorflow;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.poojab26.visualsearchtensorflow.Fragments.ProductListFragment;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    DatabaseReference productsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getTitle());

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        productsRef = database.getReference("products");

        ProductListFragment productListFragment = new ProductListFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.activity_main, productListFragment)
                .commit();

    }


}
