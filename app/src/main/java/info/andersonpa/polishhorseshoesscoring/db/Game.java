package info.andersonpa.polishhorseshoesscoring.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import info.andersonpa.polishhorseshoesscoring.enums.ThrowResult;
import info.andersonpa.polishhorseshoesscoring.enums.ThrowType;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@DatabaseTable
public class Game {
    public static final String POCKETLEAGUE_ID = "pocketleague_id";
    public static final String FIRST_MEMBER = "member_1_id";
    public static final String SECOND_MEMBER = "member_2_id";
    public static final String RULESET_ID = "ruleset_id";
    public static final String FIRST_ON_TOP = "first_on_top";
    public static final String DATE_PLAYED = "date_played";
    public static final String MEMBER_1_SCORE = "member_1_score";
    public static final String MEMBER_2_SCORE = "member_1_score";
    public static final String IS_COMPLETE = "is_complete";

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(canBeNull = false, unique = true)
    private String pocketleague_id;

    @DatabaseField(canBeNull = false)
    private long member_1_id;

    @DatabaseField(canBeNull = false)
    private long member_2_id;

    @DatabaseField(canBeNull = false)
    public int ruleset_id;

    @DatabaseField(canBeNull = false)
    public boolean first_on_left;

    @DatabaseField(canBeNull = false)
    private Date date_played;

    @DatabaseField
    private int member_1_score;

    @DatabaseField
    private int member_2_score;

    @DatabaseField
    private boolean is_complete = false;

    @ForeignCollectionField
    ForeignCollection<Throw> throw_list;

    public Game() {
    }

    public Game(String pl_id, long member_1_id, long member_2_id, int ruleset_id, Date date_played) {
        this.pocketleague_id = pl_id;
        this.member_1_id = member_1_id;
        this.member_2_id = member_2_id;
        this.ruleset_id = ruleset_id;
        this.date_played = date_played;
    }

    public Game(String pl_id, long member_1_id, long member_2_id, int ruleset_id) {
        this(pl_id, member_1_id, member_2_id, ruleset_id, new Date());
    }

    public static Dao<Game, Long> getDao(Context context) {
        DatabaseHelper helper = new DatabaseHelper(context);
        Dao<Game, Long> d;
        try {
            d = helper.getGameDao();
        } catch (SQLException e) {
            throw new RuntimeException("Could not get game dao: ", e);
        }
        return d;
    }

    public static List<Game> getAll(Context context) throws SQLException {
        Dao<Game, Long> d = Game.getDao(context);
        List<Game> games = new ArrayList<>();
        for (Game g : d) {
            games.add(g);
        }
        return games;
    }

    public boolean isValidThrow(Throw t) {
        boolean isValid = true;
        int idx = t.getThrowIdx();
        switch (idx % 2) {
            // TODO: do players need to be refreshed now that foreign variable is used?
            // first player is on offense
            case 0:
                isValid = isValid && (t.getOffensiveTeamId() == member_1_id);
                break;
            // second player is on defense
            case 1:
                isValid = isValid && (t.getDefensiveTeamId() == member_2_id);
                break;
            default:
                throw new RuntimeException("invalid index " + idx);
        }
        return isValid;
    }

    public ArrayList<Throw> getThrowList(Context context) throws SQLException {
        int tidx, maxThrowIndex;
        ArrayList<Throw> throwArray = new ArrayList<>();

        HashMap<Integer, Throw> throwMap = new HashMap<>();
        HashMap<String, Object> m = new HashMap<>();
        m.put("game_id", getId());

        Dao<Throw, Long> d = Throw.getDao(context);
        List<Throw> dbThrows = d.queryForFieldValuesArgs(m);

        maxThrowIndex = 0;
        if (!dbThrows.isEmpty()) {
            Collections.sort(dbThrows);

            for (Throw t : dbThrows) {
                tidx = t.getThrowIdx();

                // purge any throws with negative index
                if (tidx < 0) {
                    d.delete(t);
                }

                // populate the map
                throwMap.put(tidx, t);

                // keep track of the maximum index
                if (tidx > maxThrowIndex) {
                    maxThrowIndex = tidx;
                }
            }

            // ensure throws in correct order and complete
            Throw t;
            for (int i = 0; i <= maxThrowIndex; i++) {
                t = throwMap.get(i);
                // infill with a caught strike if necessary
                if (t == null) {
                    t = makeNewThrow(i);
                    t.throwType = ThrowType.STRIKE;
                    t.throwResult = ThrowResult.CATCH;
                }
                throwArray.add(t);
            }
        }

        return throwArray;
    }

