package com.example.scriptur;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.scriptur.Database.DBAdaptor;
import com.example.scriptur.Database.Play;
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

        rvPlay = (RecyclerView) findViewById(R.id.RVPlay);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        rvPlay.setLayoutManager(llm);
        RVAPlay = new RVAdaptorPlay(this, playList, this);
        rvPlay.setAdapter(RVAPlay);
    }

    @Override
    public void onRowClick(int position) {
        Intent in = new Intent(this, Scene_List_Activity.class);
        in.putExtra("PLAY_ID", playList.get(position).getUID());
        startActivity(in);
    }
}
