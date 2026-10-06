package com.example.huzz00mc.csocsomaster;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.example.huzz00mc.csocsomaster.DAO.FinishedMatch;
import com.example.huzz00mc.csocsomaster.DAO.Match;
import com.example.huzz00mc.csocsomaster.DAO.MatchParticipants;
import com.example.huzz00mc.csocsomaster.DAO.Pair;
import com.example.huzz00mc.csocsomaster.DAO.Player;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PlayerFragment.OnListFragmentInteractionListener, MatchFragment.OnFragmentInteractionListener {

    public static ArrayList<Player> playerList = new ArrayList<>();
    public static ArrayList<Match> matches = new ArrayList<>();
    public static List<MatchParticipants> matchParticipantss = new ArrayList<>();
    public static List<Pair> pairs = new ArrayList<>();
    public static List<FinishedMatch> finishedMatches = new ArrayList<>();
    public MatchFragment matchFragment = null;
    private PlayerFragment playerFragment = null;
    private ResultPlayerFragment resultPlayerFragment = null;

    public static MatchParticipants findMatchParticipants(Match match) {
        for (MatchParticipants matchParticipants : MainActivity.matchParticipantss)
            if (matchParticipants.equals(match)) return matchParticipants;
        return null;
    }

    public static int minPlayed() {
        int maxPlayed = 999999;
        int minPlayed = maxPlayed;
        for (Player p : playerList) {
            if (p.isActive() && p.getPlayed() < minPlayed)
                minPlayed = p.getPlayed();
        }
        if (minPlayed == maxPlayed) return 0;
        else return minPlayed;
    }

    public static int minPlayed(Player excludedPlayer) {
        int maxPlayed = 999999;
        int minPlayed = maxPlayed;
        for (Player p : playerList) {
            if (!excludedPlayer.equals(p) && p.isActive() && p.getPlayed() < minPlayed)
                minPlayed = p.getPlayed();
        }
        if (minPlayed == maxPlayed) return 0;
        else return minPlayed;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        ViewPager mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Reset/next/toggle actions rebuild or reorder the whole player list, so there is
    // no stable per-item mapping to drive a targeted notify*() event.
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // switch(id) on R.id.* no longer compiles: with current AGP, the app
        // module's own resource ids are not guaranteed compile-time constants.
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_reset_data) {
            resetData();
            if (playerFragment != null)
                playerFragment.getMyPlayerRecyclerViewAdapter().notifyDataSetChanged();
            if (resultPlayerFragment != null)
                resultPlayerFragment.getMyPlayerRecyclerViewAdapter().sortPlayers();
            return true;
        } else if (id == R.id.action_save_list) {
            savePlayerList();
            return true;
        } else if (id == R.id.action_load_list) {
            loadPlayerList();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void savePlayerList() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        StringBuilder csvList = new StringBuilder();
        for (Player player : playerList) {
            csvList.append(player.getName());
            csvList.append(",");
        }
        editor.putString("players", csvList.toString());
        editor.apply();
    }

    private void resetData() {
        playerList = new ArrayList<>();
        matches = new ArrayList<>();
        matchParticipantss = new ArrayList<>();
        pairs = new ArrayList<>();
        finishedMatches = new ArrayList<>();
        if (matchFragment != null)
            matchFragment.resetData();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onNextPressed() {
        if (playerFragment != null) {
            playerFragment.getMyPlayerRecyclerViewAdapter().notifyDataSetChanged();
        }
        if (resultPlayerFragment != null) {
            resultPlayerFragment.sortPlayers();
            resultPlayerFragment.getMyPlayerRecyclerViewAdapter().notifyDataSetChanged();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onListFragmentInteraction(Player player) {
        if (!player.isActive() & player.getPlayed() < minPlayed(player)) {
            player.setPlayed(minPlayed(player));
        }
        player.switchActive();
        if (playerFragment != null) {
            playerFragment.getMyPlayerRecyclerViewAdapter().notifyDataSetChanged();
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return switch (position) {
                case 0 -> PlayerFragment.newInstance();
                case 1 -> MatchFragment.newInstance();
                case 2 -> ResultPlayerFragment.newInstance();
                case 3 -> FinishedMatchFragment.newInstance();
                default -> null;
            };
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment createdFragment = (Fragment) super.instantiateItem(container, position);
            switch (position) {
                case 0:
                    playerFragment = (PlayerFragment) createdFragment;
                    break;
                case 1:
                    matchFragment = (MatchFragment) createdFragment;
                    break;
                case 2:
                    resultPlayerFragment = (ResultPlayerFragment) createdFragment;
                    break;
                case 3:
                    FinishedMatchFragment finishedMatchFragment = (FinishedMatchFragment) createdFragment;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + position);
            }
            return createdFragment;
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getString(R.string.players);
                case 1:
                    return getResources().getString(R.string.matches);
                case 2:
                    return getResources().getString(R.string.table);
                case 3:
                    return getResources().getString(R.string.match_results);
                default:
                    return null;
            }
        }
    }

    private void loadPlayerList() {
        resetData();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String csvList = prefs.getString("players", "");
        if (!csvList.isEmpty()) {
            String[] names = csvList.split(",");
            for (String name : names) {
                playerFragment.createPlayer(name);
            }
        }
    }

    public static Player getPlayer(String name) {
        for (Player player : playerList) {
            if (player.getName().equals(name)) return player;
        }
        return null;
    }
}
