package com.example.scriptur;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.scriptur.Database.Character;
import com.example.scriptur.Database.DBAdaptor;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class SceneCharacterTabbedActivity extends AppCompatActivity implements CharacterFragment.OnFragmentInteractionListener, SceneFragment.OnFragmentInteractionListener {

    int playId;
    TextView title;
    DBAdaptor DBA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene_character_tabbed);


        Intent in = getIntent();
        playId = in.getIntExtra("PLAY_ID", 1);
        int tabNum = in.getIntExtra("TAB_NUM", 1);
        DBA = new DBAdaptor(this);
        title = (TextView) findViewById(R.id.title);
        title.setText(DBA.getPlayByID(playId).getTitle());

        TabLayout tabs = findViewById(R.id.tabs);
        ViewPager viewPager = findViewById(R.id.view_pager);
        FragmentAdaptor adaptor = new FragmentAdaptor(getSupportFragmentManager());
        viewPager.setAdapter(adaptor);
        tabs.setupWithViewPager(viewPager);
        tabs.getTabAt(0).setText("Characters");
        tabs.getTabAt(1).setText("Scenes");
        if(tabNum == 1) { viewPager.setCurrentItem(1); }
        else { viewPager.setCurrentItem(0); }

    }

    public void newSceneBtn(View v) {
        Intent in = new Intent(this, NewSceneActivity.class);
        in.putExtra("PLAY_ID", playId);
        startActivity(in);
    }

    public void newCharacterBtn(View v) {
        Intent in = new Intent(this, NewCharacterActivity.class);
        in.putExtra("PLAY_ID", playId);
        startActivity(in);
    }

    @Override
    public void onCharacterFragmentInteraction(String id) {

    }

    @Override
    public void onSceneFragmentInteraction(String string) {

    }

    /**********************************************************************************************************************
     *                             F A G M E N T   P A G E R   A D A P T O R
     **********************************************************************************************************************/


    public class FragmentAdaptor extends FragmentPagerAdapter {

        public FragmentAdaptor(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch(position) {
                case 0:
                    CharacterFragment character = new CharacterFragment();
                    Bundle characterBundle = new Bundle();
                    characterBundle.putInt("PLAY_ID", playId);
                    character.setArguments(characterBundle);
                return character;
                case 1:
                    SceneFragment scene = new SceneFragment();
                    Bundle sceneBundle = new Bundle();
                    sceneBundle.putInt("PLAY_ID", playId);
                    scene.setArguments(sceneBundle);
                    return scene;
                default:
                    CharacterFragment character3 = new CharacterFragment();
                    Bundle bundle3 = new Bundle();
                    bundle3.putInt("PLAY_ID", playId);
                    character3.setArguments(bundle3);
                    return character3;
            }
        }

        @Override
        public int getCount() {
            return 2; //hardcoding
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, Play_List_Activity.class));
    }
}