package com.example.omnamahshivay.test2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.csbva.aws.S3;
import com.csbva.aws.SNS;
import com.example.omnamahshivay.test2.model.VenueInfo;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

//Class to capture images,audio and GPS location
public class Capture extends AppCompatActivity {

    private static final int ACTIVITY_START_CAMERA_APP = 0;
    private ImageView mImageview;
    private MediaRecorder mRecorder = null;
    private static String mFileName = null;
    private static final String LOG_TAG = "AudioRecordTest";
    private LocationManager locationManager;
    private LocationListener locationListener;
    private String mImageFileLocation;
    //private int amplitude;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //fetch_current_location();
        setContentView(R.layout.activity_capture);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Button btncapture;
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/audiorecordtest.3gp";
        mImageview = (ImageView) findViewById(R.id.imageView);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        // Navigate to the search activity on click of the search button
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent myIntent = new Intent(Capture.this, MainActivity.class);
                // myIntent.putExtra("key", value); //Optional parameters
                Capture.this.startActivity(myIntent);

            }
        });

        btncapture = (Button) findViewById(R.id.btnCapture);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }
    //Function which is called when capture button is pressed.
    public void capture_data(View view) {
        System.out.println("capture button clicked");

        //fetch_current_location();
        //Call take_photo to capture the image from the camera
        take_photo();
        //Call take_audio which records the audio
        take_audio();
        //Call fetch_current_location which get the user's current location
        fetch_current_location();
        //capture_start_processes();
//        Intent intent =  new Intent(this,processbackground.class);
//        startService(intent);
    }


    //Function to take photo using the device camera
    private void take_photo() {
        //Initialize a new intent and start the camera intent on a seperate thread
        Intent takePictureIntent = new Intent();

        takePictureIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        //Save the image to the picture directory of the phone
        //The image is saved so that high quality images are uploaded to s3
        File photofile = null;
        try{
            photofile = createimagefile();
        }catch (IOException e){
            e.printStackTrace();
        }
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(photofile));

        //starts the camera intent on a new thread
        startActivityForResult(takePictureIntent, ACTIVITY_START_CAMERA_APP);

    }

    //Function which is invoked when the user camera action is complete
    //Gets the result from the camera intent and store the files in s3
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACTIVITY_START_CAMERA_APP && resultCode == RESULT_OK) {
            Toast.makeText(this, "Picture taken", Toast.LENGTH_SHORT).show();

            //Bundle extras = data.getExtras();
            //Bitmap photocapture = (Bitmap) extras.get("data");
            //mImageview.setImageBitmap(photocapture);
            Bitmap photocapture = BitmapFactory.decodeFile(mImageFileLocation);
            mImageview.setImageBitmap(photocapture);
            Uri tempUri = getImageUri(getApplicationContext(), photocapture);
            String filename = new File(getRealPathFromURI(tempUri)).getName();
            String s3url = "https://s3-us-west-2.amazonaws.com/crowdsourcebasedvenueannotation/"+filename;
            VenueInfo.url = s3url;
            try {
                //initialize the s3 object to store the picture in aws s3
                S3 s3 = new S3(getApplicationContext());
                s3.execute("crowdsourcebasedvenueannotation", getRealPathFromURI(tempUri));

                //Call the SNS class to send a message to SNS,
                //the message contains the s3 image url,gps coordinates and the audio amplitude
                SNS sns = new SNS(getApplicationContext());
                JSONObject msg = new JSONObject();

                msg.put("ll", VenueInfo.ll);
                JSONArray images_array = new JSONArray();
                images_array.put(VenueInfo.url);
                msg.put("images", images_array);
                int amplitude = mRecorder.getMaxAmplitude();
                msg.put("audio_amplitude", amplitude);
               // msg.toString();
             //   System.out.println("I got this   " + msg.toString());
              //  String send_message = "{\"ll\":\"40.6946,-73.9856\",\"images\":[\"https://s3-us-west-2.amazonaws.com/crowdsourcebasedvenueannotation/"+filename+"\"]}";
                //String send_message = "{\"ll\":\"40.6946,-73.9856\",\"images\":[\"https://s3-us-west-2.amazonaws.com/crowdsourcebasedvenueannotation/"+filename+"\"]}";
                // String[] s = new String[] {"ll", "40.7589,-73.9851" , "7676"};

                sns.execute(msg.toString());

            } catch (Exception e) {
                Log.e("Error", e.getMessage());
            }
        }
    }

    //function to get the image uri
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    //function to get the image path from uri
    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    //function to release the camera intent
    private void capture_start_processes() {
        takephoto tp = new takephoto();
        tp.dispatchTakePictureIntent();
        //call capture camera
        //Intent intent =  new Intent(this,processbackground.class);
        //capture_start_processes(intent);
//        pb.execute();
        //call capture microphone

        //call gps get location


    }
//    public void capture_audio(View view)
//    {
//        System.out.println("capture audio button clicked");
//        take_audio();
//
//        //capture_start_processes();
////        Intent intent =  new Intent(this,processbackground.class);
////        startService(intent);
//    }

    //function to take the audio using the device microphone
    //Mediarecorder intent is created to record the audio in a new thread

    private void take_audio() {
        //initialize the media recorder
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
        //start the media recorder
        mRecorder.start();
        int amplitude = mRecorder.getMaxAmplitude();
        //Audio is recorded for 10 seconds.
        CountDownTimer countDowntimer = new CountDownTimer(10000, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            //function call when media recorder stops recording
            public void onFinish() {
                //Toast.makeText(this, "Picture taken", Toast.LENGTH_SHORT).show();
                try {
                    stop();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        };
        countDowntimer.start();
    }

    //Function to stop the media recorder and release the media recorder when recording is completed
    public void stop() throws IOException {
        Toast.makeText(this, "Stop audio", Toast.LENGTH_SHORT).show();
        int amplitude = mRecorder.getMaxAmplitude();
        System.out.println(amplitude);
        mRecorder.stop();
        mRecorder.release();
    }

    //Function to fethc the current location of the user using tGPS
    private void fetch_current_location() {

        //initialize the location manager
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // textView.append("\n" + location.getAltitude() + " " + location.getLongitude());
                System.out.println(location.getLatitude() + " " + location.getLongitude());
                String lat_long = ""+location.getLatitude()+","+location.getLongitude();
                VenueInfo.ll = lat_long;
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        //request location
        locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);
    }

    @Override

    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Capture Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.omnamahshivay.test2/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Capture Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.omnamahshivay.test2/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
    //function to create the image file
    File createimagefile() throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imagefilename = "Image_"+ timestamp + "_";
        File storagedirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imagefilename,".jpg",storagedirectory);
        mImageFileLocation = image.getAbsolutePath();
        return image;
    }

}
