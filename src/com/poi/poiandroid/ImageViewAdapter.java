package com.poi.poiandroid;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by QuangThai on 6/9/2017.
 */

public class ImageViewAdapter extends ArrayAdapter<Bitmap>
{
    private Activity context;
    private int resource;
    private List<Bitmap> objects;

    public ImageViewAdapter(Activity context, int resource, List<Bitmap> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflate = this.context.getLayoutInflater();
        View row = inflate.inflate(this.resource, null);
        ImageView img = (ImageView) row.findViewById(R.id.imgSlide);
        final Bitmap bmpSlide = this.objects.get(position);
        img.setImageBitmap(bmpSlide);
        return row;
    }
}
