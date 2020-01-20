package com.example.huzz00mc.csocsomaster.DAO;

/*
  Created by HUZZ00MC on 3/6/2018.
 */

import java.util.ArrayList;
import java.util.List;

public class Match implements Comparable<Match> {

    private Pair pair1;
    private Pair pair2;
    private int count;

    public Match(Pair pair1, Pair pair2) {
        count = 0;
        if (pair1.getPlayer1().getId() > pair2.getPlayer1().getId()) {
            this.pair2 = pair1;
            this.pair1 = pair2;

        } else {
            this.pair1 = pair1;
            this.pair2 = pair2;
        }
    }

    public Pair getPair1() {
        return pair1;
    }

    public Pair getPair2() {
        return pair2;
    }

    public boolean equals(Match other) {
        if ((this.pair1.equals(other.pair1) && this.pair2.equals(other.pair2)) ||
            (this.pair1.equals(other.pair2) && this.pair2.equals(other.pair1)))
            return true;
        return false;
    }

    public boolean hasPlayer(Player player) {
        if (pair1 != null && pair1.hasPlayer(player))
            return true;
        if (pair2 != null && pair2.hasPlayer(player))
            return true;
        return false;
    }

    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();
        players.addAll(pair1.getPlayers());
        players.addAll(pair2.getPlayers());
        return players;
    }

    public void saveResult(int score1, int score2) {
        pair1.saveResult(score1, score2);
        pair2.saveResult(score2, score1);
    }

    public int getPairCount() {
        return pair1.getPlayed() + pair2.getPlayed();
    }

    @Override
    public int compareTo(Match other) {
        if (this.getPairCount() > other.getPairCount())
            return 1;
        else if (this.getPairCount() < other.getPairCount())
            return -1;
        else
            return 0;
    }

    public int getCount() {
        return count;
    }

    public void increaseCount() {
        count += 1;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
