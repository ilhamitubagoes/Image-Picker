package com.ilhambagoest.imagepicker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.ImageQuality;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.util.ArrayList;

import at.markushi.ui.CircleButton;

import static com.ilhambagoest.imagepicker.Constant.MAX_IMAGE;
import static com.ilhambagoest.imagepicker.Constant.SELECT_PHOTO;

public class PreviewImageActivity extends AppCompatActivity {

    final static String DATA = "data_image";

    Toolbar toolbar;
    ImageView ivAddImage;
    CircleButton btnCheck;
    CarouselView cvImage;
    ArrayList<String> imageList, imageListTemp;
    Integer posImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_image);

        toolbar = findViewById(R.id.toolbar);
        ivAddImage = findViewById(R.id.iv_add_image);
        cvImage = findViewById(R.id.carouselView);
        btnCheck = findViewById(R.id.btn_check);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        initView();
        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData(posImage);
    }

    private void initView() {
        if (getIntent() != null){
            imageList = getIntent().getStringArrayListExtra(DATA);
        }
    }

    private void loadData(Integer posImageCurrent) {

        if (posImageCurrent != null){
            posImage = posImageCurrent;
        } else {
            posImage = 0;
        }

        if (imageList.size() != 0){
            ImageListener imageListener = (position, imageView) -> {
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                Glide.with(this)
                        .asBitmap()
                        .load(imageList.get(position))
                        .fitCenter()
                        .into(imageView);
            };
            cvImage.setImageListener(imageListener);
            cvImage.setIndicatorVisibility(View.GONE);
            if (imageList.size() != 0){
                cvImage.setPageCount(imageList.size());
            } else {
                System.out.println("DEVELOPER : Just One Photo");
            }

            if (posImage != null){
                if (posImage != 0){
                    cvImage.setCurrentItem(posImage);
                }
            }

            if (imageList.size() == MAX_IMAGE){
                ivAddImage.setVisibility(View.INVISIBLE);
            } else {
                ivAddImage.setVisibility(View.VISIBLE);
            }
        } else {
            finish();
        }

    }

    private void initEvent() {
        cvImage.setImageClickListener(position -> System.out.println("DEVELOPER 1 : " + position));

        cvImage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                posImage = position;
                System.out.println("DEVELOPER 2 : " + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        ivAddImage.setOnClickListener(v -> {
            Options options = Options.init()
                    .setRequestCode(SELECT_PHOTO)
                    .setCount(MAX_IMAGE - imageList.size())
                    .setFrontfacing(false)
                    .setImageQuality(ImageQuality.REGULAR)
                    .setScreenOrientation(Options.SCREEN_ORIENTATION_USER_PORTRAIT)
                    .setImageResolution(1280, 960)
                    .setPath("/imagePicker/images");
            Pix.start(this, options);
        });

        btnCheck.setOnClickListener(v -> {
            if (imageList.size() != 4){
                Toast.makeText(this, "Please add 4 Image", Toast.LENGTH_SHORT).show();
            } else {
                checkAlert();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_preview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_crop) {
            for (int i=0; i<imageList.size(); i++){
                if (posImage == i){
                    Uri imageUri = Uri.fromFile(new File(imageList.get(i)));
                    CropImage.activity(imageUri)
                            .setAspectRatio(200,200)
                            .start(this);
                }
            }
        }

        if (item.getItemId() == R.id.menu_delete) {
            deleteAlert();
        }

        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK && data != null){
            imageListTemp = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            if (imageListTemp != null){
                imageList.addAll(imageListTemp);
                loadData(imageList.size()-1);
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (result != null){
                Uri resultUri = result.getUri();
                String pathCropped = resultUri.getPath();

                for (int i=0; i<imageList.size(); i++){
                    if (posImage == i){
                        imageList.remove(i);
                        imageList.add(i, pathCropped);
                    }
                }
                loadData(posImage);
            }
        }
    }

    private void deleteAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Apakah anda ingin menghapus gambar ini ?");
        builder.setCancelable(true);

        builder.setPositiveButton("Ya",
                (dialog, id) -> {
                    for (int i=0; i<imageList.size(); i++){
                        if (posImage == i){
                            imageList.remove(i);
                            loadData(posImage);
                        }
                    }
                    dialog.dismiss();
                });

        builder.setNegativeButton("Tidak",
                (dialog, id) -> dialog.cancel());

        AlertDialog alert11 = builder.create();
        alert11.show();
    }

    private void checkAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Apakah anda yakin semua data yang anda masukkan sudah benar ?");
        builder.setCancelable(true);

        builder.setPositiveButton("Ya",
                (dialog, id) -> {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(MainActivity.DATA, imageList);
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                    dialog.dismiss();
                });

        builder.setNegativeButton("Tidak",
                (dialog, id) -> dialog.cancel());

        AlertDialog alert11 = builder.create();
        alert11.show();
    }

}
