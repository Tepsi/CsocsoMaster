package com.example.huzz00mc.csocsomaster;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.huzz00mc.csocsomaster.DAO.FinishedMatch;

public class ResultRecyclerViewAdapter extends RecyclerView.Adapter<ResultRecyclerViewAdapter.ViewHolder> {

    @Override
    public void onBindViewHolder(final ResultRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.match = MainActivity.finishedMatches.get(position);
        holder.tvFMName1.setText(holder.match.getPair1().getPlayer1().getName());
        holder.tvFMName2.setText(holder.match.getPair1().getPlayer2().getName());
        holder.tvFMName3.setText(holder.match.getPair2().getPlayer1().getName());
        holder.tvFMName4.setText(holder.match.getPair2().getPlayer2().getName());
        holder.tvScore1.setText(Integer.toString(holder.match.getScore1()));
        holder.tvScore2.setText(Integer.toString(holder.match.getScore2()));
        if (holder.match.getScore1() > holder.match.getScore2()) {
            setColor(holder,1,Color.GREEN);
            setColor(holder,2,Color.RED);
        } else {
            setColor(holder,1,Color.RED);
            setColor(holder,2,Color.GREEN);
        }
    }

    @Override
    public int getItemCount() {
        return MainActivity.finishedMatches.size();
    }

    @Override
    public ResultRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_finished_match, parent, false);
        return new ResultRecyclerViewAdapter.ViewHolder(view);
    }

    private void setColor(ResultRecyclerViewAdapter.ViewHolder holder, int position, int color) {
        switch (position) {
            case 1:
                holder.tvFMName1.setBackgroundColor(color);
                holder.tvFMName2.setBackgroundColor(color);
                holder.tvScore1.setBackgroundColor(color);
                holder.tvHyphen1.setBackgroundColor(color);
                break;
            default:
                holder.tvFMName3.setBackgroundColor(color);
                holder.tvFMName4.setBackgroundColor(color);
                holder.tvScore2.setBackgroundColor(color);
                holder.tvHyphen2.setBackgroundColor(color);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvFMName1;
        private final TextView tvFMName2;
        private final TextView tvFMName3;
        private final TextView tvFMName4;
        private final TextView tvScore1;
        private final TextView tvScore2;
        private final TextView tvHyphen1;
        private final TextView tvHyphen2;

        private FinishedMatch match;

        ViewHolder(View view) {
            super(view);
            tvFMName1 = view.findViewById(R.id.player_fm_1);
            tvFMName2 = view.findViewById(R.id.player_fm_2);
            tvFMName3 = view.findViewById(R.id.player_fm_3);
            tvFMName4 = view.findViewById(R.id.player_fm_4);
            tvScore1 = view.findViewById(R.id.score_fm_1);
            tvScore2 = view.findViewById(R.id.score_fm_2);
            tvHyphen1 = view.findViewById(R.id.hyphen_fm_1);
            tvHyphen2 = view.findViewById(R.id.hyphen_fm_2);
        }
    }

}
