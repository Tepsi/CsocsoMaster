# CsocsoMaster — üzleti logika és megvalósítás

## 1. Cél

Az app egy csocsó (asztali foci) bajnokság/est lebonyolítását segíti egy
csapaton/baráti társaságon belül: játékosok felvétele, 2v2 meccsek
kisorsolása úgy, hogy mindenki kb. egyenlő arányban játsszon, eredmények
rögzítése, és egy élő tabella/eredménylista vezetése. Egyetlen Android
eszközön fut, egy közös képernyőn (nincs több-eszközös szinkron, nincs
backend).

## 2. Domain modell (`DAO` package)

| Osztály | Felelősség |
|---|---|
| `Player` | Egy játékos: név, egyedi `id`, lejátszott meccsek száma (`played`), győzelem/vereség, kapott/lőtt gólok, win/lose arány, `active` flag. |
| `Pair` | Egy *csapat* = két játékos rendezetlen párja. Konstruktorban mindig `player1.id < player2.id` sorrendbe rendezi önmagát (`sortPlayers()`), hogy egy adott párosítás mindig ugyanazt a `Pair`-t jelentse. Számolja, hányszor játszott együtt ez a két ember (`played`). `disqualifies(other)` igaz, ha a két `Pair`-nek van közös játékosa — ez zárja ki, hogy valaki saját magával szemben vagy duplán szerepeljen egy meccsben. |
| `Match` | Egy meccs = két `Pair` egymás ellen. Konstruktorban is kanonikus sorrendbe rendezi a két párt (`pair1.player1.id` szerint), hogy a meccs azonosítása (`equals`) iránytól független legyen. `getPairCount()` = a két oldal eddig lejátszott meccseinek összege (ez a párok "elfáradtságát" méri). `count` mező: hányszor játszották *már le* pontosan ezt a 4 fős meccset (ismétlésszám). |
| `MatchParticipants` | Egy adott 4 fős *szereplőgárda* (a `Match` 4 játékosa, párosítástól függetlenül) hányszor lépett már pályára egymás ellen, bármilyen pár-felosztásban. Ez egy második, lazább csoportosítás a `Match`-nél (ami pár-specifikus), a `Pair.played`-nél (ami csak 2 fős) szigorúbb szinten. |
| `FinishedMatch` | Egy lezárt meccs pillanatfelvétele: a két `Pair` + a végeredmény (`score1`, `score2`). Csak megjelenítésre/históriára kell, nem vesz részt a sorsolásban. |

### Hierarchia a "hányszor játszottak már" mérésekben

1. `Player.played` — egyén szintje
2. `Pair.played` — ez a *konkrét 2 fős csapat* hányszor állt össze
3. `MatchParticipants.played` — ez a *4 fős társaság* hányszor játszott egymás ellen (bármilyen pár-bontásban)
4. `Match.count` — ez a *pontos 4 fős meccs, ezzel a pár-bontással* hányszor fordult már elő

A sorsoló algoritmus (lásd 4. pont) ezt a négy szintet használja egymás után,
mint tie-breaker láncot, a legdurvábbtól (egyén) a legfinomabbig (pontos
meccs-ismétlés).

## 3. Globális állapot (`MainActivity`)

Nincs adatbázis és nincs ViewModel/repository réteg: minden állapotot
`MainActivity` `public static` listái tárolnak, amiket a fragmentek
közvetlenül olvasnak/írnak:

- `playerList: List<Player>`
- `pairs: List<Pair>` — minden lehetséges 2 fős párosítás az eddigi
  játékosokból
- `matches: List<Match>` — minden lehetséges, szabályos (nem
  diszkvalifikált) 4 fős meccs-kombináció
- `matchParticipantss: List<MatchParticipants>` — minden lehetséges 4 fős
  társasághoz egy számláló
- `finishedMatches: List<FinishedMatch>` — lejátszott meccsek históriája

