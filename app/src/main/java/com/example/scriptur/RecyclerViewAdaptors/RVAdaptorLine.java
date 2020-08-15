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
import com.example.scriptur.R;

import java.util.ArrayList;

public class RVAdaptorLine extends RecyclerView.Adapter<RVAdaptorLine.RVHolderLine> {

    Context context;
    ArrayList<Line> lines;
    OnRowListener rowListener;

    public RVAdaptorLine(Context context, ArrayList<Line> lines, OnRowListener rowListener) {
        this.context = context;
        this.lines = lines;
        this.rowListener = rowListener;
    }

    @NonNull
    @Override
    public RVHolderLine onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.line_row_layout, parent, false);
        RVHolderLine rvHolderLine = new RVHolderLine(view, rowListener);
        return rvHolderLine;
    }

    @Override
    public void onBindViewHolder(@NonNull RVHolderLine holder, int position) {
        holder.CharacterName.setText(lines.get(position).getCharacter().getName());
        holder.dialog.setText(lines.get(position).getDialog());
        holder.itemView.setBackgroundColor(Color.parseColor(lines.get(position).getCharacter().getColour()));
        if(lines.get(position).getCharacter().isUserPart()) {
            if(lines.get(position).getScore() >= 0) { holder.data.setText("Last Score: " +lines.get(position).getScore()); }
            else { holder.data.setText("Last Score: N/A"); }
            holder.starImage.setImageResource(android.R.drawable.star_on);
        }
        if(lines.get(position).getCharacter().getGender().equalsIgnoreCase("male")) {
            holder.genderImage.setImageResource(R.drawable.male_actor);
        } else if(lines.get(position).getCharacter().getGender().equalsIgnoreCase("female")) {
            holder.genderImage.setImageResource(R.drawable.female_actor);
        } else { holder.genderImage.setImageResource(R.drawable.unisex_actor); }
    }

    @Override
    public int getItemCount() {
        return lines.size();
    }


    /**********************************************************************************************************************
     *                                  R E C Y C L E V I E W   H O L D E R
     **********************************************************************************************************************/

    public class RVHolderLine extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        TextView CharacterName;
        TextView dialog;
        TextView data;
        ImageView genderImage;
        ImageView starImage;
        OnRowListener rowListener;

        public RVHolderLine(@NonNull View itemView, OnRowListener rowListener) {
            super(itemView);
            this.CharacterName = (TextView) itemView.findViewById(R.id.tvLineCharacterRow);
            this.dialog = (TextView) itemView.findViewById(R.id.tvLineDialogRow);
            this.data = (TextView) itemView.findViewById(R.id.tvLineRowData);
            this.genderImage = (ImageView) itemView.findViewById(R.id.ivLineRow);
            this.starImage = (ImageView) itemView.findViewById(R.id.ivUserLineRow1);

            this.rowListener = rowListener;

            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            rowListener.onRowLongClick(getAdapterPosition(), v);
            return true;
        }
    }

    public interface OnRowListener {
//        void OnRowClick(int position) // maybe to reveal user line
        void onRowLongClick(int position, View v);
    }
}
