package info.andersonpa.polishhorseshoesscoring.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import info.andersonpa.polishhorseshoesscoring.R;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "polishhorseshoes.db";
    private static final int DATABASE_VERSION = 1;

    private Dao<Game, Long> gameDao;
    private Dao<Player, Long> playerDao;
    private Dao<Throw, Long> throwDao;
    private Dao<TeamStats, Long> teamStatsDao;

    private List<Class> tableClasses = new ArrayList<>();

    private Context myContext;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
        tableClasses.add(Game.class);
        tableClasses.add(Player.class);
        tableClasses.add(Throw.class);
        tableClasses.add(TeamStats.class);

        myContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource) {
        Log.i("DatabaseHelper", "Attempting to create db");
        try {
            createAll(connectionSource);
            createPlayers();
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Unable to create database", e);
        }
    }

    public void createPlayers() {
        Log.i("DatabaseHelper", "Attempting to add standard players to db");
        try {
            Dao<Player, Long> pDao = getPlayerDao();
            pDao.create(new Player("Phillip", "Anderson", "pilip"));
            pDao.create(new Player("Michael", "Cannamela", "miker"));
            pDao.create(new Player("Michael", "Freeman", "freeeedom"));
            pDao.create(new Player("Matt", "Tuttle", "king tut"));
            pDao.create(new Player("Julian", "Spring", "juice"));
            pDao.create(new Player("Zach", "Last", "zachomatic"));

        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Unable to create all players", e);
        }
    }


    @Override
    public void onUpgrade(final SQLiteDatabase sqliteDatabase,
                          final ConnectionSource connectionSource, int oldVer, final int newVer) {
        Log.w("DatabaseHelper", "Attempting to upgrade from version " + oldVer + " to" +
                " version " + newVer);

        switch (oldVer) {
            case 10:
            case 11:
                increment_11(sqliteDatabase, connectionSource);
                break;
            default:
                try {
                    dropAll(connectionSource);
                    createAll(connectionSource);
                } catch (SQLException e) {
                    Log.e(DatabaseHelper.class.getName(), "Unable to upgrade database from " +
                            "version " + oldVer + " to " + newVer, e);
                }
        }
    }

    private void increment_11(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource) {
        //        TODO: This is left here for reference, but should be removed.
        try {
            Log.i("DatabaseHelper", "Attempting to upgrade from version 11 to " + "version 12");
            // throw table
            Dao<Throw, Long> tDao = getThrowDao();
            DatabaseUpgrader.increment_11(connectionSource, tDao);

        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Unable to upgrade database from version " + 11
                    + " to " + 12, e);
        }
    }

    public void createAll() {
        try {
            createAll(getConnectionSource());
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.toString(), e.getMessage());
            throw new RuntimeException("Could not create tables: ", e);
        }
    }

    public void dropAll() {
        try {
            dropAll(getConnectionSource());
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.toString(), e.getMessage());
            throw new RuntimeException("Could not drop tables: ", e);
        }
    }

    public void clearAll() {
        try {
            clearAll(getConnectionSource());
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.toString(), e.getMessage());
            throw new RuntimeException("Could not clear tables: ", e);
        }
    }

    private void createAll(ConnectionSource connectionSource) throws SQLException {
        for (Class c : tableClasses) {
            TableUtils.createTableIfNotExists(connectionSource, c);
        }
    }

    private void dropAll(ConnectionSource connectionSource) throws SQLException {
        for (Class c : tableClasses) {
            TableUtils.dropTable(connectionSource, c, true);
        }
    }

    private void clearAll(ConnectionSource connectionSource) throws SQLException {
        for (Class c : tableClasses) {
            TableUtils.clearTable(connectionSource, c);
        }
    }

    Dao<TeamStats, Long> getTeamStatsDao() throws SQLException {
        if (teamStatsDao == null) {
            teamStatsDao = getDao(TeamStats.class);
        }
        return teamStatsDao;
    }

    Dao<Game, Long> getGameDao() throws SQLException {
        if (gameDao == null) {
            gameDao = getDao(Game.class);
        }
        return gameDao;
    }

    Dao<Player, Long> getPlayerDao() throws SQLException {
        if (playerDao == null) {
            playerDao = getDao(Player.class);
        }
        return playerDao;
    }

    Dao<Throw, Long> getThrowDao() throws SQLException {
        if (throwDao == null) {
            throwDao = getDao(Throw.class);
        }
        return throwDao;
    }
}
