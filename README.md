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
- AndroidX + Material Components, `minSdk 23`, `targetSdk`/`compileSdk 36`
  (migrated from the pre-AndroidX Support Library — see "Modernization" below)
- Gradle build (`gradlew`/`gradlew.bat`), Android Gradle Plugin 9.0.1 / Gradle 9.1.0
- UI: `ViewPager` (classic, not ViewPager2) + `TabLayout` with 4 tabs, each
  backed by a `Fragment` and a `RecyclerView`
- All application state is held in static in-memory lists on `MainActivity`
  (no database, no persistence beyond the player-name CSV saved in
  `SharedPreferences`)
- Settings screen intentionally still uses the old platform
  `android.preference.PreferenceActivity`/`PreferenceFragment` APIs (deprecated
  since API 29 but still present on-device) rather than
  `androidx.preference` — see "Modernization" below for why.

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

Requires JDK 17+ and Android SDK platform 36 + build-tools 36.0.0 installed
(matches `compileSdk`/`targetSdk 36` in `app/build.gradle`).

## Modernization (AndroidX / Play Store readiness)

As of October 2026, Google Play requires new apps and updates to target
Android 16 (API 36). The app originally targeted API 26 on the old,
EOL'd `com.android.support:*` libraries, which can't be used at that API
level — the following was changed to make it buildable and submittable
again:

- **AndroidX migration**: every `android.support.*` import/XML tag replaced
  with its `androidx.*` or `com.google.android.material.*` equivalent
  (`android.useAndroidX=true` in `gradle.properties`). The *Java package* of
  the app's own classes (`com.example.huzz00mc.csocsomaster`) was **not**
  renamed/moved — only the library imports changed.
- **`compileSdk`/`targetSdk` 36**, `minSdk` bumped 15 → 21 → 23 (the latter
  bump, in the lint-cleanup pass, was required by `androidx.appcompat`
  1.8.0/`com.google.android.material` 1.14.0 — both declare a minSdk 23 floor,
  so pinning older versions was the only alternative), AGP 7.2.1 → 9.0.1,
  Gradle 7.3.3 → 9.1.0 (9.4.0/9.6.0 briefly, then downgraded — Android Studio's
  installed AGP support lagged behind; see git history), Java source/target
  compatibility set to 17.
- **`applicationId` changed to `com.tepsi.csocsomaster`** (was
  `com.example.huzz00mc.csocsomaster`, a placeholder-style name). This is the
  public, permanent identifier Play Store will use — it is intentionally
  *different* from the namespace/Java package (that's a normal, fully
  supported AGP pattern; it just avoids moving the entire `java/` source
  tree). **This cannot be changed after the first Play Store upload**, so
  double-check it's what you want before publishing.
- **`android:exported` added to both activities** — required for any app
  targeting API 31+; the build fails without it on components with
  intent-filters (`MainActivity`).
- **`android:enableOnBackInvokedCallback="true"`** added — opts into the
  modern predictive-back gesture (the app has no custom back-press handling
  to conflict with it).
- **Release signing scaffolding** added (see below) but no keystore is
  committed — you generate your own.
- **Deliberately left unchanged** (minimal-scope modernization, not a
  redesign): the `ViewPager`/`FragmentPagerAdapter` UI (not migrated to
  ViewPager2), the `android.preference.*`-based Settings screen (not
  migrated to `androidx.preference`), and all business logic.

### Build status

`./gradlew assembleDebug`, `assembleRelease`, `bundleRelease`, `test`, and
`lintDebug` all build successfully (verified Oct 2026 — AGP 9.0.1 / Gradle
9.1.0 / compileSdk 36; `lintDebug` reports zero errors/warnings as of the
lint-cleanup pass below). Two real compile errors turned up during the
original modernization pass, both worth knowing about if you touch this
code:

- **`getDefaultProguardFile('proguard-android.txt')` is no longer
  supported** by current AGP (it hard-codes `-dontoptimize`, which blocks
  R8). Fixed by switching to `proguard-android-optimize.txt`.
- **`switch (view.getId())` / `switch (item.getItemId())` no longer
  compile** — "constant expression required". With the current AGP/AAPT2,
  an app module's own `R.id` fields are no longer guaranteed compile-time
  constants, so you can't `switch` on them (`==` comparisons are fine).
  Fixed by converting the three affected `switch` blocks (`MainActivity`,
  `PlayerFragment`, `MatchFragment`) to `if`/`else if` chains — including
  `MatchFragment`'s intentional `btn_next` → `btn_cancel` fall-through,
  preserved via a shared outer condition. If you add a new `onClick`/
  `onOptionsItemSelected` branch, use `if (id == R.id.whatever)`, not
  `switch`.

### Lint cleanup (Oct 2026)

