package info.andersonpa.polishhorseshoesscoring.db;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@DatabaseTable
public class Player {
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String NICKNAME = "nickname";

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(canBeNull = false)
    private String first_name;

    @DatabaseField()
    private String last_name;

    @DatabaseField()
    private String nickname;


    public Player() {
    }

    public Player(String first_name, String last_name, String nickname) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.nickname = nickname;
    }

    public static Dao<Player, Long> getDao(Context context) {
        DatabaseHelper helper = new DatabaseHelper(context);
        Dao<Player, Long> d;
        try {
            d = helper.getPlayerDao();
        } catch (SQLException e) {
            throw new RuntimeException("Could not get player dao: ", e);
        }
        return d;
    }

    public static List<Player> getAll(Context context) throws SQLException {
        Dao<Player, Long> d = Player.getDao(context);
        List<Player> players = new ArrayList<>();
        for (Player p : d) {
            players.add(p);
        }
        return players;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return first_name;
    }

    public void setFirstName(String first_name) {
        this.first_name = first_name;
    }

    public String getLastName() {
        return last_name;
    }

    public void setLastName(String last_name) {
        this.last_name = last_name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
