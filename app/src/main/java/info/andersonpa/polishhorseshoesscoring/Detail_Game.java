package info.andersonpa.polishhorseshoesscoring;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import info.andersonpa.polishhorseshoesscoring.backend.Activity_Base;
import info.andersonpa.polishhorseshoesscoring.db.Game;
import info.andersonpa.polishhorseshoesscoring.db.Throw;
import info.andersonpa.polishhorseshoesscoring.enums.RuleType;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Detail_Game extends Activity_Base {
    Long gId;
    Game g;
    long[] p = new long[2];
    Dao<Game, Long> gDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_game);

        Intent intent = getIntent();
        gId = intent.getLongExtra("GID", -1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem fav = menu.add(R.string.menu_modify);
        fav.setIcon(R.drawable.ic_action_edit);
        fav.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        Intent intent = new Intent(this, GameInProgress.class);
        intent.putExtra("GID", gId);

        fav.setIntent(intent);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        refreshDetails();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshDetails();
    }

    public void refreshDetails() {
        if (gId != -1) {
            try {
                Context context = getApplicationContext();
                gDao = Game.getDao(context);

                g = gDao.queryForId(gId);
                //                playerDao.refresh(g.getMember1Id());
                //                playerDao.refresh(g.getMember2Id());
                //
                //                sessionDao.refresh(g.getSession());
                //                venueDao.refresh(g.getVenue());

                p[0] = g.getMember1Id();
                p[1] = g.getMember2Id();
            } catch (SQLException e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        //        TextView gameP1 = (TextView) findViewById(R.id.gDet_p1);
        //        gameP1.setText(p[0].getNickName());
        //
        //        TextView gameP2 = (TextView) findViewById(R.id.gDet_p2);
        //        gameP2.setText(p[1].getNickName());

        TextView gameId = (TextView) findViewById(R.id.gDet_id);
        gameId.setText(String.valueOf(g.getId()));

        //        TextView gameSession = (TextView) findViewById(R.id.gDet_session);
        //        gameSession.setText(g.getSession().getSessionName());
        //
        //        TextView gameVenue = (TextView) findViewById(R.id.gDet_venue);
        //        gameVenue.setText(g.getVenue().getName());

        TextView gameRuleSet = (TextView) findViewById(R.id.gDet_ruleSet);
        gameRuleSet.setText("(" + RuleType.map.get(g.ruleset_id).getId() + ") " + RuleType.map
                .get(g.ruleset_id).getDescription());

        TextView gameScore = (TextView) findViewById(R.id.gDet_score);
        gameScore.setText(String.valueOf(g.getMember1Score()) + "/" + String.valueOf(g
                .getMember2Score()));

        DateFormat df = new SimpleDateFormat("EEE MMM dd, yyyy @HH:mm", Locale.US);
        TextView gameDate = (TextView) findViewById(R.id.gDet_date);
        gameDate.setText(df.format(g.getDatePlayed()));
    }

    public void deleteGame(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
        alertDialogBuilder.setTitle("Delete this game?");
        alertDialogBuilder.setMessage("This action can not be undone.").setPositiveButton
                ("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                try {
                    Dao<Throw, Long> tDao = Throw.getDao(getApplicationContext());
                    DeleteBuilder<Throw, Long> tdb = tDao.deleteBuilder();
                    tdb.where().eq(Throw.GAME_ID, g.getId());
                    tDao.delete(tdb.prepare());

                    gDao.deleteById(g.getId());
                    finish();
                } catch (SQLException e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

}
