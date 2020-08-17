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
        for (Line line : lineList) {
            if (line.getCharacter().getUID() == characterList.get(position).getUID()) {
                numOfLines++;
            }
        }
        holder.data.setText("Lines: " + numOfLines);
        if (characterList.get(position).isUserPart()) {
            holder.starImage.setImageResource(android.R.drawable.star_on);
        }
        switch (characterList.get(position).getAvatarCode()) {
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
        return characterList.size();
    }


    /**********************************************************************************************************************
     *                                  R E C Y C L E V I E W   H O L D E R
     **********************************************************************************************************************/


    public class RVHolderCharacter extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        TextView name;
        TextView data;
        ImageView avatarImage;
        ImageView starImage;
        OnRowListener rowListener;


        public RVHolderCharacter(@NonNull View itemView, OnRowListener rowListener) {
            super(itemView);
            this.name = (TextView) itemView.findViewById(R.id.tvCharacterRow);
            this.data = (TextView) itemView.findViewById(R.id.tvCharacterDataRow);
            this.avatarImage = (ImageView) itemView.findViewById(R.id.ivCharacterRow);
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
