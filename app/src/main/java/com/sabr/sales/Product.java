package com.sabr.sales;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Product implements Parcelable {
    private String name; //наименование покупки
    private int cont;    //количество
    private String shop; //место покупки
    private float[] geo={0 , 0}; //координаты на карте
    private String picture;   //фото
    private String date; //дата создания записи

    Product(String name, int cont, String shop, String date, String picture){
        this.name = name;
        this.cont = cont;
        this.shop = shop;
        if (date.isEmpty()) {
            @SuppressLint("SimpleDateFormat")
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            this.date = df.format(Calendar.getInstance().getTime());
        }else {
            this.date = date;
        }
        if (picture.isEmpty()){
            this.picture = "0";
        }else{
        this.picture = picture;
        }
    }

    protected Product(@NonNull Parcel in) {
        name = in.readString();
        cont = in.readInt();
        shop = in.readString();
        date = in.readString();
        picture = in.readString();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCont() {
        return cont;
    }

    public void setCont(int cont) {
        this.cont = cont;
    }

    public float[] getGeo() {
        return geo;
    }

    public void setGeo(float[] geo) {
        this.geo = geo;
    }

    public String getShop() {
        return shop;
    }

    public void setShop(String shop) {
        this.shop = shop;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
           this.date = date;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        if (picture.isEmpty()){
            this.picture = "0";
        }else {
            this.picture = picture;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(cont);
        dest.writeString(shop);
        dest.writeString(date);
        dest.writeString(picture);
    }
}
