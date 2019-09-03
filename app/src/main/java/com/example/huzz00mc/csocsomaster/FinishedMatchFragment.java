package com.example.huzz00mc.csocsomaster;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FinishedMatchFragment extends Fragment {

    private ResultRecyclerViewAdapter resultRecyclerViewAdapter;

    public static FinishedMatchFragment newInstance() {
        FinishedMatchFragment fragment = new FinishedMatchFragment();
        return fragment;
    }

    public ResultRecyclerViewAdapter getMyPlayerRecyclerViewAdapter() {
        return resultRecyclerViewAdapter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_finished_match_list, container, false);


        RecyclerView recyclerView = view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        resultRecyclerViewAdapter = new ResultRecyclerViewAdapter();
        recyclerView.setAdapter(resultRecyclerViewAdapter);

        return view;
    }
}
