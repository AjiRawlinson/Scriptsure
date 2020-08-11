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
import android.widget.TextView;

import com.example.scriptur.Database.DBAdaptor;
import com.example.scriptur.Database.Scene;
import com.example.scriptur.RecyclerViewAdaptors.RVAdaptorScene;

import java.util.ArrayList;

public class Scene_List_Activity extends AppCompatActivity implements RVAdaptorScene.OnRowListener  {

    RecyclerView rvScene;
    ArrayList<Scene> sceneList;
    DBAdaptor DBA;
    RVAdaptorScene RVAScene;
    int playid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene_list);

        DBA = new DBAdaptor(this);
        Intent in = getIntent();
        playid = in.getIntExtra("PLAY_ID", 1);
        setTitle(DBA.getPlayByID(playid).getTitle());

        rvScene = (RecyclerView) findViewById(R.id.RVScene);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        rvScene.setLayoutManager(llm);
        sceneList = DBA.getAllScenesInPlay(playid); //change to scenes
        RVAScene = new RVAdaptorScene(this, sceneList, this);
        rvScene.setAdapter(RVAScene);
    }

    public void addSceneBtn(View v) {
        Intent in = new Intent(this, NewSceneActivity.class);
        in.putExtra("PLAY_ID", playid);
        startActivity(in);
    }

    public void viewCharactersBtn(View v) {
        Intent in = new Intent(this, Character_List_Activity.class);
        in.putExtra("PLAY_ID", playid);
        startActivity(in);
    }


    @Override
    public void onRowClick(int position) {
        Intent in = new Intent(this, Line_List_Activity.class);
        in.putExtra("SCENE_ID", sceneList.get(position).getUID());
        startActivity(in);
    }

    @Override
    public void onRowLongClick(final int position, View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.getMenu().add("Edit");
        popup.getMenu().add("Delete");
//        popup.getMenu().add("Move Up"); //TODO
//        popup.getMenu().add("Move Down"); //TODO
        popup.show();

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.toString()) {
                    case "Edit":
                        Intent in = new Intent(Scene_List_Activity.this, UpdateSceneActivity.class);
                        in.putExtra("SCENE_ID", sceneList.get(position).getUID());
                        startActivity(in);
                        break;
                    case "Delete":
                        deleteSceneConfirmation(position);
                        break;
                    default://do nothing
                        break;
                }
                return true;
            }
        });
    }

    public void deleteSceneConfirmation(final int position) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        DBA.deleteScene(sceneList.get(position));
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
                .setMessage("Are you sure you want to delete Scene: " + sceneList.get(position).getName())
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }
}
