package com.example.scriptur;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.Toast;

import com.example.scriptur.Database.DBAdaptor;
import com.example.scriptur.Database.Play;
import com.example.scriptur.Database.Scene;
import com.example.scriptur.RecyclerViewAdaptors.RVAdaptorPlay;

import java.util.ArrayList;

public class Play_List_Activity extends AppCompatActivity implements RVAdaptorPlay.OnRowListener {

    RecyclerView rvPlay;
    ArrayList<Play> playList;
    DBAdaptor DBA;
    RVAdaptorPlay RVAPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list);
        setTitle("Play Library");

        DBA = new DBAdaptor(this);
        playList = DBA.getAllPlays();
        ArrayList<Scene> sceneList = DBA.getAllScenes();

        rvPlay = (RecyclerView) findViewById(R.id.RVPlay);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        rvPlay.setLayoutManager(llm);
        RVAPlay = new RVAdaptorPlay(this, playList, sceneList, this);
        rvPlay.setAdapter(RVAPlay);
    }

    public void newPlayBtn(View v) {
        Intent in = new Intent(this, NewPlayActivity.class);
        startActivity(in);
    }

    @Override
    public void onRowClick(int position) {
        Intent in = new Intent(this, Scene_List_Activity.class);
        in.putExtra("PLAY_ID", playList.get(position).getUID());
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
                        Intent in = new Intent(Play_List_Activity.this, UpdatePlayActivity.class);
                        in.putExtra("PLAY_ID", playList.get(position).getUID());
                        startActivity(in);
                        break;
                    case "Delete":
                        deletePlayConfirmation(position);
                        break;
                    default://do nothing
                        break;
                }
                return true;
            }
        });
    }

    public void deletePlayConfirmation(final int position) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        DBA.deletePlay(playList.get(position));
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
                .setMessage("Are you sure you want to delete play: " + playList.get(position).getTitle())
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

}
