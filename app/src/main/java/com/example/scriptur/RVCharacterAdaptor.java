package com.example.scriptur;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scriptur.Database.Character;

import java.util.ArrayList;

public class RVCharacterAdaptor extends RecyclerView.Adapter<RVCharacterAdaptor.RVHolderCharacter> {

    Context context;
    ArrayList<Character> characterList;
//    OnRowListener rowListener;


    public RVCharacterAdaptor(Context context, ArrayList<Character> characterList) {
        this.context = context;
        this.characterList = characterList;
    }

    @NonNull
    @Override
    public RVHolderCharacter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.character_row_layout, parent, false);
        RVHolderCharacter RVHolderCharacter = new RVHolderCharacter(view);
        return RVHolderCharacter;
    }

    @Override
    public void onBindViewHolder(@NonNull RVHolderCharacter holder, int position) {
        holder.name.setText(characterList.get(position).getName());
        //if(gender == male) {
        holder.image.setImageResource(R.drawable.male_actor);

    }


    @Override
    public int getItemCount() {
        return characterList.size();
    }


    /**********************************************************************************************************************
     *                                  R E C Y C L E V I E W   H O L D E R
     **********************************************************************************************************************/


    public class RVHolderCharacter extends RecyclerView.ViewHolder {
        TextView name;
        ImageView image;
//        OnRowListener rowListener;


        public RVHolderCharacter(@NonNull View itemView) {
            super(itemView);
            this.name = (TextView) itemView.findViewById(R.id.tvCharacterRow);
            this.image = (ImageView) itemView.findViewById(R.id.ivCharacterRow);
            //this.rowListener = rowListener;

        }
    }
}
