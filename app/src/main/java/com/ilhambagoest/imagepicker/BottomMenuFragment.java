package com.ilhambagoest.imagepicker;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

import at.markushi.ui.CircleButton;

public class BottomMenuFragment extends BottomSheetDialogFragment {

    private CircleButton btnTakePhoto, btnPickGallery;

    public BottomMenuFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_menu, container, false);


        btnTakePhoto = view.findViewById(R.id.btn_take_photo);
        btnPickGallery = view.findViewById(R.id.btn_pick_gallery);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
                if (dialog != null){
                    FrameLayout bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                    if (bottomSheet != null){
                        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
                        behavior.setFitToContents(true);
                        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                }
            }
        });

        btnTakePhoto.setOnClickListener(v -> {
            ImagePicker.cameraOnly().start(this);
        });

        btnPickGallery.setOnClickListener(v -> {
            ImagePicker.create(this)
                    .returnMode(ReturnMode.CAMERA_ONLY) // set whether pick and / or camera action should return immediate result or not.
                    .folderMode(false) // folder mode (false by default)
                    .toolbarFolderTitle("Folder") // folder selection title
                    .toolbarImageTitle("Tap to select") // image selection title
                    .toolbarArrowColor(Color.WHITE) // Toolbar 'up' arrow color
                    .theme(R.style.ip_AppTheme)
                    .includeVideo(false) // Show video on image picker
                    .multi() // multi mode (default mode)
                    .limit(4) // max images can be selected (99 by default)
                    .showCamera(false) // show camera or not (true by default)
                    .imageDirectory("Camera") // directory name for captured image  ("Camera" folder by default)
                    .enableLog(false) // disabling log
                    .start(); // start image picker activity with request code
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (ImagePicker.shouldHandle(requestCode, resultCode, data)){
            List<Image> images = ImagePicker.getImages(data);

            /*ImageListener imageListener = (position, imageView) -> Glide.with(this)
                    .asBitmap()
                    .load(images.get(position).getPath())
                    .into(imageView);
            cvImage.setImageListener(imageListener);
            cvImage.setIndicatorVisibility(View.VISIBLE);
            if (images.size() != 0){
                cvImage.setPageCount(images.size());
            } else {
                System.out.println("DEVELOPER : Just One Photo");
            }*/
        }
    }
}
