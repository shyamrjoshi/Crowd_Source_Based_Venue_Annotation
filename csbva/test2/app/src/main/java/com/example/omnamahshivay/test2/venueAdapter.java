package com.example.omnamahshivay.test2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.omnamahshivay.test2.model.venueannotation;

import java.util.List;

/**
 * Created by OM NAMAH SHIVAY on 12/15/2016.
 */
//This class is used to parse the input data and display the results on to a listview

public class venueAdapter extends ArrayAdapter{

    private Context context;
    @SuppressWarnings("unused")
    private List<venueannotation> venuelist;

    public venueAdapter(Context context,int resource, List<venueannotation> objects){
        super(context,resource,objects);
        this.context = context;
        this.venuelist = objects;

    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_venue,parent,false);


        venueannotation venue_obj = venuelist.get(position);
        TextView tv = (TextView) view.findViewById(R.id.textView1);
        tv.setText(venue_obj.getVenue());
        TextView tv1 = (TextView) view.findViewById(R.id.textView2);
        tv1.setText(venue_obj.getNoiselevel());
        String noise_level = venue_obj.getNoiselevel();
//        if (noise_level.equals("Noisy")) {
//            tv.setTextColor(Color.RED);
//        }
//        else {
//                if (noise_level.equals("Normal")) {
//                    tv.setTextColor(Color.YELLOW);
//                }
//                else
//                {
//                    if (noise_level.equals("Silent")){
//                            tv.setTextColor(Color.GREEN);
//                        }
//                    }
//            }

        ImageView image = (ImageView) view.findViewById(R.id.imageView1);
        image.setImageBitmap(venue_obj.getBitmap());


//        image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v){
//                System.out.println("image clicked...");//check logcat
//                //Intent intent = new Intent(venueAdapter.super.getContext(),detailview.class);
//                //startActivity(intent);
//            }
//        });

        return view;
    }
}
