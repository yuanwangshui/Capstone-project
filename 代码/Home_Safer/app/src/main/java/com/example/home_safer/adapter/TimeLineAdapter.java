package com.example.home_safer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.home_safer.R;
import com.example.home_safer.model.TimeLineModel;

import java.util.List;

public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineViewHolder> {
    private List<TimeLineModel> mDataSet;
    private OnItemClickListener mClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }

    public TimeLineAdapter(List<TimeLineModel> models) {
        mDataSet = models;
    }
    @Override
    public TimeLineViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_time_line, viewGroup, false);
        return new TimeLineViewHolder(v, viewType);
    }

    @Override
    public void onBindViewHolder(TimeLineViewHolder timeLineViewHolder, int position) {
        timeLineViewHolder.setData(mDataSet.get(position));
        timeLineViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onItemClick(view,position);
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        final int size = mDataSet.size() - 1;
        if (size == 0)
            return ItemType.ATOM;
        else if (position == 0)
            return ItemType.START;
        else if (position == size)
            return ItemType.END;
        else return ItemType.NORMAL;
    }


    @Override
    public int getItemCount() {
        return mDataSet.size();
    }


}
