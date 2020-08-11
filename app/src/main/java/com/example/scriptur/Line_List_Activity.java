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
import android.widget.Toast;

import com.example.scriptur.Database.DBAdaptor;
import com.example.scriptur.Database.Line;
import com.example.scriptur.RecyclerViewAdaptors.RVAdaptorLine;

import java.util.ArrayList;

public class Line_List_Activity extends AppCompatActivity implements RVAdaptorLine.OnRowListener {

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
        RVALine = new RVAdaptorLine(this, lineList, this);
        rvLine.setAdapter(RVALine);
    }

    public void addLineBtn(View v) {
        Intent in = new Intent(this, NewLineActivity.class);
        in.putExtra("SCENE_ID", sceneID);
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
                        Intent in = new Intent(Line_List_Activity.this, UpdateLineActivity.class);
                        in.putExtra("LINE_ID", lineList.get(position).getUID());
                        startActivity(in);
                        break;
                    case "Delete":
                        deleteLineConfirmation(position);
                        break;
                    default://do nothing
                        break;
                }
                return true;
            }
        });
    }

    public void deleteLineConfirmation(final int position) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        DBA.deleteLine(lineList.get(position));
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
                .setMessage("Are you sure you want to delete this Line: ")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }
}
