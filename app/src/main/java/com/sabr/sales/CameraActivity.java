package com.sabr.sales;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class CameraActivity extends AppCompatActivity {

    private final static int CAMERA_REQUEST = 1888;
    private final static int GALLERY_IMAGE = 1666;
    private Product product;
    private Button cameraButton, galleryButton,cancelButton,saveButton;
    private ImageView imageCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        cancelButton = findViewById(R.id.backButton);
        saveButton = findViewById(R.id.saveButton);
        cameraButton = findViewById(R.id.cameraButton);
        galleryButton = findViewById(R.id.galleryButton);
        imageCamera = findViewById(R.id.imageCamera);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImage();
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureCamera();
            }
        });

        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galleryImage();
            }
        });
//        TableLayout tableLayout = new TableLayout(this);
//        TableRow tableRow = new TableRow(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            product = extras.getParcelable(MainActivity.CAMERA_MESSAGE);
            if (product.getPicture().length() > 5){
                File imgFile = new File(product.getPicture());
                if (imgFile.exists()){
                    Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    imageCamera.setScaleY(1);
                    imageCamera.setScaleX(1);
                    imageCamera.setImageBitmap(bitmap);
                }

//                String fileName = product.getDate() + ".png";
//                File dir = getFilesDir();
//                File file = new File(dir, fileName);
//                try {
//                    file.createNewFile();
//
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }

            }
        }
    }
    //отправляем результат на главную активити
    public void saveImage(){
        Intent save = new Intent();
        //save.putExtra(MainActivity.CAMERA_MESSAGE, product);
        save.putExtra(MainActivity.CAMERA_MESSAGE, product);
        setResult(RESULT_OK, save);
        finish();
    }

    public void captureCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    public void galleryImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        String st = getResources().getString(R.string.choice_gallery);
        startActivityForResult(Intent.createChooser(intent, st), GALLERY_IMAGE);
    }
изменить названия файлов
    public void onActivityResult(int req, int rez, Intent data) {
        if (req == CAMERA_REQUEST && rez == RESULT_OK) {
            // вернуть фото в product
            Bitmap photo = (Bitmap) data.getExtras().get("data");

            Matrix matrix = new Matrix();
            //matrix.postRotate(90);
            photo = Bitmap.createBitmap(photo, 0,0, photo.getWidth(), photo.getHeight(), matrix, true);

            imageCamera.setScaleX(1);
            imageCamera.setScaleY(1);
            imageCamera.setImageBitmap(photo);
            File dir = getFilesDir();
            File file = new File(dir, product.getDate() + ".jpeg");
            if (file.exists()) {
                file.delete();
            }
            try {
                FileOutputStream out = new FileOutputStream(file);
                photo.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            product.setPicture(file.toString());
        }

        if (req == GALLERY_IMAGE && rez == RESULT_OK){
            try {
                final Uri imageURI = data.getData();
                final InputStream imStream = getContentResolver().openInputStream(imageURI);
                final Bitmap selImage = BitmapFactory.decodeStream(imStream);
                imageCamera.setScaleX(1);
                imageCamera.setScaleY(1);
                imageCamera.setImageBitmap(selImage);
                File dir = getFilesDir();
                File file = new File(dir, product.getDate() + ".jpeg");
                if (file.exists()){
                    file.delete();
                }
                FileOutputStream out = new FileOutputStream(file);
                selImage.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();
                imStream.close();
                product.setPicture(file.toString());
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

        super.onActivityResult(req, rez, data);
    }


}