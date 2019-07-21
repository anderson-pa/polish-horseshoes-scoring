package info.andersonpa.polishhorseshoesscoring;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import info.andersonpa.polishhorseshoesscoring.backend.Activity_Base;
import info.andersonpa.polishhorseshoesscoring.db.Game;
import info.andersonpa.polishhorseshoesscoring.db.Player;
import info.andersonpa.polishhorseshoesscoring.enums.RuleType;
import info.andersonpa.polishhorseshoesscoring.rulesets.RuleSet;

public class CreateNewGame extends Activity_Base {
    Spinner spinner_p1;
    Spinner spinner_p2;
    Spinner spinner_session;
    Spinner spinner_venue;
    Spinner spinner_ruleSet;

    Dao<Player, Long> pDao;
    int p1_pos = 0;
    int p2_pos = 1;
    int session_pos = 0;
    int venue_pos = 0;
    int ruleSet_pos = 0;

    long p1Id;
    long p2Id;

    List<Player> players = new ArrayList<>();

    List<String> player_names = new ArrayList<>();
    List<String> session_names = new ArrayList<>();
    List<String> venue_names = new ArrayList<>();
    List<String> ruleset_descriptions = new ArrayList<>();
    List<Integer> ruleset_ids = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_game);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pDao = Player.getDao(this);

        spinner_p1 = this.findViewById(R.id.spinner_player1);
        spinner_p2 = this.findViewById(R.id.spinner_player2);
        spinner_session = this.findViewById(R.id.spinner_session);
        spinner_venue = this.findViewById(R.id.spinner_venue);
        spinner_ruleSet = this.findViewById(R.id.spinner_ruleSet);

        refreshSpinners();

        spinner_p1.setOnItemSelectedListener(mPlayerOneSelectedHandler);
        spinner_p2.setOnItemSelectedListener(mPlayerTwoSelectedHandler);
        spinner_session.setOnItemSelectedListener(mSessionSelectedHandler);
        spinner_venue.setOnItemSelectedListener(mVenueSelectedHandler);
        spinner_ruleSet.setOnItemSelectedListener(mRuleSetSelectedHandler);

        spinner_p2.setSelection(1);
        if (p1Id != -1 && p2Id != -1) {
            if (p1Id > p2Id) {
                swapPlayers();
            }
        }

    }


    private AdapterView.OnItemSelectedListener mPlayerOneSelectedHandler = new AdapterView.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View v, int position,
                                   long id) {
            p1_pos = position;
        }

        public void onNothingSelected(AdapterView<?> parent) {
        }

    };
    private AdapterView.OnItemSelectedListener mPlayerTwoSelectedHandler = new AdapterView.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View v, int position,
                                   long id) {
            p2_pos = position;
        }

        public void onNothingSelected(AdapterView<?> parent) {
        }

    };
    private AdapterView.OnItemSelectedListener mSessionSelectedHandler = new AdapterView.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View v, int position,
                                   long id) {
            session_pos = position;
            // if (sessions.get(position).getDescriptor() == -1) {
            // spinner_ruleSet.setEnabled(true);
            // } else {
            // spinner_ruleSet.setEnabled(false);
            // int selectedId = sessions.get(position).getDescriptor();
            // spinner_ruleSet.setSelection(ruleset_ids.indexOf(selectedId));
            // ruleSet_pos = ruleset_ids.indexOf(selectedId);
            // }
        }

        public void onNothingSelected(AdapterView<?> parent) {
        }
    };
    private AdapterView.OnItemSelectedListener mVenueSelectedHandler = new AdapterView.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View v, int position,
                                   long id) {
            venue_pos = position;
        }

        public void onNothingSelected(AdapterView<?> parent) {
        }
    };
    private AdapterView.OnItemSelectedListener mRuleSetSelectedHandler = new AdapterView.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View v, int position,
                                   long id) {
            ruleSet_pos = position;
        }

        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    public void refreshSpinners() {
        try {
            players = Player.getAll(this);
        } catch (SQLException e) {
            loge("Could not get players.", e);
            Snackbar.make(spinner_p1, "Could not get players", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
        player_names.clear();
        for (Player p : players) {
            player_names.add(p.getFirstName() + " " + p.getLastName());
        }

        session_names.add("Summer 2019 League");

        venue_names.add("Putnam");

        ruleset_descriptions.clear();
        ruleset_ids.clear();
        for (RuleSet rs : RuleType.map.values()) {
            ruleset_descriptions.add(rs.getDescription());
            ruleset_ids.add(rs.getId());
        }

        ArrayAdapter<String> pAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, player_names);
        pAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> sAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, session_names);
        sAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> vAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, venue_names);
        vAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> rsAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, ruleset_descriptions);
        rsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_p1.setAdapter(pAdapter);
        spinner_p2.setAdapter(pAdapter);

        spinner_session.setAdapter(sAdapter);
        spinner_venue.setAdapter(vAdapter);
        spinner_ruleSet.setAdapter(rsAdapter);
    }

    public void swapPlayers(View vw) {
        swapPlayers();
    }

    public void swapPlayers() {
        int p1 = spinner_p1.getSelectedItemPosition();
        spinner_p1.setSelection(spinner_p2.getSelectedItemPosition());
        spinner_p2.setSelection(p1);
    }

    public void createGame(View view) {
        Player p1 = players.get(p1_pos);
        Player p2 = players.get(p2_pos);

        int ruleSetId = ruleset_ids.get(ruleSet_pos);

        Game g = new Game(p1.getId(), p2.getId(), ruleSetId, new Date());
        long gid;

        try {
            Dao<Game, Long> d = Game.getDao(this);
            d.createIfNotExists(g);
            gid = g.getId();
            Intent intent = new Intent(this, GameInProgress.class);
            intent.putExtra("GID", gid);
            startActivity(intent);
            finish();

        } catch (SQLException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

}