Ez az állapot **csak a futó process életciklusáig él**. Az egyetlen, ami
túléli az app bezárását: a játékosnevek CSV-je a `SharedPreferences`
`"players"` kulcs alatt (`savePlayerList`/`loadPlayerList`) — ez csak a
*neveket* menti, statisztikát és meccstörténetet nem.

`Player.currentId` statikus számláló, csak process-újraindításkor nullázódik
— "Adatok törlése" (reset) a listákat törli, de az id-sorozatot nem.

## 4. Új játékos felvétele és a párok/meccsek generálása (`PlayerFragment`)

`createPlayer(name)` lépései:

1. Új `Player` létrehozása (automatikusan megkapja a következő `id`-t és
   `played = 0`-t).
2. **Fair start**: ha az új játékos `played` értéke kisebb, mint a jelenlegi
   legalacsonyabb lejátszott-meccs-szám a mezőnyben
   (`MainActivity.minPlayed()`), akkor az új játékos `played`-jét felhúzza
   addig a szintig — így egy később csatlakozó játékos nem kap "ingyen"
   előnyt azzal, hogy 0-ról indul, miközben mások már játszottak.
3. Ha már legalább 4 játékos van:
   - `Collections.shuffle(playerList)` — a sorrend összekeverése (ez később
     a végsorrendnél, nem a sorsolásnál játszik szerepet, mert a sorsolás
     mindig `played` szerint rendez).
   - `generatePairs()`: minden játékospár (i<j) végigjárása, és minden *még
     nem létező* `Pair` hozzáadása a globális `pairs` listához (dedup a
     `Pair.equals(Pair)` manuális hívásával — lásd 6. pont a `equals`
     csapdáról). Végül `shuffle`.
   - `generateMatches()`: minden `pair1 x pair2` kombináció, kizárva ahol
     `pair1.disqualifies(pair2)` (közös játékos), minden új, még nem
     létező `Match` hozzáadása a `matches` listához, és minden új 4 fős
     társasághoz egy `MatchParticipants` bejegyzés létrehozása, ha még nem
     létezik. Végül `shuffle`.

Fontos: ez a két generáló függvény **csak hozzáad**, soha nem távolít el. Ha
egy játékos inaktívvá válik vagy törlésre kerülne, a `matches`/`pairs`
listákból nem esik ki — a kiszűrése a sorsolás pillanatában,
szűréssel történik (lásd lent), nem az adatstruktúra karbantartásával.

## 5. Játékos aktív/inaktív állapota

`MainActivity.onListFragmentInteraction(player)` (játékosra kattintás a
listában) kapcsolja a `Player.active` flaget. Különleges szabály: ha egy
játékos éppen *inaktívvá* válik és a `played` száma kisebb, mint a nélküle
számolt minimum (`minPlayed(excludedPlayer)`), akkor a `played`-jét felhúzza
arra a szintre, mielőtt kikapcsolná — így amikor **visszakapcsolják**, nem
fog "jogosulatlan előnnyel" (alacsonyabb `played` miatt túl korán soron
kívül) visszakerülni a sorba.

## 6. A következő meccs kisorsolása — `MatchFragment.generateNextMatch()`

Ez a motor szíve. Minden "Következő" / "Mégsem" gombnyomásra újra lefut.
Bemenete: az aktív (`active == true`) játékosok listája (`activePlayerList`,
`MainActivity.playerList`-ből szűrve). Ha 4-nél kevesebb aktív játékos van,
hibaüzenet (`too_few_active_players`), nincs sorsolás.

Lépésről lépésre:

1. **Rendezés `played` szerint** (`Collections.sort(activePlayerList)` —
   `Player.compareTo` a `played` mezőt hasonlítja).
2. **`toPlay` halmaz összeállítása**: az első 4 (legkevesebbet játszott)
   játékos biztosan bekerül; onnantól minden további játékos is bekerül,
   *amíg* a `played` értéke pontosan megegyezik a 4. játékos
   (`lowestToPlay`) értékével. Így `toPlay` egy "legkevesebbet játszott
   réteg" — lehet pontosan 4 fő, vagy több, ha több embernek egyenlő, minimális
   `played`-je van.
