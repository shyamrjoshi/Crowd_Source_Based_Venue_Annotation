package com.example.omnamahshivay.test2.model;

import android.graphics.Bitmap;

/**
 * Created by OM NAMAH SHIVAY on 12/15/2016.
 */

public class venueannotation {

    private String venue;
    private String coordinates;
    private String tags;
    private String PhotoURl;
    private String noiselevel;

    public String getNoiselevel() {
        return noiselevel;
    }

    public void setNoiselevel(String noiselevel) {
        this.noiselevel = noiselevel;
    }



    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    private Bitmap bitmap;

    public String getVenue() {

        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getPhotoURl() {
        return PhotoURl;
    }

    public void setPhotoURl(String photoURl) {
        PhotoURl = photoURl;
    }






}
