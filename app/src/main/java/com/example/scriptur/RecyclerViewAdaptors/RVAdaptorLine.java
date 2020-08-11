package com.example.scriptur.RecyclerViewAdaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scriptur.Database.Line;
import com.example.scriptur.R;

import java.util.ArrayList;

public class RVAdaptorLine extends RecyclerView.Adapter<RVAdaptorLine.RVHolderLine> {

    Context context;
    ArrayList<Line> lines;

    public RVAdaptorLine(Context context, ArrayList<Line> lines) {
        this.context = context;
        this.lines = lines;
    }

    @NonNull
    @Override
    public RVHolderLine onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.line_row_layout, parent, false);
        RVHolderLine rvHolderLine = new RVHolderLine(view);
        return rvHolderLine;
    }

    @Override
    public void onBindViewHolder(@NonNull RVHolderLine holder, int position) {
        holder.CharacterName.setText(lines.get(position).getCharacter().getName());
        holder.dialog.setText(lines.get(position).getDialog());
    }

    @Override
    public int getItemCount() {
        return lines.size();
    }


    /**********************************************************************************************************************
     *                                  R E C Y C L E V I E W   H O L D E R
     **********************************************************************************************************************/

    public class RVHolderLine extends RecyclerView.ViewHolder {
        TextView CharacterName;
        TextView dialog;

        public RVHolderLine(@NonNull View itemView) {
            super(itemView);
            this.CharacterName = (TextView) itemView.findViewById(R.id.tvLineCharacterRow);
            this.dialog = (TextView) itemView.findViewById(R.id.tvLineDialogRow);
        }
    }
}
