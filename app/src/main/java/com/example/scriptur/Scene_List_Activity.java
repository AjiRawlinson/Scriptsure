package com.example.scriptur;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
    TextView playTitle;
    int playid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene_list);

//        playTitle = (TextView) findViewById(R.id.tvPlayTitleSceneList);
        DBA = new DBAdaptor(this);
        Intent in = getIntent();
        playid = in.getIntExtra("PLAY_ID", 1);
        setTitle(DBA.getPlayByID(playid).getTitle());
//        playTitle.setText(DBA.getPlayByID(playid).getTitle());

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
}
