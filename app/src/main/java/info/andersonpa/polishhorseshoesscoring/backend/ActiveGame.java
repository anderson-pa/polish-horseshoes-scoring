package info.andersonpa.polishhorseshoesscoring.backend;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

import info.andersonpa.polishhorseshoesscoring.db.Game;
import info.andersonpa.polishhorseshoesscoring.db.Throw;
import info.andersonpa.polishhorseshoesscoring.enums.RuleType;
import info.andersonpa.polishhorseshoesscoring.rulesets.RuleSet;

public class ActiveGame {
    protected String LOGTAG = getClass().getSimpleName();
    private Context context;
    private int active_idx;
    private Game g;
    private ArrayList<Throw> throws_list;
    private RuleSet rs;
    private List<Throw[]> innings_list = new ArrayList<>();
    private String[] team_names = new String[2];
    private String session_name;
    private String venue_name;
    private Dao<Game, Long> g_dao;
    private Dao<Throw, Long> t_dao;
    private boolean save_to_db;


    public ActiveGame(Context context, String gId) {
        g_dao = Game.getDao(context);
        t_dao = Throw.getDao(context);
        this.context = context;
        g = retrieveOrCreateGame(gId);
        rs = RuleType.map.get(g.getRulesetId());
        rs.setContext(context);

        try {
            throws_list = g.getThrowList(context);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve throws for game " + g.getId() + ": ", e);
        }

        if (throws_list.size() > 0) {
            active_idx = throws_list.size() - 1;
        } else {
            active_idx = 0;
        }

        updateScoresFrom(0);
    }

    public void setSaveToDB(boolean save_to_db) {
        this.save_to_db = save_to_db;
    }

