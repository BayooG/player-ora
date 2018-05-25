package com.example.obaydaba.sear;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by obay on 6/29/2017.
 */

public class GridViewAdaptor extends ArrayAdapter<Product> {

    public GridViewAdaptor(@NonNull Context context, @LayoutRes int resource, @NonNull List<Product> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v =convertView;
        if(v==null){
            LayoutInflater inflater =(LayoutInflater) getContext().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
            v =inflater.inflate(R.layout.grid_item,null);
        }
        Product product = getItem(position);
        ImageView img = (ImageView) v.findViewById(R.id.imageView);
        TextView txTitle = (TextView) v.findViewById(R.id.txtitle);
        TextView artist = (TextView) v.findViewById(R.id.artist);

        img.setImageDrawable(product.cover);
        txTitle.setText(product.title);
        artist.setText(product.artist);
        return v;
    }
}
