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
    public static int selectedPosition;
    boolean hideLines;

    public RVAdaptorLine(Context context, ArrayList<Line> lines, boolean hideLines, int selectedPosition, OnRowListener rowListener) {
        this.context = context;
        this.lines = lines;
        this.hideLines = hideLines;
        this.selectedPosition = selectedPosition;
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
        if(selectedPosition == position) {
            holder.itemView.setBackgroundColor(Color.parseColor(lines.get(position).getCharacter().getColour()));
        } else { holder.itemView.setBackgroundColor(Color.parseColor("#10" + lines.get(position).getCharacter().getColour().substring(1))); }
        if(lines.get(position).getCharacter().isUserPart()) {
            if(lines.get(position).getScore() >= 0) { holder.data.setText("Last Score: " +lines.get(position).getScore()); }
            else { holder.data.setText("Last Score: N/A"); }
            holder.starImage.setImageResource(android.R.drawable.star_on);
        }
        switch (lines.get(position).getCharacter().getAvatarCode()) {
            case "female 1":
                holder.avatarImage.setImageResource(R.drawable.female1);
                break;
            case "female 2":
                holder.avatarImage.setImageResource(R.drawable.female2);
                break;
            case "female 3":
                holder.avatarImage.setImageResource(R.drawable.female3);
                break;
            case "female 4":
                holder.avatarImage.setImageResource(R.drawable.female4);
                break;
            case "female 5":
                holder.avatarImage.setImageResource(R.drawable.female5);
                break;
            case "female 6":
                holder.avatarImage.setImageResource(R.drawable.female6);
                break;
            case "male 1":
                holder.avatarImage.setImageResource(R.drawable.male1);
                break;
            case "male 2":
                holder.avatarImage.setImageResource(R.drawable.male2);
                break;
            case "male 3":
                holder.avatarImage.setImageResource(R.drawable.male3);
                break;
            case "male 4":
                holder.avatarImage.setImageResource(R.drawable.male4);
                break;
            case "male 5":
                holder.avatarImage.setImageResource(R.drawable.male5);
                break;
            case "male 6":
                holder.avatarImage.setImageResource(R.drawable.male6);
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return lines.size();
    }


    /**********************************************************************************************************************
     *                                  R E C Y C L E V I E W   H O L D E R
     **********************************************************************************************************************/

    public class RVHolderLine extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {
        TextView CharacterName;
        TextView dialog;
        TextView data;
        ImageView avatarImage;
        ImageView starImage;
        OnRowListener rowListener;

        public RVHolderLine(@NonNull View itemView, OnRowListener rowListener) {
            super(itemView);
            this.CharacterName = (TextView) itemView.findViewById(R.id.tvLineCharacterRow);
            this.dialog = (TextView) itemView.findViewById(R.id.tvLineDialogRow);
            this.data = (TextView) itemView.findViewById(R.id.tvLineRowData);
            this.avatarImage = (ImageView) itemView.findViewById(R.id.ivLineRow);
            this.starImage = (ImageView) itemView.findViewById(R.id.ivUserLineRow1);

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
