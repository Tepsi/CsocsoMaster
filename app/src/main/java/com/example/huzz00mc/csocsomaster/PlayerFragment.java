package com.example.huzz00mc.csocsomaster;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.huzz00mc.csocsomaster.DAO.Match;
import com.example.huzz00mc.csocsomaster.DAO.MatchParticipants;
import com.example.huzz00mc.csocsomaster.DAO.Pair;
import com.example.huzz00mc.csocsomaster.DAO.Player;

import java.util.Collections;

public class PlayerFragment extends Fragment implements View.OnClickListener {

    private OnListFragmentInteractionListener mListener;
    private MyPlayerRecyclerViewAdapter myPlayerRecyclerViewAdapter;

    public PlayerFragment() {
    }

    public static PlayerFragment newInstance() {
        return new PlayerFragment();
    }

    public MyPlayerRecyclerViewAdapter getMyPlayerRecyclerViewAdapter() {
        return myPlayerRecyclerViewAdapter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player_list, container, false);

        FloatingActionButton btnAddPlayer = view.findViewById(R.id.btn_add_player);
        btnAddPlayer.setOnClickListener(this);

        RecyclerView recyclerView = view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        myPlayerRecyclerViewAdapter = new MyPlayerRecyclerViewAdapter(mListener);
        recyclerView.setAdapter(myPlayerRecyclerViewAdapter);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add_player:
                addPlayer();
                break;
        }
    }

    private void addPlayer() {
        final EditText playerName = new EditText(getContext());
        playerName.setHeight(100);
        playerName.setWidth(340);
        playerName.setGravity(Gravity.START);
        playerName.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(R.string.add_new_player).setView(playerName);
        alertDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                createPlayer(playerName.getText().toString());
            }
        });
        alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        alertDialog.show();
    }

    public void createPlayer(String nev) {
        Player newPlayer = new Player(nev);
        newPlayer.setPlayed(MainActivity.minPlayed());
        MainActivity.playerList.add(newPlayer);
        if (MainActivity.playerList.size() >= 4) {
            Collections.shuffle(MainActivity.playerList);
            generatePairs();
            generateMatches();
        }
        myPlayerRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void generatePairs() {
        for (int i = 0; i < MainActivity.playerList.size() - 1; i++) {
            for (int j = i + 1; j < MainActivity.playerList.size(); j++) {
                Pair pair = new Pair(MainActivity.playerList.get(i), MainActivity.playerList.get(j));
                boolean found = false;
                for (Pair other : MainActivity.pairs) {
                    if (pair.equals(other)) {
                        found = true;
                    }
                }
                if (!found)
                    MainActivity.pairs.add(pair);
            }
        }
    }

    private void generateMatches() {
        for (Pair pair1 : MainActivity.pairs) {
            for (Pair pair2 : MainActivity.pairs) {
                if (!pair1.disqualifies(pair2)) {
                    boolean found = false;
                    Match currentMatch = new Match(pair1, pair2);
                    for (Match match : MainActivity.matches) {
                        if (match.equals(currentMatch))
                            found = true;
                    }
                    if (!found) {
                        MainActivity.matches.add(currentMatch);
                        MatchParticipants matchParticipants = MainActivity.findMatchParticipants(currentMatch);
                        if (matchParticipants == null)
                            MainActivity.matchParticipantss.add(new MatchParticipants(currentMatch));
                    }
                }
            }
        }
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Player player);
    }


}
