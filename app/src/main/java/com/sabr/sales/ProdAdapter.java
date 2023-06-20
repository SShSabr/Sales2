package com.sabr.sales;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProdAdapter extends RecyclerView.Adapter<ProdAdapter.ViewHolder> {


    interface OnProdClickListener{
        void onProdClick(Product product, int position);
        void onLongProdClick(int check, int position);
        void onImageClick(Product product, int position);
    }

    private final OnProdClickListener onClickListener;

    private final LayoutInflater inflater;
    //private final int layout;
    private final List<Product> productList;


    ProdAdapter(Context context, ArrayList<Product> products, OnProdClickListener onClickListener){
        this.inflater = LayoutInflater.from(context);
        this.onClickListener = onClickListener;
        this.productList = products;

    }

    @NonNull
    @Override
    public ProdAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = inflater.inflate(R.layout.list_prod, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProdAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position){
        Product product = productList.get(position);
        holder.textProd.setText(product.getName());
        holder.shopProd.setText(product.getShop());
        holder.dateCreate.setText(product.getDate());
        if (product.getPicture().length() > 5){
           // Log.d("----------Holder", product.getPicture());
            holder.imageProd.setImageURI(Uri.fromFile(new File(product.getPicture())));
        }

        holder.imageProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onImageClick(product, position);
            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
               onClickListener.onProdClick(product, position);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onClickListener.onLongProdClick(0, position);
               return false;
            }
        });


    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView shopProd;
        final TextView dateCreate;
        final TextView textProd;
        final ImageView imageProd;
        ViewHolder(View view){
            super(view);
            shopProd = view.findViewById(R.id.shop);
            textProd = view.findViewById(R.id.textProd);
            imageProd = view.findViewById(R.id.imageProd);
            dateCreate = view.findViewById(R.id.dateCreate);
        }

    }
}
