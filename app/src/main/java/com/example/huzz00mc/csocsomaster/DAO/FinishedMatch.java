package com.example.huzz00mc.csocsomaster.DAO;

public class FinishedMatch {
    private Pair pair1;
    private Pair pair2;
    private int score1;
    private int score2;

    public FinishedMatch(Pair pair1, Pair pair2, int score1, int score2) {
        this.pair1 = pair1;
        this.pair2 = pair2;
        this.score1 = score1;
        this.score2 = score2;
    }

    public Pair getPair1() {
        return pair1;
    }

    public Pair getPair2() {
        return pair2;
    }

    public int getScore1() {
        return score1;
    }

    public int getScore2() {
        return score2;
    }

}
