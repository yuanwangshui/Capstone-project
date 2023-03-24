package com.example.home_safer.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.home_safer.R;
import com.example.home_safer.model.TimeLineModel;
import com.example.home_safer.view.TimeLineMarker;

public class TimeLineViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    private TextView mName;
    private ImageView mImage;

    public TimeLineViewHolder(View itemView,int type) {
        super(itemView);
        mName = (TextView) itemView.findViewById(R.id.item_time_line_txt);
        mImage = (ImageView) itemView.findViewById(R.id.imageview);

        TimeLineMarker mMarker = (TimeLineMarker) itemView.findViewById(R.id.item_time_line_mark);
        if (type == ItemType.ATOM) {
            mMarker.setBeginLine(null);
            mMarker.setEndLine(null);
        }
         else if (type == ItemType.END)
            mMarker.setBeginLine(null);
        else if (type == ItemType.START)
            mMarker.setEndLine(null);
    }
    public void setData(TimeLineModel data) {
        mName.setText("Name:" + data.getName() + " Age:" + data.getAge());
        if(data.getBitmap()!=null)
            mImage.setImageBitmap(data.getBitmap());
    }

    @Override
    public void onClick(View view) {
        System.out.println("有东西");
    }
}
