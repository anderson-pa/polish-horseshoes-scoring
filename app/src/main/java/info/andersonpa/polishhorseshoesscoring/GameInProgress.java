package info.andersonpa.polishhorseshoesscoring;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import info.andersonpa.polishhorseshoesscoring.backend.ActiveGame;
import info.andersonpa.polishhorseshoesscoring.backend.Activity_Base;
import info.andersonpa.polishhorseshoesscoring.backend.Adapter_Inning;
import info.andersonpa.polishhorseshoesscoring.backend.Item_Inning;
import info.andersonpa.polishhorseshoesscoring.db.Throw;
import info.andersonpa.polishhorseshoesscoring.enums.DeadType;
import info.andersonpa.polishhorseshoesscoring.enums.ThrowResult;
import info.andersonpa.polishhorseshoesscoring.enums.ThrowType;

public class GameInProgress extends Activity_Base {
    private RecyclerView rv_throws;

    private View[] dead_views = new View[4];
    private ImageView iv_high;
    private ImageView iv_low;
    private ImageView iv_left;
    private ImageView iv_right;
    private ImageView iv_trap;
    private ImageView iv_short;
    private ImageView iv_strike;
    private ImageView iv_bottle;
    private ImageView iv_cup;
    private ImageView iv_pole;
    private TextView tv_own_goal;
    private TextView tv_def_err;
    private ToggleButton tb_fire;
    private View v_na_top;
    private View v_na_btm;
    NumberPicker np_result;

    public ActiveGame ag;
    Throw uiThrow;
    Adapter_Inning inning_adapter;
    List<Item_Inning> innings = new ArrayList<>();

    // LISTENERS ==============================================================
    private OnValueChangeListener resultNPChangeListener = new OnValueChangeListener() {
        public void onValueChange(NumberPicker parent, int oldVal, int newVal) {
            switch (newVal) {
                case 0:
                    uiThrow.throwResult = ThrowResult.DROP;
                    break;
                case 1:
                    uiThrow.throwResult = ThrowResult.CATCH;
                    break;
                case 2:
                    uiThrow.throwResult = ThrowResult.STALWART;
                    break;
            }
            updateActiveThrow();
        }
    };

    private View.OnClickListener onThrowClicked = new View.OnClickListener() {
        public void onClick(View view) {
            gotoThrow((int) view.getTag());
        }
    };

    private OnLongClickListener mLongClickListener = new OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            log("mLongClickListener(): " + view.getContentDescription() + " was long pressed");
            int buttonId = view.getId();

            if (Arrays.asList(ThrowType.TRAP, ThrowType.TRAP_REDEEMED)
                    .contains(uiThrow.throwType)) {
                switch (buttonId) {
                    case R.id.gip_button_pole:
                        toggleBroken();
                        ag.setThrowType(uiThrow, ThrowType.TRAP_REDEEMED);
                        break;
                    case R.id.gip_button_cup:
                        toggleBroken();
                        ag.setThrowType(uiThrow, ThrowType.TRAP_REDEEMED);
                        break;
                    case R.id.gip_button_bottle:
                        toggleBroken();
                        ag.setThrowType(uiThrow, ThrowType.TRAP_REDEEMED);
                        break;
                }
            } else {
                switch (buttonId) {
                    case R.id.gip_button_pole:
                        toggleBroken();
                        ag.setThrowType(uiThrow, ThrowType.POLE);
                        break;
                    case R.id.gip_button_cup:
                        toggleBroken();
                        ag.setThrowType(uiThrow, ThrowType.CUP);
                        break;
                    case R.id.gip_button_bottle:
                        toggleBroken();
                        ag.setThrowType(uiThrow, ThrowType.BOTTLE);
                        break;
                }
            }