3. **`mustPlay` halmaz**: ha `toPlay` pontosan 4 fő, akkor mindannyian
   kötelezően játszanak (`mustPlay = toPlay`). Ha `toPlay` több mint 4 fő
   (azonos legalacsonyabb `played` miatt), akkor `mustPlay` csak azokat
   tartalmazza, akiknek a `played`-je **szigorúan kisebb**, mint
   `lowestToPlay` — ami ebben az ágban üres halmazt eredményez (lásd 7.
   pont, ismert él-eset).
4. **Jelölt meccsek szűrése** a teljes `MainActivity.matches` listából:
   egy `Match` jelölt, ha
   - minden `mustPlay`-ben lévő játékos szerepel benne, **és**
   - a meccs mind 4 szereplője benne van `toPlay`-ben (tehát senki, akinek
     magasabb `played`-je van a rétegnél, nem kerülhet be).
5. **Rangsorolás a jelöltek között**, ebben a sorrendben (korábban leírt
   hierarchia, legdurvábbtól a legfinomabbig):
   1. `Match.compareTo` → `getPairCount()` (a két `Pair.played` összege) —
      minél kevesebbszer játszott már *akár az egyik, akár a másik pár*,
      annál előrébb.
   2. Azonos `pairCount`-ú jelöltek között: `MatchParticipants.played` — a
      4 fős társaság hányszor találkozott már egymással bármilyen
      pár-bontásban.
   3. Azonos `MatchParticipants.played` esetén: `Match.count` — ugyanez a
      *pontos* pár-bontás hányszor fordult már elő, a kevesebb nyer.
   A ciklus a legjobb (`currentMatch`) jelöltet tartja meg; `break`-kel korán
   kilép, amint egy rosszabb `pairCount`-ú vagy `MatchParticipants.played`-ű
   jelölt jönne (mivel `possibleMatches` már rendezve van `pairCount`
   szerint, ez helyes korai leállás az első két szinten).
6. A kiválasztott `Match` megjelenik (`displayMatch`) — 4 név a képernyőn.

### Mi történik "Következő" gombnyomásra, ha már van aktív meccs

A `btn_next` eset *átesik* (fall-through, nincs `break`) a `btn_cancel`
ágba is — tehát egy gombnyomás **egyszerre**:
- rögzíti az előző meccs eredményét (`increaseCount`, `match.saveResult`,
  `FinishedMatch` hozzáadása a históriához), **és**
- rögtön legenerálja és kijelzi a következő meccset.

Ezzel a felhasználó egy gombbal zár le egy meccset és nyit egy újat; a
"Mégsem" gomb csak az új meccs sorsolását futtatja le eredményrögzítés
nélkül (pl. ha félre akarja dobni a kisorsolt párosítást, vagy ha még nem
indult meccs).

### Eredményrögzítés részletei (`increaseCount` + `Match.saveResult`)

- `Match.getPair1().increaseCount()` / `getPair2().increaseCount()`: növeli
  a `Pair.played`-et és mindkét benne lévő `Player.played`-et.
- `increaseMatchCount(match)`: megkeresi a `matches` listában az
  (`Match.equals`) egyező meccset, és növeli a `Match.count`-ját.
- `MainActivity.findMatchParticipants(match).increaseCount()`: a megfelelő
  4 fős társaság számlálóját is növeli.
- `match.saveResult(score1, score2)`: mindkét `Pair`-re meghívja
  `saveResult`, ami mindkét oldal mindkét játékosára lefuttatja
  `Player.addMatch(scoreSaját, scoreEllenfél)` → frissíti `goalsFor`/
  `goalsAgainst`-ot, és `incWon()`/`incLost()`-tal a győzelem/vereség
  számlálót + a `winLoseRatio`-t (`won / (won + lost)`).

## 7. Tabella / rangsor (`ResultPlayerFragment` + `MyPlayerRecyclerViewAdapter2`)

A `BY_RESULTS` komparátor sorrendje:

1. **Win/lose arány** csökkenő sorrendben (elsődleges szempont).
2. Egyenlő arány esetén: **gólkülönbség** (lőtt − kapott gól) csökkenő
   sorrendben.
