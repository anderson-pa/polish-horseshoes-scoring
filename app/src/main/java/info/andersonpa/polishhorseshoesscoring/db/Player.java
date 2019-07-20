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
    public static final String NAME = "name";
    public static final String NICKNAME = "nickname";

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(canBeNull = false)
    private String name;

    @DatabaseField(canBeNull = true)
    private String nickname;


    public Player() {
    }

    public Player(String name, String nickname) {
        this.name = name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
