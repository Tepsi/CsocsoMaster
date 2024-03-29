package com.example.huzz00mc.csocsomaster;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huzz00mc.csocsomaster.DAO.FinishedMatch;
import com.example.huzz00mc.csocsomaster.DAO.Match;
import com.example.huzz00mc.csocsomaster.DAO.MatchParticipants;
import com.example.huzz00mc.csocsomaster.DAO.Pair;
import com.example.huzz00mc.csocsomaster.DAO.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MatchFragment extends Fragment implements View.OnClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private OnFragmentInteractionListener mListener;
    private TextView tvPlayer1;
    private TextView tvPlayer2;
    private TextView tvPlayer3;
    private TextView tvPlayer4;
    private TextView tvNumberPicker1;
    private TextView tvNumberPicker2;
    private ImageButton btnIncreaseNumberPicker1;
    private ImageButton btnIncreaseNumberPicker2;
    private ImageButton btnDecreaseNumberPicker1;
    private ImageButton btnDecreaseNumberPicker2;
    // Inflate the layout for this fragment
    private int maxScore;

    private ArrayList<Player> activePlayerList;
    private Match match;

    public MatchFragment() {
    }

    public static MatchFragment newInstance() {
        MatchFragment fragment = new MatchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private static void increaseCount(Match currentMatch) {
        currentMatch.getPair1().increaseCount();
        currentMatch.getPair2().increaseCount();
        increaseMatchCount(currentMatch);
        MainActivity.findMatchParticipants(currentMatch).increaseCount();
    }

    private static void increaseMatchCount(Match match) {
        for (Match current : MainActivity.matches) {
            if (current.equals((match))) {
                current.increaseCount();
                break;
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        maxScore = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("pref_win_score", "5"));
        View view = inflater.inflate(R.layout.fragment_matches, container, false);

        Button btnNext = view.findViewById(R.id.btn_next);
        btnNext.setOnClickListener(this);
        Button btnCancel = view.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(this);

        btnDecreaseNumberPicker1 = view.findViewById(R.id.btndecrease_numberpicker_1);
        btnDecreaseNumberPicker1.setOnClickListener(this);
        btnDecreaseNumberPicker2 = view.findViewById(R.id.btndecrease_numberpicker_2);
        btnDecreaseNumberPicker2.setOnClickListener(this);
        btnIncreaseNumberPicker1 = view.findViewById(R.id.btnincrease_numberpicker_1);
        btnIncreaseNumberPicker1.setOnClickListener(this);
        btnIncreaseNumberPicker2 = view.findViewById(R.id.btnincrease_numberpicker_2);
        btnIncreaseNumberPicker2.setOnClickListener(this);

        tvPlayer1 = view.findViewById(R.id.player_1);
        tvPlayer2 = view.findViewById(R.id.player_2);
        tvPlayer3 = view.findViewById(R.id.player_3);
        tvPlayer4 = view.findViewById(R.id.player_4);
        tvNumberPicker1 = view.findViewById(R.id.numberpicker_1);
        tvNumberPicker2 = view.findViewById(R.id.numberpicker_2);

        if (savedInstanceState != null) {
            tvNumberPicker1.setText(savedInstanceState.getString("NUMBER1"));
            tvNumberPicker2.setText(savedInstanceState.getString("NUMBER2"));
            if (match == null && !savedInstanceState.getString("PLAYER1").equals("")) {
                match = new Match(new Pair(MainActivity.getPlayer(savedInstanceState.getString("PLAYER1")),
                        MainActivity.getPlayer(savedInstanceState.getString("PLAYER2"))),
                        new Pair(MainActivity.getPlayer(savedInstanceState.getString("PLAYER3")),
                                MainActivity.getPlayer(savedInstanceState.getString("PLAYER4"))));
            }
        }
        if (match != null) displayMatch(match);
        if (tvNumberPicker1.getText() == "") {
            tvNumberPicker1.setText(Integer.toString(maxScore));
            tvNumberPicker2.setText(Integer.toString(maxScore - 1));
        }

        setScoreVisibility();
        return view;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals("pref_win_score")) {
            int newMaxScore = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("pref_win_score", "5"));
            if (Integer.parseInt(tvNumberPicker1.getText().toString()) == maxScore) {
                tvNumberPicker1.setText(Integer.toString(newMaxScore));
                if (Integer.parseInt(tvNumberPicker2.getText().toString()) >= newMaxScore)
                    tvNumberPicker2.setText(Integer.toString(newMaxScore - 1));
            }
            if (Integer.parseInt(tvNumberPicker2.getText().toString()) == maxScore) {
                tvNumberPicker2.setText(Integer.toString(newMaxScore));
                if (Integer.parseInt(tvNumberPicker1.getText().toString()) >= newMaxScore)
                    tvNumberPicker1.setText(Integer.toString(newMaxScore - 1));
            }
            maxScore = newMaxScore;
        }
        if (s.equals("pref_keep_score")) {
            setScoreVisibility();
        }
    }

    public void setScoreVisibility() {
        if (PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("pref_keep_score", true)) {
            tvNumberPicker1.setVisibility(View.VISIBLE);
            tvNumberPicker2.setVisibility(View.VISIBLE);
            btnDecreaseNumberPicker1.setVisibility(View.VISIBLE);
            btnDecreaseNumberPicker2.setVisibility(View.VISIBLE);
            btnIncreaseNumberPicker1.setVisibility(View.VISIBLE);
            btnIncreaseNumberPicker2.setVisibility(View.VISIBLE);
        } else {
            tvNumberPicker1.setVisibility(View.INVISIBLE);
            tvNumberPicker2.setVisibility(View.INVISIBLE);
            btnDecreaseNumberPicker1.setVisibility(View.INVISIBLE);
            btnDecreaseNumberPicker2.setVisibility(View.INVISIBLE);
            btnIncreaseNumberPicker1.setVisibility(View.INVISIBLE);
            btnIncreaseNumberPicker2.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        PreferenceManager.getDefaultSharedPreferences(getContext()).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        PreferenceManager.getDefaultSharedPreferences(getContext()).unregisterOnSharedPreferenceChangeListener(this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View view) {
        int number;
        int maxScore = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("pref_win_score", "0"));
        switch (view.getId()) {
            case R.id.btn_next:
                if (match != null) {
                    increaseCount(match);
                    match.saveResult(Integer.parseInt(tvNumberPicker1.getText().toString()), Integer.parseInt(tvNumberPicker2.getText().toString()));
                    MainActivity.finishedMatches.add(new FinishedMatch(match.getPair1(), match.getPair2(),
                            Integer.parseInt(tvNumberPicker1.getText().toString()),
                            Integer.parseInt(tvNumberPicker2.getText().toString())));
                }
            case R.id.btn_cancel:
                activePlayerList = new ArrayList<>();
                for (Player player : MainActivity.playerList) {
                    if (player.isActive()) activePlayerList.add(player);
                }
                if (activePlayerList.size() < 4) {
                    Toast.makeText(getContext(), R.string.too_few_active_players, Toast.LENGTH_SHORT).show();
                } else {
                    match = generateNextMatch();
                    displayMatch(match);
                    mListener.onNextPressed();
                }
                break;
            case R.id.btndecrease_numberpicker_1:
                number = Integer.parseInt(tvNumberPicker1.getText().toString());
                if (number == maxScore) tvNumberPicker2.setText(Integer.toString(maxScore));
                if (number > 0) {
                    number--;
                    tvNumberPicker1.setText(Integer.toString(number));
                }
                break;
            case R.id.btnincrease_numberpicker_1:
                number = Integer.parseInt(tvNumberPicker1.getText().toString());
                if (number == maxScore - 1 && Integer.parseInt(tvNumberPicker2.getText().toString()) == maxScore) {
                    tvNumberPicker2.setText(Integer.toString(maxScore - 1));
                }
                if (number < maxScore) {
                    number++;
                    tvNumberPicker1.setText(Integer.toString(number));
                }
                break;
            case R.id.btndecrease_numberpicker_2:
                number = Integer.parseInt(tvNumberPicker2.getText().toString());
                if (number == maxScore) {
                    tvNumberPicker1.setText(Integer.toString(maxScore));
                }
                if (number > 0) {
                    number--;
                    tvNumberPicker2.setText(Integer.toString(number));
                }
                break;
            case R.id.btnincrease_numberpicker_2:
                number = Integer.parseInt(tvNumberPicker2.getText().toString());
                if (number == maxScore - 1 && Integer.parseInt(tvNumberPicker1.getText().toString()) == maxScore) {
                    tvNumberPicker1.setText(Integer.toString(maxScore - 1));
                }
                if (number < maxScore) {
                    number++;
                    tvNumberPicker2.setText(Integer.toString(number));
                }
                break;
            default:
                break;
        }
    }

    private Match generateNextMatch() {

        List<Player> toPlay = new ArrayList<>();
        List<Player> mustPlay = new ArrayList<>();
        int lowestToPlay = 0;
        Match currentMatch = null;

        Collections.sort(activePlayerList);
        int i = -1;
        for (Player player : activePlayerList) {
            i++;
            if (i < 4) {
                toPlay.add(player);
                lowestToPlay = player.getPlayed();
            } else if (player.getPlayed() == lowestToPlay)
                toPlay.add(player);
            else
                break;
        }

        if (toPlay.size() == 4) {
            mustPlay = toPlay;
        } else {
            for (Player player : toPlay) {
                if (player.getPlayed() < lowestToPlay) {
                    mustPlay.add(player);
                } else
                    break;
            }
        }

        List<Match> possibleMatches = new ArrayList<>();
        for (Match match : MainActivity.matches) {
            boolean ok = true;
            for (Player player : mustPlay) {
                if (!match.hasPlayer(player))
                    ok = false;
            }
            for (Player player : match.getPlayers()) {
                if (!toPlay.contains(player))
                    ok = false;
            }
            if (ok)
                possibleMatches.add(match);
        }

        Collections.sort(possibleMatches);
        int minCount = possibleMatches.get(0).getPairCount();
        int minOtherCount = 99999;
        int minMatchCount = 99999;
        for (Match match : possibleMatches) {
            if (match.getPairCount() > minCount)
                break;
            minCount = match.getPairCount();
            MatchParticipants matchParticipants = MainActivity.findMatchParticipants(match);
            if (matchParticipants.getPlayed() > minOtherCount) break;
            if (matchParticipants.getPlayed() == minOtherCount) {
                if (match.getCount() < minMatchCount) {
                    currentMatch = match;
                    minMatchCount = match.getCount();
                }
            } else {
                minOtherCount = matchParticipants.getPlayed();
                currentMatch = match;
            }
        }
        return currentMatch;
    }

    public void resetData() {
        tvPlayer1.setText("");
        tvPlayer2.setText("");
        tvPlayer3.setText("");
        tvPlayer4.setText("");
        match = null;
    }

    private void displayMatch(Match match) {
        tvPlayer1.setText(match.getPair1().getPlayer1().getName());
        tvPlayer2.setText(match.getPair1().getPlayer2().getName());
        tvPlayer3.setText(match.getPair2().getPlayer1().getName());
        tvPlayer4.setText(match.getPair2().getPlayer2().getName());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putString("PLAYER1", tvPlayer1.getText().toString());
        outState.putString("PLAYER2", tvPlayer2.getText().toString());
        outState.putString("PLAYER3", tvPlayer3.getText().toString());
        outState.putString("PLAYER4", tvPlayer4.getText().toString());
        outState.putString("NUMBER1", tvNumberPicker1.getText().toString());
        outState.putString("NUMBER2", tvNumberPicker2.getText().toString());

        super.onSaveInstanceState(outState);
    }

    public interface OnFragmentInteractionListener {
        void onNextPressed();
    }
}
