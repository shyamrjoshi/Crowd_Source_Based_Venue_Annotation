package com.example.omnamahshivay.test2;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.content.pm.PackageManager;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import static android.app.Activity.RESULT_OK;

/**
 * Created by OM NAMAH SHIVAY on 12/17/2016.
 */

public class takephoto extends Activity{


    //private static final int REQUEST_IMAGE_CAPTURE = ;
    private static final int ACTIVITY_START_CAMERA_APP = 0;

    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent();
        takePictureIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent,ACTIVITY_START_CAMERA_APP);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACTIVITY_START_CAMERA_APP && resultCode == RESULT_OK) {
            Toast.makeText(this,"Picture taken",Toast.LENGTH_SHORT).show();
//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            ImageView mImageView;
//            mImageView = View.findViewById(R.id.imageView);
//            mImageView.setImageBitmap(imageBitmap);
        }
    }


}
