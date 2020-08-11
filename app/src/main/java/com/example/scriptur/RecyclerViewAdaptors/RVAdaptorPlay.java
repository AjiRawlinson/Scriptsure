package com.example.scriptur.RecyclerViewAdaptors;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scriptur.Database.Play;
import com.example.scriptur.R;

import java.util.ArrayList;

public class RVAdaptorPlay extends RecyclerView.Adapter<RVAdaptorPlay.RVHolderPlay> {

    Context context;
    ArrayList<Play> playList;
    OnRowListener rowListener;

    public RVAdaptorPlay(Context context, ArrayList<Play> playList, OnRowListener rowListener) {
        this.context = context;
        this.playList = playList;
        this.rowListener = rowListener;
    }

    @NonNull
    @Override
    public RVHolderPlay onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.play_row_layout, parent, false);
        RVAdaptorPlay.RVHolderPlay RVHolderPlay = new RVAdaptorPlay.RVHolderPlay(view, rowListener);
        return  RVHolderPlay;
    }

    @Override
    public void onBindViewHolder(@NonNull RVHolderPlay holder, int position) {
        holder.title.setText(playList.get(position).getTitle());
        holder.itemView.setBackgroundColor(Color.parseColor(playList.get(position).getColour()));
    }

    @Override
    public int getItemCount() {
        return playList.size();
    }


    /**********************************************************************************************************************
     *                                  R E C Y C L E V I E W   H O L D E R
     **********************************************************************************************************************/


    public class RVHolderPlay extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView title;
        OnRowListener rowListener;

        public RVHolderPlay(@NonNull View itemView, OnRowListener rowListener) {
            super(itemView);
            this.title = (TextView) itemView.findViewById(R.id.tvPlayRow);
            this.rowListener = rowListener;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }


        @Override
        public void onClick(View v) {
            rowListener.onRowClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            rowListener.onRowLongClick(getAdapterPosition(), v);
            return true;
        }
    }

    public interface OnRowListener {
        void onRowClick(int position);
        void onRowLongClick(int position, View v);
    }
}
