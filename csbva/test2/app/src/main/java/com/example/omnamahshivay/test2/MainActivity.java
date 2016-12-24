package com.example.omnamahshivay.test2;

import android.app.ListActivity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.omnamahshivay.test2.model.venueannotation;
import com.example.omnamahshivay.test2.parser.jsondata;

import org.json.JSONException;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

//Activity for search screen
public class MainActivity extends ListActivity {

    TextView output;
    ProgressBar pb;
    EditText search;
    List<venueannotation> venuelist;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        output = (TextView) findViewById(R.id.textView);
        pb = (ProgressBar) findViewById(R.id.progressBar2);
        pb.setVisibility(View.INVISIBLE);

        //imageView = (ImageView) findViewById(R.id.imageView1);

    }


//    public void onClickGetData(View view){
//        System.out.println("button clicked");
//        if (isOnline()) {
//            requestData("http://127.0.0.1:5000/search");
//        } else {
//            Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
//
//        }
//
//    }


    //On click of search button, send a get request to fetch the data for the input keyword.
    private void requestData(String uri) {

        RequestPackage p = new RequestPackage();
        p.setMethod("GET");
        p.setUri(uri);
        String keyword = search.getText().toString();
        p.setParams("q",keyword);



        MyTask task = new MyTask();
        task.execute(p);
    }

    //Function to display the search results that are returned on to the list
    protected void updateDisplay() {
        //output.append(message + "\n");
//        if (venuelist != null) {
//            for (venueannotation venue_obj : venuelist) {
//                output.append(venue_obj.getVenue() + "\n");
//            }
//        }



        venueAdapter adapter = new venueAdapter(this,R.layout.item_venue,venuelist);

        setListAdapter(adapter);

//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(MainActivity.this, "Image view clicked", Toast.LENGTH_LONG).show();
//            }
//        });




    }

    //function to check and request internet access
    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    //Asynchronous class which sends the async get request .
    private class MyTask extends AsyncTask<RequestPackage, String, List<venueannotation>> {

        @Override
        protected void onPreExecute() {
            //updateDisplay("Starting task");


                pb.setVisibility(View.VISIBLE);

        }

        @Override
        protected List<venueannotation> doInBackground(RequestPackage... params) {

            String content = HttpManager.getData(params[0]);
            try {
                venuelist = jsondata.parseinputData(content);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            int i = 0;
            for (venueannotation venue_obj : venuelist){

                try {
                    String photoURL = venue_obj.getPhotoURl();
                    InputStream in = (InputStream) new URL(photoURL).getContent();
                    Bitmap bitmap = null;
                    try {
                       bitmap = BitmapFactory.decodeStream(in);
                    }
                    catch (Exception e){

                        e.printStackTrace();
                    }
                    venue_obj.setBitmap(bitmap);
                    in.close();

                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            return venuelist;
        }

        @Override
        protected void onPostExecute(List<venueannotation> result) {
            if (result == null) {
                Toast.makeText(MainActivity.this, "Web service not available", Toast.LENGTH_LONG).show();
                return;
            }

            venuelist = result;
            updateDisplay();

            pb.setVisibility(View.INVISIBLE);

        }

        @Override
        protected void onProgressUpdate(String... values) {
          //  updateDisplay(values[0]);
        }

    }


    //onclick function for the searh button
    public void handledata(View view){
        System.out.println("button clicked");
        //get the text from the search field
        //if search field is empty then display message select something to search
        //else send get request with the keyword
        search = (EditText)findViewById(R.id.editText3);
        String keyword = search.getText().toString();
        if (keyword.isEmpty()){
            Toast.makeText(this, "Enter a text to search", Toast.LENGTH_LONG).show();
        }
        else {
            if (isOnline()) {
                requestData("https://search-csbva-4ztsupaq6tc4jbt2e6ax6ehlra.us-west-2.es.amazonaws.com/venue_annotations/venues/_search");
            } else {
                Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();

            }
        }
    }
}
