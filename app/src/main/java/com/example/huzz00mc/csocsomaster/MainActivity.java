package com.example.huzz00mc.csocsomaster;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.example.huzz00mc.csocsomaster.DAO.Match;
import com.example.huzz00mc.csocsomaster.DAO.MatchParticipants;
import com.example.huzz00mc.csocsomaster.DAO.Pair;
import com.example.huzz00mc.csocsomaster.DAO.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements PlayerFragment.OnListFragmentInteractionListener, MatchFragment.OnFragmentInteractionListener {

    public static ArrayList<Player> playerList = new ArrayList<>();
    public static ArrayList<Match> matches = new ArrayList<>();
    public static List<MatchParticipants> matchParticipantss = new ArrayList<>();
    public static List<Pair> pairs = new ArrayList<>();
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

        if (playerList.isEmpty()) {
            HashSet<String> playerListSet = new HashSet<String>();
            PreferenceManager.getDefaultSharedPreferences(this).getStringSet("players",null);
            for (String name:playerListSet) {
                playerFragment.createPlayer(name);
            }
        }

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.action_reset_data:
                resetData();
                if (playerFragment != null)
                    playerFragment.getMyPlayerRecyclerViewAdapter().notifyDataSetChanged();
                if (resultPlayerFragment != null)
                    resultPlayerFragment.getMyPlayerRecyclerViewAdapter().sortPlayers();
                return true;
            case R.id.action_save_list:
                savePlayerList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void savePlayerList() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        HashSet<String> playerListSet = new HashSet<String>();
        for (Player player:playerList) {
            playerListSet.add(player.getName());
        }
        try {
            editor.putStringSet("players",playerListSet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void resetData() {
        playerList = new ArrayList<>();
        matches = new ArrayList<>();
        matchParticipantss = new ArrayList<>();
        pairs = new ArrayList<>();
        if (matchFragment != null)
            matchFragment.resetData();
    }

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

    @Override
    public void onListFragmentInteraction(Player player) {
        if (!player.isActive()) {
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
            switch (position) {
                case 0:
                    return PlayerFragment.newInstance();
                case 1:
                    return MatchFragment.newInstance();
                case 2:
                    return ResultPlayerFragment.newInstance();
                default:
                    return null;
            }
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
            }
            return createdFragment;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getString(R.string.players);
                case 1:
                    return getResources().getString(R.string.matches);
                case 2:
                    return getString(R.string.table);
                default:
                    return null;

            }
        }
    }
}
