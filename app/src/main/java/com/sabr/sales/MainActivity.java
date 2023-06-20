package com.sabr.sales;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Product> products ;//= new ArrayList<Product>();

    private ProdAdapter prodAdapter;
    private EditText nameProdAdd;
    private int tempPosition = -1;
    final static String nameVariableKey = "NAME_VARIABLE"; //ключ к Bundly
    final static String CAMERA_MESSAGE = "CAMERA_MESSAGE"; //ключ к возврату от активити камеры
    final static String EDIT_MESSAGE = "EDIT_MESSAGE";     //ключ к возврату от активи изменения товара

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Инициализация списка
        setInitialData(savedInstanceState);


        Button addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // addProdActivity(v);
                if (!nameProdAdd.getText().toString().isEmpty()) {
                    addProd(new Product(nameProdAdd.getText().toString(), 1, String.valueOf(R.string.shop), "", ""));
                    nameProdAdd.setText("");
                    hideKeyboard(v);
                }else {
                    editProdActivity(new Product("" , 1, "", "", ""), 0);
                }
            }
        });

        nameProdAdd = findViewById(R.id.addProdName);
        //nameShopAdd = findViewById();


        RecyclerView recyclerView = findViewById(R.id.listItem);
        ProdAdapter.OnProdClickListener prodClickListener = new ProdAdapter.OnProdClickListener() {
            //Нажатие на список
            @Override
            public void onProdClick(Product product, int position) {
                tempPosition = position;
                @SuppressLint("SimpleDateFormat")
                DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                product.setDate(df.format(Calendar.getInstance().getTime()));
                editProdActivity(product, position);
            }
            //Длинное нажатие на список
            @Override
            public void onLongProdClick(int chek, int position) {
                dialogFrame(position);
                tempPosition = position;
            }
            // запуск активити на добавление\изменение фото
            @Override
            public void onImageClick(Product product, int position) {
                tempPosition = position;
                startCameraActivity(product);
            }
        };

        prodAdapter = new ProdAdapter(this, products, prodClickListener);
        recyclerView.setAdapter(prodAdapter);
    }

    //Лаунчер Activity редактора
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                     if (result.getResultCode() == Activity.RESULT_OK){
                        Intent intent = result.getData();
                        if(intent != null) {
                            if (tempPosition >= 0) {
                                editProduct((Product) intent.getExtras().get(EDIT_MESSAGE));
                                }
                            else{
                                addProd((Product) intent.getExtras().get(EDIT_MESSAGE));
                                }
                        }
                    }
                }
            });
    //Лаунчер активити камеры
    ActivityResultLauncher<Intent> camResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK){
                        Intent intent = result.getData();
                        if (intent != null){
                            Product prod = (Product)intent.getExtras().get(CAMERA_MESSAGE);
                            editProduct(prod);
                        }
                    }
                }
            });

    private void startCameraActivity(Product product){
        Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
        intent.putExtra(CAMERA_MESSAGE, product);
        camResultLauncher.launch(intent);
    }

    //Сохранить состояние  ¿
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(nameVariableKey, products);

    }

    //Первичные данные
    private void setInitialData(Bundle bundle) {
         if (bundle != null){
            products = bundle.getParcelableArrayList(nameVariableKey);
        }
        else{
            products = new ArrayList<Product>();
            products = readDB();
        }
    }


    // Вызов окна добавления в список
    @SuppressLint("NotifyDataSetChanged")
    public void addProdActivity(View view){
//       String name = nameProdAdd.getText().toString();
//       if (!name.isEmpty()) {
//            products.add(new Product(name, 1, "aaa"));
//            nameProdAdd.setText("");
//            prodAdapter.notifyDataSetChanged();
//        }
        Intent intent = new Intent(this, EditActivity.class);
        activityResultLauncher.launch(intent);
    }
    //Добавление в список
    @SuppressLint("NotifyDataSetChanged")
    public void  addProd(Product product){
        products.add(product);
        prodAdapter.notifyDataSetChanged();
    }

    //Удалить из списка
    @SuppressLint("NotifyDataSetChanged")
    public void remove(){
        File file = new File(products.get(tempPosition).getPicture());
        if (file.exists()) {
            file.delete();
        }
        products.remove(tempPosition);
        prodAdapter.notifyDataSetChanged();
        tempPosition = -1;
    }
    // Окно на удаление из списка
    public void dialogFrame(int position){
        FragmentManager manager = getSupportFragmentManager();
        MyDialogFragment myDialogFragment = new MyDialogFragment();

        Bundle args = new Bundle();
        args.putParcelable("product", products.get(position));
        myDialogFragment.setArguments(args);

        myDialogFragment.show(manager, "Tag");
    }

    //Вызов окна редактора Activity
    public void editProdActivity(Product product, int position){
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra(EDIT_MESSAGE, product);
        activityResultLauncher.launch(intent);
    }

    //Редактирование поля
    @SuppressLint("NotifyDataSetChanged")
    public  void editProduct(Product product){
        products.set(tempPosition, product);
        prodAdapter.notifyDataSetChanged();
        tempPosition = -1;
    }

    //Скрыть клавиатуру с экрана
    public void hideKeyboard(View view){
        view = getCurrentFocus();
        if (view != null){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    //Чтение с DB sales
    public ArrayList<Product> readDB (){
        ArrayList<Product> tempArray = new ArrayList<Product>();
        SQLiteDatabase db = getBaseContext().openOrCreateDatabase("sales.db", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS list (name TEXT, count INTEGER, shop TEXT, geoX FLOAT, geoY FLOAT, dateCreate TEXT, picture TEXT);");

        Cursor query = db.rawQuery("SELECT * FROM list;", null);
        if (query.getCount() > 0 ){
            float[] geo = {0,0};
            while (query.moveToNext()){
                Product tempProd = new Product("", 0, "", "", "");
                tempProd.setName(query.getString(0));
                tempProd.setCont(query.getInt(1));
                tempProd.setShop(query.getString(2));
                geo[0] = query.getFloat(3);
                geo[1] = query.getFloat(4);
                tempProd.setGeo(geo);
                tempProd.setDate(query.getString(5));
                tempProd.setPicture(query.getString(6));
                tempArray.add(tempProd);
            }
        }
        query.close();
        db.close();
        return tempArray;
    }
    //Запись в DB sales
    public void writeDB(@NonNull ArrayList<Product> listProd){
        SQLiteDatabase db = getBaseContext().openOrCreateDatabase("sales.db", MODE_PRIVATE, null);
        //db.execSQL("CREATE TABLE IF NOT EXISTS list (name TEXT, count INTEGER, shop TEXT, geoX FLOAT, geoY FLOAT, picture TEXT);");
        db.execSQL("DELETE FROM list");
        String sqlDB = "";

        for (int i = 0; i < listProd.size(); i++) {
            Product prod = listProd.get(i);
            sqlDB = "INSERT OR IGNORE INTO list VALUES ('" +
                    prod.getName() + "'," +
                    String.valueOf(prod.getCont()) + ",'" +
                    prod.getShop() + "'," +
                    String.valueOf(prod.getGeo()[0]) + "," +
                    String.valueOf(prod.getGeo()[1]) + ",'" +
                    prod.getDate() + "','" +
                    prod.getPicture() + "');";
            db.execSQL(sqlDB);
        }
        db.close();
   }

    @Override
    protected void onPause() {
        writeDB(products);
        super.onPause();
    }
}














