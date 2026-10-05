# CsocsoMaster

Android app for running a casual foosball ("csocsó") tournament evening. It
tracks players, pairs them into 2v2 matches, suggests who should play next so
that everyone gets a fair, even number of games, records scores, and keeps a
live result table.

## Features

- Add/remove players on the fly, mark players active/inactive (benched)
- Automatic 2v2 match scheduling that balances playing time across players
  and pairs, and avoids repeating the same matchup more than necessary
- Score entry per match (configurable score needed to win)
- Live standings table (wins/losses, win ratio, goal difference)
- Match history / results log
- Save and reload a player list (by name) via app preferences

## Tech stack

- Java, native Android (no Kotlin, no backend/network)
- Android Support Library (pre-AndroidX), `minSdk 15`, `targetSdk`/`compileSdk 26`
- Gradle build (`gradlew`/`gradlew.bat`)
- UI: `ViewPager` + `TabLayout` with 4 tabs, each backed by a `Fragment` and a
  `RecyclerView`
- All application state is held in static in-memory lists on `MainActivity`
  (no database, no persistence beyond the player-name CSV saved in
  `SharedPreferences`)

## Project layout

```
app/src/main/java/com/example/huzz00mc/csocsomaster/
├── DAO/                        # Plain data/domain model classes
│   ├── Player.java             # A player and their running stats
│   ├── Pair.java                # An unordered pair of two players (one team)
│   ├── Match.java               # Two pairs facing each other
│   ├── MatchParticipants.java   # Tracks how often a given 4-player group has played
│   └── FinishedMatch.java       # A completed match with its final score
├── MainActivity.java            # App shell, tabs, global state, static helpers
├── PlayerFragment.java          # "Players" tab: add players, generate pairs/matches
├── MatchFragment.java           # "Matches" tab: scheduling algorithm + score entry
├── ResultPlayerFragment.java    # "Table" tab: standings
├── FinishedMatchFragment.java   # "Results" tab: match history
├── MyPlayerRecyclerViewAdapter(2).java / ResultRecyclerViewAdapter.java
├── SettingsActivity.java / SettingsFragment.java  # Preferences screen
```

Business logic and the match-scheduling algorithm are documented in detail in
[`documentation.md`](documentation.md). Guidance for AI coding assistants
working in this repo is in [`CLAUDE.md`](CLAUDE.md).

## Building

```bash
./gradlew assembleDebug
```

Requires a JDK compatible with the Android Gradle Plugin version pinned in
`gradle/wrapper/gradle-wrapper.properties`, and Android SDK platform 26
installed (`compileSdkVersion`/`targetSdkVersion` 26).

## Status

This is a small hobby/weekend project (first commit March 2018) using an old,
deprecated Android Support Library setup. It has no tests beyond the default
generated stubs and no CI.
