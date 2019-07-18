package com.example.huzz00mc.csocsomaster.DAO;

import android.support.annotation.NonNull;

/*
 * Created by HUZZ00MC on 2/28/2018.
 */

public class Player implements Comparable<Player> {

    private static int currentId = 0;
    int id;
    String name;
    int played;
    int won;
    int lost;
    int goalsFor;
    int goalsAgainst;
    float winLoseRatio;
    boolean active;

    public Player(String name) {
        this.name = name;
        id = currentId;
        currentId++;
        played = 0;
        won = 0;
        lost = 0;
        goalsFor = 0;
        goalsAgainst = 0;
        active = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPlayed() {
        return played;
    }

    public void setPlayed(int played) {
        this.played = played;
    }

    public void increaseCount() {
        played++;
    }

    public boolean isActive() {
        return active;
    }

    public void switchActive() {
        active = !active;
    }

    public int getWon() {
        return won;
    }

    public int getLost() {
        return lost;
    }

    public int getGoalsFor() {
        return goalsFor;
    }

    public int getGoalsAgainst() {
        return goalsAgainst;
    }

    public float getWinLoseRatio() {
        return winLoseRatio;
    }

    public void incWon() {
        won++;
        calculateWinLoseRatio();
    }

    public void incLost() {
        lost++;
        calculateWinLoseRatio();
    }

    private void calculateWinLoseRatio() {
        if (played != 0) winLoseRatio = (float) won / (won + lost);
    }

    public void incGoalsFor(int goals) {
        goalsFor += goals;
    }

    public void incGoalsAgainst(int goals) {
        goalsAgainst += goals;
    }

    public void addMatch(int score1, int score2) {
        incGoalsFor(score1);
        incGoalsAgainst(score2);
        if (score1 > score2)
            incWon();
        else incLost();
    }

    @Override
    public int compareTo(@NonNull Player other) {
        if (this.played > other.played) return 1;
        else if (this.played < other.played) return -1;
        else return 0;
    }
}