            switch (buttonId) {
                case R.id.gip_button_strike:
                    ag.setIsTipped(uiThrow);
                    if (uiThrow.isTipped) {
                        iv_strike.getDrawable().setLevel(2);
                    } else {
                        iv_strike.getDrawable().setLevel(0);
                    }
                    break;
                case R.id.gip_button_high:
                    toggleDeadType(DeadType.HIGH);
                    break;
                case R.id.gip_button_right:
                    toggleDeadType(DeadType.RIGHT);
                    break;
                case R.id.gip_button_low:
                    toggleDeadType(DeadType.LOW);
                    break;
                case R.id.gip_button_left:
                    toggleDeadType(DeadType.LEFT);
                    break;
                default:
                    break;
            }
            if (buttonId == R.id.gip_button_pole || buttonId == R.id.gip_button_cup || buttonId
                    == R.id.gip_button_bottle) {
                confirmThrow();
            } else {
                updateActiveThrow();
            }
            return true;
        }
    };

    public void throwTypePressed(View view) {
        log("buttonPressed(): " + view.getContentDescription() + " was pressed");
        int buttonId = view.getId();

        if (Arrays.asList(ThrowType.TRAP, ThrowType.TRAP_REDEEMED)
                .contains(uiThrow.throwType)) {
            switch (buttonId) {
                case R.id.gip_button_trap:
                    ag.setThrowResult(uiThrow, getThrowResultFromNP());
                    ag.setThrowType(uiThrow, ThrowType.NOT_THROWN);
                    ((ImageView) view).getDrawable().setLevel(0);
                    break;
                case R.id.gip_button_bottle:
                case R.id.gip_button_pole:
                case R.id.gip_button_cup:
                    ag.setThrowType(uiThrow, ThrowType.TRAP_REDEEMED);
                    confirmThrow();
                    break;
                default:
                    ag.setThrowType(uiThrow, ThrowType.TRAP);
                    confirmThrow();
                    break;
            }
        } else {
            switch (buttonId) {
                case R.id.gip_button_high:
                    ag.setThrowType(uiThrow, ThrowType.BALL_HIGH);
                    break;
                case R.id.gip_button_low:
                    ag.setThrowType(uiThrow, ThrowType.BALL_LOW);
                    break;
                case R.id.gip_button_left:
                    ag.setThrowType(uiThrow, ThrowType.BALL_LEFT);
                    break;
                case R.id.gip_button_right:
                    ag.setThrowType(uiThrow, ThrowType.BALL_RIGHT);
                    break;
                case R.id.gip_button_trap:
                    ag.setThrowType(uiThrow, ThrowType.TRAP);
                    ((ImageView) view).getDrawable().setLevel(2);
                    break;
                case R.id.gip_button_short:
                    ag.setThrowType(uiThrow, ThrowType.SHORT);
                    break;
                case R.id.gip_button_strike:
                    ag.setThrowType(uiThrow, ThrowType.STRIKE);
                    break;
                case R.id.gip_button_bottle:
                    ag.setThrowType(uiThrow, ThrowType.BOTTLE);
                    break;
                case R.id.gip_button_pole:
                    ag.setThrowType(uiThrow, ThrowType.POLE);
                    break;
                case R.id.gip_button_cup:
                    ag.setThrowType(uiThrow, ThrowType.CUP);
                    break;
            }

            if (buttonId != R.id.gip_button_trap) {
                confirmThrow();
            }
        }
    }

    public void fireButtonPressed(View view) {
        boolean isChecked = ((ToggleButton) view).isChecked();

        if (isChecked) {
            uiThrow.offenseFireCount = 3;
            uiThrow.defenseFireCount = 0;
            if (uiThrow.throwResult != ThrowResult.BROKEN) {
                ag.setThrowResult(uiThrow, ThrowResult.NA);
            }
            if (uiThrow.throwType == ThrowType.FIRED_ON) {
                ag.setThrowType(uiThrow, ThrowType.NOT_THROWN);
            }
        } else {
            uiThrow.offenseFireCount = 0;
            ag.setThrowResult(uiThrow, getThrowResultFromNP());
        }
        log("fire checked changed");
        updateActiveThrow();
    }

    public void firedOnPressed(View view) {
        log("buttonPressed(): " + view.getContentDescription() + " was pressed");

        if (uiThrow.defenseFireCount == 0) {
            uiThrow.defenseFireCount = 3;
            uiThrow.offenseFireCount = 0;
            ag.setThrowType(uiThrow, ThrowType.FIRED_ON);
            confirmThrow();
        } else {
            uiThrow.defenseFireCount = 0;
            ag.setThrowType(uiThrow, ThrowType.NOT_THROWN);
            ag.setThrowResult(uiThrow, getThrowResultFromNP());
            updateActiveThrow();
        }
    }

    // INNER CLASSES ==========================================================
    public void OwnGoalDialog(View view) {
        final boolean[] ownGoals = uiThrow.getOwnGoals();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Own Goal").setMultiChoiceItems(R.array.owngoals, ownGoals,
                new DialogInterface.OnMultiChoiceClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            // If the user checked the item, add it to
                            // the selected items
                            ownGoals[which] = true;
                        } else {
                            ownGoals[which] = false;
                        }
                        uiThrow.setOwnGoals(ownGoals);
                        updateActiveThrow();
                    }
                }).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void PlayerErrorDialog(View view) {
        final boolean[] defErrors = uiThrow.getDefErrors();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Defensive Error").setMultiChoiceItems(R.array.defErrors, defErrors,
                new DialogInterface.OnMultiChoiceClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            // If the user checked the item, add it to
                            // the selected items
                            defErrors[which] = true;
                        } else {
                            defErrors[which] = false;
                        }
                        uiThrow.setDefErrors(defErrors);
                        updateActiveThrow();
                    }
                }).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void InfoDialog() {
        DateFormat df = new SimpleDateFormat("EEE MMM dd, yyyy. HH:mm", Locale.US);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Game #" + String.valueOf(ag.getGameId())).setPositiveButton("Close",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        LayoutInflater inflater = getLayoutInflater();

        View fView = inflater.inflate(R.layout.dialog_game_information, null);
        TextView tv;

        // players
        tv = (TextView) fView.findViewById(R.id.gInfo_p1);
        tv.setText(ag.getTeamNames()[0]);

        tv = (TextView) fView.findViewById(R.id.gInfo_p2);
        tv.setText(ag.getTeamNames()[1]);

        // // session
        tv = (TextView) fView.findViewById(R.id.gInfo_session);
        tv.setText(ag.getSessionName());

        // venue
        tv = (TextView) fView.findViewById(R.id.gInfo_venue);
        tv.setText(ag.getVenueName());

        // date
        tv = (TextView) fView.findViewById(R.id.gInfo_date);
        tv.setText(df.format(ag.getGameDate()));

        builder.setView(fView);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static class GentlemensDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Time out, Gentlemen!").setPositiveButton("Resume",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }

    // ANDROID CALLBACKS ======================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log("onCreate(): creating GIP");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_in_progress);

        rv_throws = (RecyclerView) this.findViewById(R.id.rv_throws_table);
        rv_throws.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        String gId = intent.getStringExtra("GID");
        ag = new ActiveGame(this, gId);

        uiThrow = ag.getActiveThrow();
        inning_adapter = new Adapter_Inning(this, innings, ag.getRuleSet(), onThrowClicked);
        rv_throws.setAdapter(inning_adapter);

        initMetadata();
        initListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem fav = menu.add(0, 1, 0, "Game Information");
        fav.setIcon(R.drawable.ic_action_about);
        fav.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case 1:
                InfoDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        gotoThrow(ag.getActiveIdx());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        log("Calling onStop");
        ag.saveAllThrows();
        ag.saveGame();
    }

    // INITIALIZATION =========================================================
    private void initMetadata() {
        TextView tv;

        tv = (TextView) findViewById(R.id.header_p1);
        tv.setText(ag.getTeamNames()[0]);
        tv = (TextView) findViewById(R.id.header_p2);
        tv.setText(ag.getTeamNames()[1]);
    }

    private void initListeners() {
        dead_views[0] = findViewById(R.id.gip_dead_high);
        dead_views[1] = findViewById(R.id.gip_dead_right);
        dead_views[2] = findViewById(R.id.gip_dead_low);
        dead_views[3] = findViewById(R.id.gip_dead_left);

        iv_high = (ImageView) findViewById(R.id.gip_button_high);
        iv_high.setOnLongClickListener(mLongClickListener);

        iv_left = (ImageView) findViewById(R.id.gip_button_left);
        iv_left.setOnLongClickListener(mLongClickListener);

        iv_right = (ImageView) findViewById(R.id.gip_button_right);
        iv_right.setOnLongClickListener(mLongClickListener);

        iv_low = (ImageView) findViewById(R.id.gip_button_low);
        iv_low.setOnLongClickListener(mLongClickListener);

        iv_trap = (ImageView) findViewById(R.id.gip_button_trap);
        iv_trap.setOnLongClickListener(mLongClickListener);

        iv_short = (ImageView) findViewById(R.id.gip_button_short);
        iv_short.setOnLongClickListener(mLongClickListener);

        iv_strike = (ImageView) findViewById(R.id.gip_button_strike);
        iv_strike.setOnLongClickListener(mLongClickListener);

        iv_pole = (ImageView) findViewById(R.id.gip_button_pole);
        iv_pole.setOnLongClickListener(mLongClickListener);

        iv_cup = (ImageView) findViewById(R.id.gip_button_cup);
        iv_cup.setOnLongClickListener(mLongClickListener);

        iv_bottle = (ImageView) findViewById(R.id.gip_button_bottle);
        iv_bottle.setOnLongClickListener(mLongClickListener);

        tv_own_goal = (TextView) findViewById(R.id.gip_ownGoal);
        tv_def_err = (TextView) findViewById(R.id.gip_playerError);

        tb_fire = (ToggleButton) findViewById(R.id.gip_toggle_fire);

        if (ag.usesAutoFire()) {
            tb_fire.setVisibility(View.GONE);
            Button bFiredOn = (Button) findViewById(R.id.gip_button_fired_on);
            bFiredOn.setVisibility(View.GONE);
        }

        v_na_top = findViewById(R.id.gip_top_na_ind);
        v_na_btm = findViewById(R.id.gip_btm_na_ind);

        np_result = (NumberPicker) findViewById(R.id.numPicker_catch);
        np_result.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        String[] catchText = new String[3];
        catchText[0] = getString(R.string.gip_drop);
        catchText[1] = getString(R.string.gip_catch);
        catchText[2] = getString(R.string.gip_stalwart);
        np_result.setMinValue(0);
        np_result.setMaxValue(2);
        np_result.setValue(1);
        np_result.setDisplayedValues(catchText);
        np_result.setOnValueChangedListener(resultNPChangeListener);
    }

    // STATE LOGIC AND PROGRAM FLOW ===========================================
    void updateActiveThrow() {
        log("updateThrow(): Updating throw at idx " + ag.getActiveIdx());
        ag.updateActiveThrow(uiThrow);

        refreshUI();
    }

    void confirmThrow() {
        int active_idx = ag.getActiveIdx();
        if ((active_idx + 7) % 70 == 0) {
            Toast.makeText(getApplicationContext(), "GTO in 3 innings", Toast.LENGTH_LONG).show();
        } else if ((active_idx + 1) % 70 == 0) {
            GentlemensDialogFragment frag = new GentlemensDialogFragment();
            frag.show(getFragmentManager(), "gentlemens");
        }
        gotoThrow(active_idx + 1);
        ag.updateScoresFrom(active_idx + 1);
    }

    void gotoThrow(int newActiveIdx) {
        log("gotoThrow(): Going from throw " + ag.getActiveIdx() + " to " + newActiveIdx + ".");

        ag.updateActiveThrow(uiThrow); // ui -> ag
        ag.setActiveIdx(newActiveIdx); // change index
        uiThrow = ag.getActiveThrow(); // ag -> ui

        refreshUI();

        ag.saveGame(); // save the game
    }

    // UI =====================================================================
    private void refreshUI() {
        setThrowResultToNP(uiThrow.throwResult);
        setThrowButtonState(ThrowType.BALL_HIGH, iv_high);
        setThrowButtonState(ThrowType.BALL_LOW, iv_low);
        setThrowButtonState(ThrowType.BALL_LEFT, iv_left);
        setThrowButtonState(ThrowType.BALL_RIGHT, iv_right);
        setThrowButtonState(ThrowType.TRAP, iv_trap);
        setThrowButtonState(ThrowType.SHORT, iv_short);
        setThrowButtonState(ThrowType.STRIKE, iv_strike);
        setThrowButtonState(ThrowType.BOTTLE, iv_bottle);
        setThrowButtonState(ThrowType.POLE, iv_pole);
        setThrowButtonState(ThrowType.CUP, iv_cup);
        setBrokenButtonState();
        setExtrasButtonState();

        if (uiThrow.isTipped) {
            iv_strike.getDrawable().setLevel(3);
        }

        for (View vw : dead_views) {
            vw.setBackgroundColor(Color.LTGRAY);
        }
        if (uiThrow.deadType > 0) {
            dead_views[uiThrow.deadType - 1].setBackgroundColor(Color.RED);
        }

        inning_adapter.setCurrentThrow(uiThrow);
        updateThrowTable();
    }

    private void setThrowButtonState(int throwType, ImageView iv) {
        if (throwType == uiThrow.throwType) {
            iv.getDrawable().setLevel(1);
        } else if (throwType == ThrowType.TRAP && uiThrow.throwType == ThrowType.TRAP_REDEEMED) {
            iv.getDrawable().setLevel(1);
        } else {
            iv.getDrawable().setLevel(0);
        }
    }

    private void setBrokenButtonState() {
        Drawable poleDwl = iv_pole.getDrawable();
        Drawable cupDwl = iv_cup.getDrawable();
        Drawable bottleDwl = iv_bottle.getDrawable();

        if (uiThrow.throwResult == ThrowResult.BROKEN) {
            switch (uiThrow.throwType) {
                case ThrowType.POLE:
                    poleDwl.setLevel(3);
                    cupDwl.setLevel(2);
                    bottleDwl.setLevel(2);
                    break;
                case ThrowType.CUP:
                    poleDwl.setLevel(2);
                    cupDwl.setLevel(3);
                    bottleDwl.setLevel(2);
                    break;
                case ThrowType.BOTTLE:
                    poleDwl.setLevel(2);
                    cupDwl.setLevel(2);
                    bottleDwl.setLevel(3);
                    break;
                case ThrowType.TRAP:
                case ThrowType.TRAP_REDEEMED:
                    poleDwl.setLevel(2);
                    cupDwl.setLevel(2);
                    bottleDwl.setLevel(2);
                    break;
            }
        } else {
            switch (uiThrow.throwType) {
                case ThrowType.POLE:
                    poleDwl.setLevel(1);
                    cupDwl.setLevel(0);
                    bottleDwl.setLevel(0);
                    break;
                case ThrowType.CUP:
                    poleDwl.setLevel(0);
                    cupDwl.setLevel(1);
                    bottleDwl.setLevel(0);
                    break;
                case ThrowType.BOTTLE:
                    poleDwl.setLevel(0);
                    cupDwl.setLevel(0);
                    bottleDwl.setLevel(1);
                    break;
                case ThrowType.TRAP:
                case ThrowType.TRAP_REDEEMED:
                    poleDwl.setLevel(0);
                    cupDwl.setLevel(0);
                    bottleDwl.setLevel(0);
                    break;
            }
        }
    }

    private void setExtrasButtonState() {
        tv_own_goal.setTextColor(Color.BLACK);
        tv_def_err.setTextColor(Color.BLACK);
        for (boolean og : uiThrow.getOwnGoals()) {
            if (og) {
                tv_own_goal.setTextColor(Color.RED);
            }
        }
        for (boolean de : uiThrow.getDefErrors()) {
            if (de) {
                tv_def_err.setTextColor(Color.RED);
            }
        }

        if (uiThrow.offenseFireCount >= 3) {
            tb_fire.setChecked(true);
        } else {
            tb_fire.setChecked(false);
        }
    }

    private void updateThrowTable() {
        innings.clear();
        innings.addAll(ag.getInnings());
        inning_adapter.notifyDataSetChanged();
        rv_throws.scrollToPosition(ag.getActiveIdx()/2);
    }

    public int getThrowResultFromNP() {
        int theResult = 0;
        switch (np_result.getValue()) {
            case 0:
                theResult = ThrowResult.DROP;
                break;
            case 1:
                theResult = ThrowResult.CATCH;
                break;
            case 2:
                theResult = ThrowResult.STALWART;
                break;
        }
        return theResult;
    }

    public void setThrowResultToNP(int result) {
        v_na_top.setBackgroundColor(Color.LTGRAY);
        v_na_btm.setBackgroundColor(Color.LTGRAY);
        switch (result) {
            case ThrowResult.DROP:
                np_result.setValue(0);
                break;
            case ThrowResult.CATCH:
                np_result.setValue(1);
                break;
            case ThrowResult.STALWART:
                np_result.setValue(2);
                break;
            case ThrowResult.NA:
                v_na_top.setBackgroundColor(Color.RED);
                v_na_btm.setBackgroundColor(Color.RED);
                break;
        }
    }

    public void toggleDeadType(int deadType) {
        if (uiThrow.deadType == deadType) {
            ag.setDeadType(uiThrow, DeadType.ALIVE);
        } else {
            ag.setDeadType(uiThrow, deadType);
        }
    }

    public void toggleBroken() {
        if (uiThrow.throwResult == ThrowResult.BROKEN) {
            ag.setThrowResult(uiThrow, getThrowResultFromNP());
        } else {
            ag.setThrowResult(uiThrow, ThrowResult.BROKEN);
        }
    }
}