    private Game retrieveOrCreateGame(String gId) {
        Uri pl_uri;
        Cursor cursor;
        long[] gm_ids = new long[2];
        int ruleset_id;

        pl_uri = new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT)
                .authority("info.andersonpa.pocketleague.provider").appendPath("game")
                .appendPath(gId).build();
        try {
            cursor = context.getContentResolver().query(pl_uri, null, null, null, null);
            cursor.moveToFirst();
            String ruleset_str = cursor.getString(cursor.getColumnIndex("ruleset_id"));
            ruleset_id = Integer.valueOf(ruleset_str);
            session_name = cursor.getString(cursor.getColumnIndex("session_name"));
            venue_name = cursor.getString(cursor.getColumnIndex("venue_name"));

            cursor.close();

            pl_uri = new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT)
                    .authority("info.andersonpa.pocketleague.provider").appendPath("game_member")
                    .appendPath(gId).build();
            cursor = context.getContentResolver().query(pl_uri, null, null, null, null);
            while (cursor.moveToNext()) {
                gm_ids[cursor.getPosition()] = 0; //cursor.getLong(cursor.getColumnIndex("id"));
                team_names[cursor.getPosition()] = cursor.getString(cursor.getColumnIndex("team_name"));
            }
            cursor.close();
        } catch (Exception e){
            loge("No Content Provider: ", e);
            session_name = "No session";
            venue_name = "No venue";
            team_names[0] = "Team 1";
            team_names[1] = "Team 2";
            gm_ids[0] = 0;
            gm_ids[1] = 1;
            ruleset_id = 1;
        }

        try {
            g = g_dao.queryBuilder().where().eq(Game.POCKETLEAGUE_ID, gId).queryForFirst();
        } catch (SQLException e) {
            loge("Could not find  game: ", e);
        }

        if (g == null) {
            try {
                g = new Game(gId, gm_ids[0], gm_ids[1], ruleset_id);
                g_dao.create(g);
            } catch (SQLException e) {
                loge("Could not create game: ", e);
            }
        }

        log("Game ID is:" + g.getPocketLeagueId());
        return g;
    }

    public String getSessionName() {
        return session_name;
    }

    public String getVenueName() {
        return venue_name;
    }

    public String[] getTeamNames() {
        return team_names;
    }

    public RuleSet getRuleSet() {
        return rs;
    }

    public void setRuleSet(RuleSet rs) {
        // For testing purposes only.
        this.rs = rs;
        g.setRulesetId(rs.getId());
    }

    private Throw makeNextThrow() {
        return g.makeNewThrow(getThrowCount());
    }

    private void setInitialScores(Throw t, Throw previousThrow) {
        int[] scores = rs.getFinalScores(previousThrow);
        t.initialDefensivePlayerScore = scores[0];
        t.initialOffensivePlayerScore = scores[1];
    }

    private void setInitialScores(Throw t) {
        t.initialDefensivePlayerScore = 0;
        t.initialOffensivePlayerScore = 0;
    }

    public void updateScoresFrom(int idx) {
        Throw t, u;
        for (int i = idx; i < getThrowCount(); i++) {
            t = getThrow(i);
            if (i == 0) {
                setInitialScores(t);
                t.offenseFireCount = 0;
                t.defenseFireCount = 0;
            } else {
                u = getPreviousThrow(t);
                setInitialScores(t, u);
                rs.setFireCounts(t, u);
            }
        }
        updateGameScore();
    }

    private void updateGameScore() {
        int[] scores = {0, 0};
        if (getThrowCount() > 0) {
            Throw lastThrow = getThrow(getThrowCount() - 1);
            if (Throw.isP1Throw(lastThrow)) {
                scores = rs.getFinalScores(lastThrow);
            } else {
                int[] tmp = rs.getFinalScores(lastThrow);
                scores[1] = tmp[0];
                scores[0] = tmp[1];
            }
        }
        g.setMember1Score(context, scores[0]);
        g.setMember2Score(context, scores[1]);
    }

    public void setThrowType(Throw t, int t_type) {
        rs.setThrowType(t, t_type);
    }

    public void setThrowResult(Throw t, int t_result) {
        rs.setThrowResult(t, t_result);
    }

    public void setDeadType(Throw t, int dead_type) {
        rs.setDeadType(t, dead_type);
    }

    public void setIsTipped(Throw t) {
        rs.setIsTipped(t, !t.isTipped);
    }

    public boolean usesAutoFire() {
        return rs.useAutoFire();
    }

    private ArrayList<Long> getThrowIds() {
        HashMap<String, Object> m;
        List<Throw> tList;
        ArrayList<Long> throwIds = new ArrayList<>();
        int cnt = 0;
        try {
            for (Throw t : throws_list) {
                m = t.getQueryMap();
                tList = t_dao.queryForFieldValuesArgs(m);
                if (tList.isEmpty()) {
                    throwIds.add((long) -1);
                } else {
                    throwIds.add(tList.get(0).getId());
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("could not query for throw ids");
        }
        return throwIds;
    }

    public Throw getActiveThrow() {
        return getThrow(active_idx);
    }

    public void updateActiveThrow(Throw t) {
        if (active_idx < 0) {
            throw new RuntimeException("must have positive throw index, not: " + active_idx);
        } else if (active_idx >= 0 && active_idx < getThrowCount()) {
            t.setThrowIndex(active_idx);
            t = throws_list.set(active_idx, t);
            saveThrow(t);
        } else if (active_idx == getThrowCount()) {
            t.setThrowIndex(active_idx);
            throws_list.add(t);
            saveThrow(t);
        } else if (active_idx > getThrowCount()) {
            throw new RuntimeException("cannot set throw " + active_idx + " in the far future");
        }
        updateScoresFrom(active_idx);
    }

    public Throw updateActiveThrowGetNext(Throw t) {
        updateActiveThrow(t);
        active_idx++;
        Throw u = getActiveThrow();
        updateScoresFrom(active_idx);
        return u;
    }

    public Throw getThrow(int idx) {
        Throw t = null;
        if (idx < 0) {
            throw new RuntimeException("must have positive throw index, not: " + idx);
        } else if (idx >= 0 && idx < getThrowCount()) {
            t = throws_list.get(idx);
        } else if (idx == getThrowCount()) {
            t = makeNextThrow();
            if (idx == 0) {
                setInitialScores(t);
            } else {
                Throw u = getPreviousThrow(t);
                setInitialScores(t, u);
            }

            throws_list.add(t);
        } else if (idx > getThrowCount()) {
            throw new RuntimeException("cannot get throw " + idx + " from the far future");
        }
        if (t == null) {
            throw new NullPointerException("Got invalid throw for index " + idx);
        }
        return t;
    }

    public Throw getPreviousThrow(Throw t) {
        Throw u = null;
        int idx = t.getThrowIdx();
        if (idx <= 0) {
            throw new RuntimeException("throw " + idx + " has no predecessor");
        } else if (idx > 0 && idx <= getThrowCount()) {
            u = throws_list.get(idx - 1);
        } else if (idx > getThrowCount()) {
            throw new RuntimeException("cannot get predecessor of throw " + idx + " from the far " +
                    "future");
        }
        if (u == null) {
            throw new NullPointerException("Got invalid predecessor for throw index " + idx);
        }
        return u;
    }

    public int getThrowCount() {
        return throws_list.size();
    }

    public ArrayList<Throw> getThrows() {
        return throws_list;
    }

    public List<Item_Inning> getInnings() {
        List<Item_Inning> innings = new ArrayList<>();
        if (throws_list.size() > 0) {
            for (Integer i = 0; i < throws_list.size() - 1; i=i+2) {
                innings.add(new Item_Inning(i/2+1, throws_list.get(i), throws_list.get(i + 1)));
            }
            if (throws_list.size() % 2 == 1) {
                innings.add(new Item_Inning(throws_list.size()/2+1, throws_list.get(throws_list.size()-1), null));
            }
        }
        return innings;
    }

    public int getActiveIdx() {
        return active_idx;
    }

    public void setActiveIdx(int idx) {
        this.active_idx = idx;
    }

    public Game getGame() {
        return g;
    }

    public void setGame(Game g) {
        this.g = g;
    }

    public long getGameId() {
        return g.getId();
    }

    public Date getGameDate() {
        return g.getDatePlayed();
    }

    /* Saving functions */
    private void saveThrow(Throw t) {
        if (save_to_db && g != null) {
            HashMap<String, Object> m = t.getQueryMap();
            List<Throw> tList;
            try {
                tList = t_dao.queryForFieldValuesArgs(m);
            } catch (SQLException e) {
                throw new RuntimeException("could not query for throw " + t.getThrowIdx() + ", " +
                        "game " + t.getGame().getId());
            }
            try {
                if (tList.isEmpty()) {
                    t_dao.create(t);
                } else {
                    t.setId(tList.get(0).getId());
                    t_dao.update(t);
                }
            } catch (SQLException e) {
                throw new RuntimeException("could not create/update throw " + t.getThrowIdx() + ", game " + t.getGame().getId());
            }
        }
    }

    public void saveAllThrows() {
        updateScoresFrom(0);
        if (save_to_db && g != null) {
            final ArrayList<Long> throwIds = getThrowIds();
            try {
                t_dao.callBatchTasks(new Callable<Void>() {
                    public Void call() throws SQLException {
                        long id;
                        Throw t;
                        for (int i = 0; i < throws_list.size(); i++) {
                            id = throwIds.get(i);
                            t = throws_list.get(i);
                            if (id == -1) {
                                t_dao.create(t);
                            } else {
                                t.setId(id);
                                t_dao.update(t);
                            }
                        }
                        return null;
                    }
                });
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void saveGame() {
        if (save_to_db && g != null) {
            try {
                g_dao.update(g);
            } catch (SQLException e) {
                throw new RuntimeException("Could not save game " + g.getId());
            }
        }
    }

    public void log(String msg) {
        Log.i(LOGTAG, msg);
    }

    public void logd(String msg) {
        Log.d(LOGTAG, msg);
    }

    public void loge(String msg, Exception e) {
        Log.e(LOGTAG, msg + ": " + e.getMessage());
    }
}
