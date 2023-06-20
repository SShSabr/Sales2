package com.sabr.sales;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class EditActivity extends AppCompatActivity {

    EditText editText;
    EditText editShop;
    TextView dateCreate;
    Button okButton, cancelButton;
    Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        editShop = findViewById(R.id.editShop);
        dateCreate = findViewById(R.id.dateCreate);
        editText = findViewById(R.id.editTextActivityEdit);
        okButton = findViewById(R.id.okButtonActivityEdit);
        cancelButton = findViewById(R.id.cancelButtonActivityEdit);

        Bundle arguments = getIntent().getExtras();

        if (arguments != null){
            product = arguments.getParcelable(MainActivity.EDIT_MESSAGE);
            editText.setText(product.getName());
            dateCreate.setText(product.getDate());
            editShop.setText(product.getShop());
        }else {
            product = new Product("", 1, "", "", "");
        }

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                product.setName(editText.getText().toString());
                product.setShop(editShop.getText().toString());
                Intent intent = new Intent();
                intent.putExtra(MainActivity.EDIT_MESSAGE, product);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }
}