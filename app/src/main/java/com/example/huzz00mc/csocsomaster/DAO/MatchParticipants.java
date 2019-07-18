package com.example.huzz00mc.csocsomaster.DAO;

/*
  Created by HUZZ00MC on 3/6/2018.
 */

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class MatchParticipants implements Comparable<MatchParticipants>{
    private List<Player> players;
    private int played;

    public MatchParticipants(Match match) {
        players = new ArrayList<>();
        played = 0;
        players.addAll(match.getPlayers());
    }

    public int getPlayed() {
        return played;
    }

    @Override
    public int compareTo(@NonNull MatchParticipants other) {
        if (this.played>other.getPlayed()) return 1;
        if (this.played<other.getPlayed()) return -1;
        else return 0;
    }

    public void increaseCount() {
        played++;
    }

    public boolean equals(Match match) {
        for (Player player:match.getPlayers()) {
            if (!players.contains(player)) return false;
        }
        return true;
    }


}