    public Throw makeNewThrow(int throwNumber) {
        long offensiveTeam_id, defensiveTeam_id;
        if (throwNumber % 2 == 0) {
            offensiveTeam_id = getMember1Id();
            defensiveTeam_id = getMember2Id();
        } else {
            offensiveTeam_id = getMember2Id();
            defensiveTeam_id = getMember1Id();
        }
        Date timestamp = new Date(System.currentTimeMillis());

        return new Throw(throwNumber, this, offensiveTeam_id, defensiveTeam_id, timestamp);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPocketLeagueId() {
        return pocketleague_id;
    }

    public void setPlId(String pocketleague_id) {
        this.pocketleague_id = pocketleague_id;
    }

    public long getMember1Id() {
        return member_1_id;
    }

    public void setMember1Id(long member_1_id) {
        this.member_1_id = member_1_id;
    }

    public long getMember2Id() {
        return member_2_id;
    }

    public void setMember2Id(long member_2_id) {
        this.member_2_id = member_2_id;
    }

    public int getRulesetId() {
        return ruleset_id;
    }

    public void setRulesetId(int ruleset_id) {
        this.ruleset_id = ruleset_id;
    }

    public Date getDatePlayed() {
        return date_played;
    }

    public void setDatePlayed(Date date_played) {
        this.date_played = date_played;
    }

    public int getMember1Score() {
        return member_1_score;
    }

    public void setMember1Score(Context context, int member_1_score) {
        this.member_1_score = member_1_score;
//        Uri uri = new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT)
//                .authority("info.andersonpa.pocketleague.provider")
//                .appendPath("game_member").appendPath(String.valueOf(member_1_id))
//                .build();
//        ContentValues values = new ContentValues();
//        values.put("score", member_1_score);
//        context.getContentResolver().update(uri, values, null, null);
        checkGameComplete(context);
    }

    public int getMember2Score() {
        return member_2_score;
    }

    public void setMember2Score(Context context, int member_2_score) {
        this.member_2_score = member_2_score;
//        Uri uri = new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT)
//                .authority("info.andersonpa.pocketleague.provider")
//                .appendPath("game_member").appendPath(String.valueOf(member_2_id))
//                .build();
//        ContentValues values = new ContentValues();
//        values.put("score", member_2_score);
//        context.getContentResolver().update(uri, values, null, null);
        checkGameComplete(context);
    }

    public boolean getIsComplete() {
        return is_complete;
    }

    public void setIsComplete(Context context, boolean isComplete) {
        this.is_complete = isComplete;
//        Uri uri = new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT)
//                .authority("info.andersonpa.pocketleague.provider").appendPath("game")
//                .appendPath(String.valueOf(pocketleague_id)).build();
//        ContentValues values = new ContentValues();
//        values.put("is_complete", isComplete);
//        values.put("t1_score", member_1_score);
//        values.put("t2_score", member_2_score);

//        try {
//            context.getContentResolver().update(uri, values, null, null);
//        } catch (Exception e) {
//            Log.e("No Content Provider", "setIsComplete: ", e);
//        }
    }

    public void checkGameComplete(Context context) {
        Integer s1 = getMember1Score();
        Integer s2 = getMember2Score();
        if (Math.abs(s1 - s2) >= 2 && (s1 >= 11 || s2 >= 11)) {
//            setIsComplete(context, true);
        } else {
//            setIsComplete(context, false);
        }
    }

    public ForeignCollection<Throw> getThrows() {
        return throw_list;
    }

    public long getWinner() {
        // TODO: should raise an error if game is not complete
        long winner = member_1_id;
        if (getMember2Score() > getMember1Score()) {
            winner = member_2_id;
        }
        return winner;
    }

    public long getLoser() {
        long loser = member_1_id;
        if (getMember2Score() < getMember1Score()) {
            loser = member_2_id;
        }
        return loser;
    }
}