A follow-up pass eliminated every warning `lintDebug` reported (previously 5
errors, 77 warnings). Most were mechanical (useless `FrameLayout` wrappers,
redundant XML namespaces, hardcoded strings, `android:tint`→`app:tint`,
`Integer.toString()`→`String.format(Locale, ...)`). A few are worth knowing
about:

- **Deleted dead Settings-screen template leftovers**: `content_preference.xml`
  referenced a `PreferenceActivityFragment` class that doesn't exist (a
  `MissingClass` lint *error*, not just a warning) — it, `fragment_main.xml`,
  `menu_preference.xml`, and the `ic_settings*` icon family were all unused
  remnants of Android Studio's default "Settings Activity" template;
  `SettingsActivity`/`SettingsFragment` build their UI programmatically via
  `addPreferencesFromResource()` and never reference any of them.
- **`minSdk` 21 → 23**: see "Modernization" above — forced by taking the
  latest `appcompat`/`material` to clear `GradleDependency` warnings.
- **`NotifyDataSetChanged` warnings suppressed, not fixed**: every call site
  rebuilds or reorders the *entire* player list (reset, shuffle-on-add,
  resort), so there's no stable per-item mapping to hand to a targeted
  `notifyItem*()` call. Introducing `DiffUtil` to do this properly was judged
  out of scope for a lint pass on a hobby project — see
  `@SuppressLint("NotifyDataSetChanged")` call sites for the reasoning.
- **Generated `ic_launcher_monochrome.png`** (one per mipmap density) for the
  `MonochromeLauncherIcon` check by thresholding the existing
  `ic_launcher_foreground.png` artwork (non-white → opaque white, white →
  transparent) rather than hand-drawing new art — the foreground logo already
  reads as flat color blocks on white, so this is a faithful derivation, not
  new iconography.
- **`slider_bg.jpg` moved** from `drawable/` to `drawable-nodpi/` (it's a
  density-independent background image, flagged by `IconLocation`).

### Known risk — not yet verified on a real device/emulator

The build compiles and packages cleanly, but it hasn't been **run**
anywhere yet (no emulator/device available in this environment). The main
thing to check first:

- **Edge-to-edge display**: apps targeting API 35+ get edge-to-edge
  enforced by Android (content can draw behind the status/nav bar). The
  main screen already has `android:fitsSystemWindows="true"` on its root
  `CoordinatorLayout`, which has long been the standard fix for this exact
  scenario and likely still works, but this was **not visually verified**.
  Check `MainActivity` and `SettingsActivity` on a real device/emulator for
  any content overlapping the status bar or navigation bar, especially
  `SettingsActivity` (plain `PreferenceActivity`, no special handling).

(`android.preference.PreferenceActivity`/`PreferenceFragment` existing at
API 36 — the other previous open question — is now confirmed: the project
compiles against `compileSdk 36` using them without issue.)

## Play Store release

### 1. Install the Android SDK + build

```bash
./gradlew assembleDebug      # sanity check first
./gradlew bundleRelease       # produces app/build/outputs/bundle/release/app-release.aab
```

Play Store requires an `.aab` (Android App Bundle), not an `.apk` — `bundleRelease` already produces the right format.

### 2. Create a release keystore (one-time, do this yourself — don't skip)

```bash
cd app
keytool -genkeypair -v -keystore csocsomaster-release.jks -alias csocsomaster -keyalg RSA -keysize 2048 -validity 10000
cp keystore.properties.sample keystore.properties
# edit keystore.properties with the passwords/alias you just chose
```

`keystore.properties` and `*.jks` are gitignored — **never commit them**.
Store the keystore and its passwords somewhere safe outside this repo (e.g. a
password manager); if you lose it, you can never publish an update to this
app under this listing again. Once `app/keystore.properties` exists,
`bundleRelease`/`assembleRelease` automatically sign with it.

### 3. What's left — manual Play Console steps (can't be scripted/automated)

These require your own Google Play Developer account (one-time $25 fee) at
<https://play.google.com/console> and are genuinely manual:

- Create the app listing, pick a package name match for `com.tepsi.csocsomaster`
- Store listing content: title, short/full description, screenshots (phone
  + optionally tablet), feature graphic, app icon
- Content rating questionnaire
- Data safety form (this app collects/transmits nothing — answer accordingly)
- Privacy policy URL (required even for apps with no data collection, by
  current Play Console policy — a one-page "this app stores data only on
  your device" statement hosted anywhere is sufficient)
- Upload the signed `.aab` from step 1, set a release track (internal
  testing first is recommended), and submit for review

## Status

This is a small hobby/weekend project (first commit March 2018), modernized
for Play Store submission in October 2026. It has no automated tests beyond
the default generated stubs and no CI.