3. Ha a gólkülönbség is egyenlő: **lőtt gólok száma** csökkenő sorrendben.

Csak azok a játékosok jelennek meg a táblázatban, akiknek `played > 0`
(tehát játszottak már legalább egy meccset).

## 8. Eredménylista (`FinishedMatchFragment` + `ResultRecyclerViewAdapter`)

Egyszerű, időrendi (hozzáadás sorrendjében) lista a `finishedMatches`
listából. A győztes pár neve/eredménye zöld, a vesztesé piros háttérszínt
kap (`Color.GREEN` / `Color.RED`), döntetlen eset nincs kezelve (a kód
`score1 > score2` alapján dönt, egyenlőség esetén a 2. oldal "nyer" — ez
csocsóban nem fordulhat elő, mert nincs döntetlen).

## 9. Beállítások (`SettingsFragment` / `preferences.xml`)

Két beállítás, `SharedPreferences`-ben tárolva:

- `pref_keep_score` (checkbox, alapértelmezett `true`): ha ki van kapcsolva,
  a `MatchFragment` elrejti a pontszám-választó UI elemeket
  (`setScoreVisibility`) — a meccsek így "csak párosítás", eredmény nélkül
  futnak.
- `pref_win_score` (szöveg/szám, alapértelmezett `5`): hány gólig megy a
  meccs. Ez csak a pontszám-választó UI felső határát és kezdőértékét
  szabja meg (`maxScore`); **nincs automatikus meccs-lezárás** ennél a
  pontszámnál — a felhasználó manuálisan állítja be a végeredményt a +/−
  gombokkal, majd nyomja meg a "Következő"-t.

## 10. Ismert él-esetek / korlátok (fontos, ha valaki belenyúl a kódba)

- **4. pontban leírt `mustPlay` üres halmaz eset**: ha a legalacsonyabb
  `played`-del rendelkező réteg (`toPlay`) 4-nél több főt tartalmaz (mert
  többeknek egyenlő, minimális `played`-je van), akkor `mustPlay` üresen
  marad. Ebben az esetben a 4. pont szűrése gyakorlatilag "bármelyik 4 fő
  `toPlay`-ből" alapon enged jelölteket, a `mustPlay`-ellenőrzés nem zár ki
  semmit (az üres listára a `for` ciklus nem talál ellentmondást). Ez
  szándékos vagy nem dokumentált — viselkedésként figyelembe kell venni.
- **`equals` csapda**: `Pair`, `Match`, `MatchParticipants`, `Player`
  mindegyike definiál egy `equals(SajátTípus other)` metódust, de **nem**
  override-olja az `Object.equals(Object)`-et (nincs `@Override`, és az
  aláírás más típusú paraméterrel). Ennek következtében:
  - `List.contains(x)`, `List.remove(x)`, `list1.equals(list2)` **nem** ezt
    a logikát használja, hanem az objektum-azonosságot (`Object.equals`
    default implementáció, ami `==`).
  - A kódban mindenhol, ahol dedup/keresés kell, **kézzel írt ciklus** fut
    (`for (Pair other : pairs) if (pair.equals(other)) ...`), pont azért,
    mert a beépített kollekció-metódusok nem lennének jók erre.
  - Ha valaki ezt "modernizálná" `@Override equals/hashCode`-ra, az
    megváltoztatná a `List.contains`/`HashSet` viselkedését is minden
    meglévő hívási helyen — alapos átvizsgálás nélkül **ne** nyúlj hozzá.
- **Játékos törlése nem támogatott**, csak deaktiválás (`active = false`).
  Nincs UI elem tényleges törlésre.
- **Nincs undo** egy rögzített eredményre — a `Match.saveResult` és a
  számlálók növelése azonnali és végleges a futó session-ön belül; a
  `finishedMatches` lista sem szerkeszthető/törölhető a UI-ból.
- **Process-halál = adatvesztés** a mentett névlistán kívül (lásd 3. pont).
