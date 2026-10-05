package com.example.huzz00mc.csocsomaster;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FinishedMatchFragment extends Fragment {

    private ResultRecyclerViewAdapter resultRecyclerViewAdapter;

    public static FinishedMatchFragment newInstance() {
        return new FinishedMatchFragment();
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
