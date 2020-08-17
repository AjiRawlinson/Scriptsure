package com.example.scriptur;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.scriptur.Database.Character;
import com.example.scriptur.Database.DBAdaptor;
import com.example.scriptur.Database.Line;
import com.example.scriptur.RecyclerViewAdaptors.RVAdaptorCharacter;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CharacterFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CharacterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CharacterFragment extends Fragment implements RVAdaptorCharacter.OnRowListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    RecyclerView rvCharacter;
    TextView noData;
    ArrayList<Character> characterList;
    DBAdaptor DBA;
    RVAdaptorCharacter RVACharacter;
    int playid;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public CharacterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CharacterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CharacterFragment newInstance(String param1, String param2) {
        CharacterFragment fragment = new CharacterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_character, container, false);

        DBA = new DBAdaptor(view.getContext());
        playid = getArguments().getInt("PLAY_ID");
        characterList = DBA.getAllCharactersInPlay(playid);
        noData = (TextView) view.findViewById(R.id.tvCharacterNoData);
        rvCharacter = (RecyclerView) view.findViewById(R.id.RVCharacter);
        if(!characterList.isEmpty()) {
            LinearLayoutManager llm = new LinearLayoutManager(view.getContext());
            rvCharacter.setLayoutManager(llm);
            ArrayList<Line> lineList = DBA.getAllLines();
            RVACharacter = new RVAdaptorCharacter(view.getContext(), characterList, lineList, this);
            rvCharacter.setAdapter(RVACharacter);
        } else { noData.setVisibility(View.VISIBLE); }

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String id) {
        if (mListener != null) {
            mListener.onCharacterFragmentInteraction(id);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onCharacterFragmentInteraction(String id);
    }

    @Override
    public void onRowLongClick(final int position, View v) {
        PopupMenu popup = new PopupMenu(getActivity(), v);
        popup.getMenu().add("Edit");
        popup.getMenu().add("Delete");
        popup.show();

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.toString()) {
                    case "Edit":
                        Intent in = new Intent(getActivity(), UpdateCharacterActivity.class);
                        in.putExtra("CHARACTER_ID", characterList.get(position).getUID());
                        startActivity(in);
                        break;
                    case "Delete":
                        int numberOfLines = DBA.getNumberOfLineByCharacter(characterList.get(position));
                        if(numberOfLines > 0) {
                            Toast.makeText(getActivity(), "Character has " + numberOfLines + " Lines in this Play, please delete these Lines before deleteing Character ", Toast.LENGTH_LONG).show();
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
                        Intent in = new Intent(getActivity(), SceneCharacterTabbedActivity.class);
                        in.putExtra("TAB_NUM", 0);
                        in.putExtra("PLAY_ID", playid);
                        startActivity(in);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
//                        do nothing
                        break;
                    default://do nothing
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete")
                .setMessage("Are you sure you want to delete Character: " + characterList.get(position).getName())
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }


}
