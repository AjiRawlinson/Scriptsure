package com.example.scriptur.RecyclerViewAdaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scriptur.Database.Scene;
import com.example.scriptur.R;

import java.util.ArrayList;

public class RVAdaptorScene extends RecyclerView.Adapter<RVAdaptorScene.RVHolderScene> {

    Context context;
    ArrayList<Scene> sceneList;
    OnRowListener rowListener;

    public RVAdaptorScene(Context context, ArrayList<Scene> scenes, OnRowListener rowListener) {
        this.context = context;
        this.sceneList = scenes;
        this.rowListener = rowListener;
    }

    @NonNull
    @Override
    public RVHolderScene onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.scene_row_layout, parent, false);
        RVHolderScene RVHolderScene = new RVHolderScene(view, rowListener);
        return  RVHolderScene;
    }

    @Override
    public void onBindViewHolder(@NonNull RVHolderScene holder, int position) {
        holder.name.setText(sceneList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return sceneList.size();
    }

    /**********************************************************************************************************************
     *                                  R E C Y C L E V I E W   H O L D E R
     **********************************************************************************************************************/


    public class RVHolderScene extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name;
        ImageView image;
        OnRowListener rowListener;

        public RVHolderScene(@NonNull View itemView, OnRowListener rowListener) {
            super(itemView);
            this.name = (TextView) itemView.findViewById(R.id.tvSceneRow);
            this.image = (ImageView) itemView.findViewById(R.id.ivSceneRow);
            this.rowListener = rowListener;

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            rowListener.onRowClick(getAdapterPosition());
        }
    }

    public interface OnRowListener {
        void onRowClick(int position);
    }
}
