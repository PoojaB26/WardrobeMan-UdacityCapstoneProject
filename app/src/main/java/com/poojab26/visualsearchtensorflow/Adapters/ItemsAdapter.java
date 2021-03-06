package com.poojab26.visualsearchtensorflow.Adapters;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.poojab26.visualsearchtensorflow.Model.Item;
import com.poojab26.visualsearchtensorflow.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by poojab26 on 08-Apr-18.
 */

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {
    public ItemsAdapter(ArrayList<Item> items) {
        itemList = items;
        Collections.reverse(itemList);
    }

    private final ArrayList<Item> itemList;


    @Override
    public ItemsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.wardrobe_item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemsAdapter.ViewHolder holder, int position) {

            holder.bind(position);


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivItemImage;

        public ViewHolder(View itemView) {
            super(itemView);
            ivItemImage = itemView.findViewById(R.id.ivItemImage);
        }

        public void bind(final int position) {
                if(position==0){
                    ViewCompat.setTransitionName(ivItemImage, ivItemImage.getTransitionName());
                }
                String imgPath = itemList.get(position).getItemDownloadUrl();
                if (!TextUtils.isEmpty(imgPath)) {
                    Picasso.with(itemView.getContext())
                            .load(imgPath)
                            .into(ivItemImage);

                }
        }
    }
}
