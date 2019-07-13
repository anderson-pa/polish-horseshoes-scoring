package info.andersonpa.polishhorseshoesscoring.db;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import info.andersonpa.polishhorseshoesscoring.enums.RuleType;
import info.andersonpa.polishhorseshoesscoring.rulesets.RuleSet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseUpgrader {
    public static void increment_11(ConnectionSource connectionSource, Dao<Throw,
            Long> tDao) throws SQLException {
        tDao.executeRaw(addColumn("throw", "initialOffensivePlayerHitPoints", "INTEGER", "10"));
        tDao.executeRaw(addColumn("throw", "initialDefensivePlayerHitPoints", "INTEGER", "10"));
    }

    public static String replaceNulls(String tableName, String columnName, String value) {
        return "UPDATE " + tableName + " SET " + columnName + "=" + value + " where " +
                columnName + " is NULL;";

    }

    public static String addColumn(String tableName, String columnName, String type,
                                   String defaultValue) {
        return "ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + type + " DEFAULT " +
                "" + defaultValue + ";";
    }

    public static String addBooleanDefaultZeroColumn(String tableName, String columnName) {
        return addColumn(tableName, columnName, "BOOLEAN", "0");
    }

    public static List<Long> updateScores(Dao<Game, Long> gDao, Context context) {
//        ActiveGame ag = null;
//        String msg;
//        int[] oldScores = new int[2];
//        int[] newScores = new int[2];
        List<Long> badGames = new ArrayList<>();
//        for (Game g : gDao) {
//            // Log.i("DatabaseUpgrader.updateScores()","processing game "+g.getId());
//            oldScores[0] = g.getMember1Score();
//            oldScores[1] = g.getMember2Score();
//
//            ag = new ActiveGame(g.getId(), context, g.ruleset_id);
//            // saveAllThrows is extremely slow. any way to speed up?
//            ag.saveAllThrows(); // this also calls updateThrowsFrom(0)
//            ag.saveGame();
//            newScores[0] = ag.getGame().getMember1Score();
//            newScores[1] = ag.getGame().getMember2Score();
//
//            if (!(are_scores_equal(oldScores, newScores))) {
//                msg = String.format("bad game %d: (%d,%d)->(%d,%d)", g.getId(), oldScores[0],
//                        oldScores[1], newScores[0], newScores[1]);
//                Log.w("DB_Upgrader", msg);
//                badGames.add(g.getId());
//            }
//        }
        return badGames;
    }

    public static List<Long> checkThrows(Dao<Throw, Long> tDao,
                                         Context context) throws SQLException {
        String msg;
        List<Long> badThrows = new ArrayList<>();
        // TODO: make this dynamic once implemented in db
        RuleSet rs = RuleType.RS00;
        for (Throw t : tDao) {
            if (!rs.isValid(t)) {
                msg = "bad throw: " + t.getId() + "- " + t.invalidMessage;
                Log.w("DB_Upgrader", msg);
                badThrows.add(t.getId());
            }
        }
        return badThrows;
    }

    public static boolean are_scores_equal(int[] oldScores, int[] newScores) {
        return oldScores[0] == newScores[0] && oldScores[1] == newScores[1];
    }
}
