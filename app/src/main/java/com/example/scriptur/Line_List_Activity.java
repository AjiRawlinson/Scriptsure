package com.example.scriptur;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.scriptur.Database.DBAdaptor;
import com.example.scriptur.Database.Line;
import com.example.scriptur.RecyclerViewAdaptors.RVAdaptorLine;

import java.util.ArrayList;

public class Line_List_Activity extends AppCompatActivity {

    RecyclerView rvLine;
    ArrayList<Line> lineList;
    DBAdaptor DBA;
    RVAdaptorLine RVALine;
    TextView sceneName;
    int sceneID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_list);

        DBA = new DBAdaptor(this);
        Intent in = getIntent();
        sceneID = in.getIntExtra("SCENE_ID", 1);
        setTitle(DBA.getSceneByID(sceneID).getName());

        rvLine = (RecyclerView) findViewById(R.id.RVLine);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        rvLine.setLayoutManager(llm);
        lineList = DBA.getAllLinesInScene(sceneID); //change to scenes
        RVALine = new RVAdaptorLine(this, lineList);
        rvLine.setAdapter(RVALine);
    }

    public void addLineBtn(View v) {
        Intent in = new Intent(this, NewLineActivity.class);
        in.putExtra("SCENE_ID", sceneID);
        startActivity(in);
    }
}
