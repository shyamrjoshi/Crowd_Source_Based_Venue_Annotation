package com.example.omnamahshivay.test2;

import android.widget.Button;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by OM NAMAH SHIVAY on 12/14/2016.
 */

//This class is used to send http get and http post request

public class HttpManager {

    public static String getData(RequestPackage p) {
        //generate the url
        BufferedReader reader = null;
        String uri = p.getUri();
        if (p.getMethod().equals("GET")){
            uri += "?" + p.getEncodedParams();
        }
        try {

            URL url = new URL(uri);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(p.getMethod());

            StringBuilder sb = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null){
                sb.append(line + "\n");
            }
            return sb.toString();
        }catch (Exception e){
            e.printStackTrace();
            return null;

        }
        finally {
            if (reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }



    }

}
