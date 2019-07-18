package com.example.huzz00mc.csocsomaster;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.huzz00mc.csocsomaster.DAO.Player;
import com.example.huzz00mc.csocsomaster.PlayerFragment.OnListFragmentInteractionListener;

public class MyPlayerRecyclerViewAdapter extends RecyclerView.Adapter<MyPlayerRecyclerViewAdapter.ViewHolder> {

    private final OnListFragmentInteractionListener mListener;

    MyPlayerRecyclerViewAdapter(OnListFragmentInteractionListener listener) {
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_player, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mNameView.setText(MainActivity.playerList.get(position).getName());
        holder.mPlayedView.setText(Integer.toString(MainActivity.playerList.get(position).getPlayed()));
        holder.player = MainActivity.playerList.get(position);
        holder.mImageView.setImageResource(holder.player.isActive() ? android.R.drawable.presence_online : android.R.drawable.presence_busy);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onListFragmentInteraction(holder.player);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return MainActivity.playerList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView mNameView;
        final TextView mPlayedView;
        final ImageView mImageView;
        Player player;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = view.findViewById(R.id.name);
            mPlayedView = view.findViewById(R.id.played);
            mImageView = view.findViewById(R.id.active);
            player = null;
        }
    }
}
