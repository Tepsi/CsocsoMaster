package com.example.huzz00mc.csocsomaster.DAO;

/**
 * Created by HUZZ00MC on 3/6/2018.
 */

import java.util.ArrayList;
import java.util.List;

public class Pair implements Comparable<Pair> {

    private Player player1;
    private Player player2;
    private int played;

    public Pair(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        sortPlayers();
        played = 0;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    int getPlayed() {
        return played;
    }

    public boolean equals(Pair other) {
        if (this.player1.getId() == other.player1.getId() && this.player2.getId() == other.player2.getId())
            return true;
        else return false;

    }

    @Override
    public int compareTo(Pair other) {
        if (this.played > other.played)
            return 1;
        else if (this.played < other.played)
            return -1;
        else
            return 0;
    }

    private void sortPlayers() {
        if (player2 == null)
            return;
        if (player1 == null) {
            player1 = player2;
            player2 = null;
            return;
        }
        if (player1.getId() > player2.getId()) {
            Player temp = player2;
            this.player2 = player1;
            this.player1 = temp;
        }
    }

    public boolean disqualifies(Pair other) {
        if (this.getPlayer1().getId() == other.getPlayer1().getId()
                || this.getPlayer2().getId() == other.getPlayer1().getId()
                || this.getPlayer1().getId() == other.getPlayer2().getId()
                || this.getPlayer2().getId() == other.getPlayer2().getId())
            return true;
        return false;

    }

    boolean hasPlayer(Player player) {
        if (player1 != null && player1 == player)
            return true;
        if (player2 != null && player2 == player)
            return true;
        return false;
    }

    List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        return players;
    }

    public void increaseCount() {
        played++;
        player1.increaseCount();
        player2.increaseCount();
    }

    public void saveResult(int score1, int score2) {
        player1.addMatch(score1, score2);
        player2.addMatch(score1, score2);
    }

}
