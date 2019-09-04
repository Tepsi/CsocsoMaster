package com.example.huzz00mc.csocsomaster;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ResultPlayerFragment extends Fragment {

    private MyPlayerRecyclerViewAdapter2 myPlayerRecyclerViewAdapter;

    public static ResultPlayerFragment newInstance() {
        ResultPlayerFragment fragment = new ResultPlayerFragment();
        return fragment;
    }

    public MyPlayerRecyclerViewAdapter2 getMyPlayerRecyclerViewAdapter() {
        return myPlayerRecyclerViewAdapter;
    }

    public void sortPlayers() {
        myPlayerRecyclerViewAdapter.sortPlayers();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player_result_list, container, false);


        RecyclerView recyclerView = view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        myPlayerRecyclerViewAdapter = new MyPlayerRecyclerViewAdapter2();
        recyclerView.setAdapter(myPlayerRecyclerViewAdapter);

        return view;
    }
}
