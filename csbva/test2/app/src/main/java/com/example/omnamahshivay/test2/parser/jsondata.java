package com.example.omnamahshivay.test2.parser;

import com.example.omnamahshivay.test2.model.venueannotation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by OM NAMAH SHIVAY on 12/15/2016.
 */

public class jsondata {

    public static List<venueannotation> parseinputData(String content) throws JSONException {

//        System.out.println(content);
//        JSONArray ar = new JSONArray(content);
//        System.out.println(ar);
        List<venueannotation> venue_list = new ArrayList<>();
//
//        for (int i = 0; i<ar.length();i++){
//            JSONObject obj = ar.getJSONObject(i);
//            venueannotation venue_local = new venueannotation();
//            venue_local.setCoordinates(obj.getString("coordinates"));
//            venue_local.setPhotoURl(obj.getString("PhotoURl"));
//            venue_local.setTags(obj.getString("tags"));
//            venue_local.setVenue(obj.getString("venue"));
//            venue_list.add(venue_local);
//        }
      //  return venue_list;

        JSONObject json = new JSONObject(content);
        JSONObject hitsObj = json.getJSONObject("hits");
        JSONArray hitsArr = hitsObj.getJSONArray("hits");
        JSONObject first = hitsArr.getJSONObject(0); // assumes 1 entry in hits array
        JSONObject source_test = null;
        for (int i=0; i<hitsArr.length(); i++) {
            JSONObject h = hitsArr.getJSONObject(i);
            source_test = h.getJSONObject("_source");
            venueannotation venue_local = new venueannotation();
            if(source_test.has("location")){
                venue_local.setCoordinates(source_test.getString("location"));
            }
            if(source_test.has("url")){
                venue_local.setPhotoURl(source_test.getString("url"));
            }
            if (source_test.has("annotations")){
                venue_local.setTags(source_test.getString("annotations"));
            }
            if(source_test.has("venue")){
                venue_local.setVenue(source_test.getString("venue"));
            }
            if(source_test.has("noise_level")){
                venue_local.setNoiselevel(source_test.getString("noise_level"));
            }
            venue_list.add(venue_local);
            //string object = (source.getString("the string you want to get"));

        }

//        JSONObject source = first.getJSONObject("_source");
//        //JSONObject phone = source.getJSONObject("phone");
//
//        String venue = source.getString("venue");
//        //String name = source.getString("name");
//       // String mobile = phone.getString("mobile");
//       // String home = phone.getString("home");
//        venueannotation venue_local = new venueannotation();
//          //  venue_local.setCoordinates(obj.getString("coordinates"));
//           // venue_local.setPhotoURl(obj.getString("PhotoURl"));
//           // venue_local.setTags(obj.getString("tags"));
//            venue_local.setVenue(venue);
//           venue_list.add(venue_local);
//        System.out.println(venue);
        return venue_list;

    }
}
