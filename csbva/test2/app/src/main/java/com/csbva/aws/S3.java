package com.csbva.aws;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.sns.AmazonSNSClient;

import java.io.File;

//Class to upload images to s3
public class S3 extends AsyncTask<String, String, String> {
    Context applicationContext;
    CognitoCachingCredentialsProvider credentialsProvider;
    AmazonS3 s3;

    //connect to s3 securely
    public S3(Context applicationContext) {
        this.applicationContext = applicationContext;
        credentialsProvider = new CognitoCachingCredentialsProvider(
                applicationContext,
                "Identity Pool ID", // Identity Pool ID
                Regions.US_WEST_2 // Region
        );
        s3 = new AmazonS3Client(credentialsProvider);
    }
    //Upload images to sr
    public void upload(String bucketName, String fileName) {
        try {
            TransferUtility transferUtility = new TransferUtility(s3, applicationContext);
            TransferObserver observer = transferUtility.upload(
                    bucketName,     /* The bucket to upload to */
                    new File(fileName).getName(),    /* The key for the uploaded object */
                    new File(fileName)        /* The file where the data to upload exists */
            );

        }
        catch(Exception e) {
            Log.e("Error",e.getMessage());
        }
    }

    @Override
    protected String doInBackground(String... strings) {
        upload(strings[0], strings[1]);
        return null;
    }


}
