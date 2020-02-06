package com.ilhambagoest.imagepicker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.ImageQuality;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.util.ArrayList;

import static com.ilhambagoest.imagepicker.Constant.MAX_IMAGE;
import static com.ilhambagoest.imagepicker.Constant.PREVIEW_IMAGE;
import static com.ilhambagoest.imagepicker.Constant.SELECT_PHOTO;

public class MainActivity extends AppCompatActivity {

    final static String DATA = "data_image";

    Button btnChosseImage;
    ImageButton btnPhoto;
    CarouselView cvImage;
    ArrayList<String> imageList, imageResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initEvent();

    }

    private void initView() {

        imageList = new ArrayList<>();
        imageResult = new ArrayList<>();

        btnChosseImage = findViewById(R.id.btn_chosse_image);
        btnPhoto = findViewById(R.id.ib_photo);
        cvImage = findViewById(R.id.carouselView);
    }

    private void loadingData() {

        ImageListener imageListener = (position, imageView) -> {
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            Glide.with(this)
                    .asBitmap()
                    .load(imageResult.get(position))
                    .fitCenter()
                    .into(imageView);
        };
        cvImage.setImageListener(imageListener);
        cvImage.setIndicatorVisibility(View.GONE);
        cvImage.setPageCount(imageResult.size());

    }

    private void initEvent() {
        btnChosseImage.setOnClickListener(v -> {
            BottomMenuFragment menu = new BottomMenuFragment();
            menu.show(getSupportFragmentManager(), menu.getTag());
        });

        btnPhoto.setOnClickListener(v -> {
            Options options = Options.init()
                    .setRequestCode(SELECT_PHOTO)
                    .setCount(MAX_IMAGE)
                    .setFrontfacing(false)
                    .setImageQuality(ImageQuality.REGULAR)
                    .setScreenOrientation(Options.SCREEN_ORIENTATION_USER_PORTRAIT)
                    .setImageResolution(1280, 960)
                    .setPath("/imagePicker/images");
            Pix.start(this, options);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadingData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        System.out.println("DEVELOPER : " + requestCode + " " + resultCode);

        if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK && data != null){
            imageList = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);

            Intent intent = new Intent(this, PreviewImageActivity.class);
            intent.putExtra(PreviewImageActivity.DATA, imageList);
            startActivityForResult(intent, PREVIEW_IMAGE);
        }

        if (requestCode == PREVIEW_IMAGE & resultCode == RESULT_OK && data != null){
            imageResult = data.getStringArrayListExtra(DATA);
        }
    }
}
