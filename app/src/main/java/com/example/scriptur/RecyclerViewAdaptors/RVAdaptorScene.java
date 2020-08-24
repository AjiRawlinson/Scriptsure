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

import com.example.scriptur.Database.Line;
import com.example.scriptur.Database.Scene;
import com.example.scriptur.R;

import java.util.ArrayList;

public class RVAdaptorScene extends RecyclerView.Adapter<RVAdaptorScene.RVHolderScene> {

    Context context;
    ArrayList<Scene> sceneList;
    ArrayList<Line> lineList;
    OnRowListener rowListener;
    Integer[] avgScoreArray, lineInSceneArray, userLineInSceneArray;

    public RVAdaptorScene(Context context, ArrayList<Scene> scenes, ArrayList<Line> lines, OnRowListener rowListener) {
        this.context = context;
        this.sceneList = scenes;
        this.lineList = lines;
        this.rowListener = rowListener;
        this.avgScoreArray = new Integer[scenes.size()];
        this.lineInSceneArray = new Integer[scenes.size()];
        this.userLineInSceneArray = new Integer[scenes.size()];

        int sceneIndex = 0;
        for(Scene scene: sceneList) {
            avgScoreArray[sceneIndex] = -1;
            lineInSceneArray[sceneIndex] = 0;
            userLineInSceneArray[sceneIndex] = 0;
            for(Line line: lineList) {
                if(line.getScene().getUID() == scene.getUID()) {
                    lineInSceneArray[sceneIndex]++;
                    if(line.getCharacter().isUserPart()) {
                        avgScoreArray[sceneIndex] += line.getScore();
                        userLineInSceneArray[sceneIndex]++;
                    }
                }
            }
            if(userLineInSceneArray[sceneIndex] != 0) {
                avgScoreArray[sceneIndex] = (avgScoreArray[sceneIndex] + 1) / userLineInSceneArray[sceneIndex];
            }
            sceneIndex++;
        }
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
        if(avgScoreArray[position] >= 0) { holder.data.setText("User Lines: " + userLineInSceneArray[position] + "/" + lineInSceneArray[position] + " Average Score: " + avgScoreArray[position]); }
        else { holder.data.setText("User Lines: " + userLineInSceneArray[position] + "/" + lineInSceneArray[position] + " Average Score: N/A"); }
        holder.itemView.setBackgroundColor(Color.parseColor(sceneList.get(position).getColour()));
    }

    @Override
    public int getItemCount() {
        return sceneList.size();
    }

    /**********************************************************************************************************************
     *                                  R E C Y C L E V I E W   H O L D E R
     **********************************************************************************************************************/


    public class RVHolderScene extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView name, data;
        ImageView image;
        OnRowListener rowListener;

        public RVHolderScene(@NonNull View itemView, OnRowListener rowListener) {
            super(itemView);
            this.name = (TextView) itemView.findViewById(R.id.tvSceneRow);
            this.data = (TextView) itemView.findViewById(R.id.tvSceneRowData);
            this.image = (ImageView) itemView.findViewById(R.id.ivSceneRow);
            this.rowListener = rowListener;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            rowListener.onRowClick(getAdapterPosition());
        }

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
