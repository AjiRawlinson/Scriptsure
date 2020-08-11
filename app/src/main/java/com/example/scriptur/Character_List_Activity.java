package com.example.scriptur;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.aware.Characteristics;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.scriptur.Database.Character;
import com.example.scriptur.Database.DBAdaptor;
import com.example.scriptur.RecyclerViewAdaptors.RVAdaptorCharacter;

import java.util.ArrayList;

public class Character_List_Activity extends AppCompatActivity implements RVAdaptorCharacter.OnRowListener {

    RecyclerView rvCharacter;
    ArrayList<Character> characterList;
    DBAdaptor DBA;
    RVAdaptorCharacter RVACharacter;
    TextView playTitle;
    int playid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_list);

        DBA = new DBAdaptor(this);
        Intent in = getIntent();
        playid = in.getIntExtra("PLAY_ID", 0);
        setTitle(DBA.getPlayByID(playid).getTitle());

        rvCharacter = (RecyclerView) findViewById(R.id.RVCharacter);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        rvCharacter.setLayoutManager(llm);
        DBAdaptor DBA = new DBAdaptor(this);
        characterList = DBA.getAllCharactersInPlay(playid);
        RVACharacter = new RVAdaptorCharacter(this, characterList, this);
        rvCharacter.setAdapter(RVACharacter);
    }

    public void addCharacterBtn(View v) {
        Intent in = new Intent(this, NewCharacterActivity.class);
        in.putExtra("PLAY_ID", playid);
        startActivity(in);
    }

    public void viewScenesBtn(View v) {
        Intent in = new Intent(this, Scene_List_Activity.class);
        in.putExtra("PLAY_ID", playid);
        startActivity(in);
    }

    @Override
    public void onRowLongClick(final int position, View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.getMenu().add("Edit");
        popup.getMenu().add("Delete");
        popup.show();

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.toString()) {
                    case "Edit":
                        Intent in = new Intent(Character_List_Activity.this, UpdateCharacterActivity.class);
                        in.putExtra("CHARACTER_ID", characterList.get(position).getUID());
                        startActivity(in);
                        break;
                    case "Delete":
                        int numberOfLines = DBA.getNumberOfLineByCharacter(characterList.get(position));
                        if(numberOfLines > 0) {
                            Toast.makeText(Character_List_Activity.this, "Character has " + numberOfLines + " Lines in this Play, please delete these Lines before deleteing Character ", Toast.LENGTH_LONG).show();
                        } else {
                            deleteCharacterConfirmation(position);
                        }
                        break;
                    default://do nothing
                        break;
                }
                return true;
            }
        });
    }

    public void deleteCharacterConfirmation(final int position) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        DBA.deleteCharacter(characterList.get(position));
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
//                        do nothing
                        break;
                    default://do nothing
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete")
                .setMessage("Are you sure you want to delete Character: " + characterList.get(position).getName())
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }
}
