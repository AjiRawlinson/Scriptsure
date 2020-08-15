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

import com.example.scriptur.Database.Character;
import com.example.scriptur.Database.Line;
import com.example.scriptur.R;

import java.util.ArrayList;

public class RVAdaptorCharacter extends RecyclerView.Adapter<RVAdaptorCharacter.RVHolderCharacter> {

    Context context;
    ArrayList<Character> characterList;
    ArrayList<Line> lineList;
    OnRowListener rowListener;


    public RVAdaptorCharacter(Context context, ArrayList<Character> characterList, ArrayList<Line> lineList, OnRowListener rowListener) {
        this.context = context;
        this.characterList = characterList;
        this.lineList = lineList;
        this.rowListener = rowListener;
    }

    @NonNull
    @Override
    public RVHolderCharacter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.character_row_layout, parent, false);
        RVHolderCharacter RVHolderCharacter = new RVHolderCharacter(view, rowListener);
        return RVHolderCharacter;
    }

    @Override
    public void onBindViewHolder(@NonNull RVHolderCharacter holder, int position) {
        holder.name.setText(characterList.get(position).getName());
        holder.itemView.setBackgroundColor(Color.parseColor(characterList.get(position).getColour()));
        int numOfLines = 0;
        for(Line line: lineList) {
            if(line.getCharacter().getUID() == characterList.get(position).getUID()) { numOfLines++; }
        }
        holder.data.setText("Lines = " + numOfLines);
        if(characterList.get(position).isUserPart()) { holder.starImage.setImageResource(android.R.drawable.star_on); }
        if(characterList.get(position).getGender().equalsIgnoreCase("male")) { holder.genderImage.setImageResource(R.drawable.male_actor); }
        else if(characterList.get(position).getGender().equalsIgnoreCase("female")) { holder.genderImage.setImageResource(R.drawable.female_actor); }
        else { holder.genderImage.setImageResource(R.drawable.unisex_actor); }

    }


    @Override
    public int getItemCount() {
        return characterList.size();
    }


    /**********************************************************************************************************************
     *                                  R E C Y C L E V I E W   H O L D E R
     **********************************************************************************************************************/


    public class RVHolderCharacter extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        TextView name;
        TextView data;
        ImageView genderImage;
        ImageView starImage;
        OnRowListener rowListener;


        public RVHolderCharacter(@NonNull View itemView, OnRowListener rowListener) {
            super(itemView);
            this.name = (TextView) itemView.findViewById(R.id.tvCharacterRow);
            this.data = (TextView) itemView.findViewById(R.id.tvCharacterDataRow);
            this.genderImage = (ImageView) itemView.findViewById(R.id.ivCharacterRow);
            this.starImage = (ImageView) itemView.findViewById(R.id.ivCharacterStarRow1);
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
        void onRowLongClick(int position, View v);
    }
}
