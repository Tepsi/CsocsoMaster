package com.example.huzz00mc.csocsomaster;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.huzz00mc.csocsomaster.DAO.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static java.lang.Math.round;

public class MyPlayerRecyclerViewAdapter2 extends RecyclerView.Adapter<MyPlayerRecyclerViewAdapter2.ViewHolder> {

    static final Comparator<Player> BY_RESULTS =
            new Comparator<Player>() {
                @Override
                public int compare(Player player, Player t1) {
                    if (player.getWinLoseRatio() == t1.getWinLoseRatio())
                        if (t1.getGoalsFor() + player.getGoalsAgainst() - player.getGoalsFor() - t1.getGoalsAgainst() == 0)
                            return t1.getGoalsFor() - player.getGoalsFor();
                        else
                          return round((t1.getGoalsFor() + player.getGoalsAgainst() - player.getGoalsFor() - t1.getGoalsAgainst()) * 100);
                    else
                        return round((t1.getWinLoseRatio() - player.getWinLoseRatio()) * 10000);
                }
            };

    private List<Player> mValues;

    public void sortPlayers() {
        mValues = new ArrayList<>();
        for (Player player : MainActivity.playerList) {
            if (player.getPlayed() > 0) mValues.add(player);
        }
        Collections.sort(mValues, BY_RESULTS);
        notifyDataSetChanged();
    }


    public MyPlayerRecyclerViewAdapter2() {
        sortPlayers();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_player_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.player = mValues.get(position);
        holder.tvName.setText(holder.player.getName());
        holder.tvPlayed.setText(Integer.toString(holder.player.getWon() + holder.player.getLost()));
        holder.tvWon.setText(Integer.toString(holder.player.getWon()));
        holder.tvLost.setText(Integer.toString(holder.player.getLost()));
        holder.tvWinLoseRatio.setText(Integer.toString(round(holder.player.getWinLoseRatio() * 100)) + "%");
        holder.tvGoalsFor.setText(Integer.toString(holder.player.getGoalsFor()));
        holder.tvGoalsAgainst.setText(Integer.toString(holder.player.getGoalsAgainst()));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView tvName;
        public final TextView tvPlayed;
        public final TextView tvWon;
        public final TextView tvLost;
        public final TextView tvWinLoseRatio;
        public final TextView tvGoalsFor;
        public final TextView tvGoalsAgainst;

        public Player player;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvName = view.findViewById(R.id.name);
            tvPlayed = view.findViewById(R.id.played);
            tvWon = view.findViewById(R.id.won);
            tvLost = view.findViewById(R.id.lost);
            tvWinLoseRatio = view.findViewById(R.id.win_lose_ratio);
            tvGoalsFor = view.findViewById(R.id.goals_for);
            tvGoalsAgainst = view.findViewById(R.id.goals_against);
            player = null;
        }
    }
}
