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
 * Created by obay on 7/14/2017.
 */

public class VideoList extends ArrayAdapter<Video> {

    public VideoList(@NonNull Context context, @LayoutRes int resource, @NonNull List<Video> objects) {
        super(context, resource, objects);
    }

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v =convertView;
        if(v==null){
            LayoutInflater inflater =(LayoutInflater) getContext().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
            v =inflater.inflate(R.layout.video_item,null);
        }
        Video vid = getItem(position);
        TextView title = (TextView) v.findViewById(R.id.vid_title);
        TextView time = (TextView) v.findViewById(R.id.time);
        TextView reso = (TextView) v.findViewById(R.id.reso);

        title.setText(vid.vidName);
        time.setText(vid.time);
        reso.setText(vid.reso);

        return v;
    }
}
