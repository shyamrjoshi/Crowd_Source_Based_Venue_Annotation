package com.csbva.aws;

import android.content.Context;
import android.os.AsyncTask;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.SubscribeRequest;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sns.model.DeleteTopicRequest;

//Class used to send message to SNS
public class SNS extends AsyncTask<String, String, String> {

    CognitoCachingCredentialsProvider credentialsProvider;
    AmazonSNSClient snsClient;

    //connect to sns securely
    public SNS(Context applicationContext) {
        credentialsProvider = new CognitoCachingCredentialsProvider(
                applicationContext,
                "Identity Pool ID", //
                Regions.US_WEST_2 // Region
        );
        snsClient = new AmazonSNSClient(credentialsProvider);
        snsClient.setRegion(Region.getRegion(Regions.US_WEST_2));
    }

    //publish the data to sns
    public void publish(String msg){
        //publish to an SNS topic
        System.out.print(msg);
        String topicArn = "topicArn";
        PublishRequest publishRequest = new PublishRequest(topicArn, msg);
        PublishResult publishResult = snsClient.publish(publishRequest);
        //print MessageId of message published to SNS topic
        System.out.println("MessageId - " + publishResult.getMessageId());
    }

    @Override
    protected String doInBackground(String... strings) {
        System.out.println("Inside b" + strings[0]);
        publish(strings[0]);
        return null;
    }
}