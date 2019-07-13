package info.andersonpa.polishhorseshoesscoring.rulesets;

import android.util.Log;

import info.andersonpa.polishhorseshoesscoring.db.Throw;
import info.andersonpa.polishhorseshoesscoring.enums.DeadType;
import info.andersonpa.polishhorseshoesscoring.enums.ThrowResult;
import info.andersonpa.polishhorseshoesscoring.enums.ThrowType;

public class RuleSet01 extends RuleSet00 {
    /**
     * Standard rules with coercion and autofire.
     */

    @Override
    public int getId() {
        return 1;
    }

    @Override
    public String getDescription() {
        return "Standard ruleset with coercion and autofire.";
    }

    @Override
    public boolean useAutoFire() {
        return true;
    }

    @Override
    public void setThrowType(Throw t, int throw_type) {
        if (t.defenseFireCount >= 3) {
            t.throwType = ThrowType.FIRED_ON;
            setThrowResult(t, ThrowResult.NA);
            setDeadType(t, DeadType.ALIVE);
        } else {
            t.throwType = throw_type;

            if (t.offenseFireCount >= 3) {
                setThrowResult(t, ThrowResult.NA);
            } else {
                switch (throw_type) {
                    case ThrowType.BALL_HIGH:
                    case ThrowType.BALL_RIGHT:
                    case ThrowType.BALL_LOW:
                    case ThrowType.BALL_LEFT:
                    case ThrowType.STRIKE:
                        if (t.throwResult != ThrowResult.DROP && t.throwResult != ThrowResult
                                .CATCH) {
                            setThrowResult(t, ThrowResult.CATCH);
                        }
                        break;
                    case ThrowType.SHORT:
                        if (t.deadType == DeadType.ALIVE) {
                            setDeadType(t, DeadType.LOW);
                        }
                        setThrowResult(t, ThrowResult.NA);
                        break;
                    case ThrowType.TRAP:
                        if (t.deadType == DeadType.ALIVE) {
                            setDeadType(t, DeadType.HIGH);
                        }
                        setThrowResult(t, ThrowResult.NA);
                        break;
                    case ThrowType.TRAP_REDEEMED:
                        if (t.deadType == DeadType.ALIVE) {
                            setDeadType(t, DeadType.HIGH);
                        }
                        if (t.throwResult != ThrowResult.BROKEN) {
                            setThrowResult(t, ThrowResult.NA);
                        }
                        break;
                }
            }
        }
    }

    private boolean stokesOffensiveFire(Throw t) {
        // quench caused by own goal or throwing dead
        boolean quenches = isOffensiveError(t) || (t.deadType != DeadType.ALIVE);

        // will stoke if all conditions are met:
        // (a) not quenched, (b) hits the stack, (c) not stalwart
        boolean stokes = !quenches && isStackHit(t) && t.throwResult != ThrowResult.STALWART;

        return stokes;
    }

    private boolean quenchesDefensiveFire(Throw t) {

        boolean fireHit = isOnFire(t) && isStackHit(t);
        boolean broken = t.throwResult == ThrowResult.BROKEN;
        boolean defFail = t.throwResult == ThrowResult.DROP && (isStackHit(t) ||
                (t.throwType == ThrowType.STRIKE && t.deadType == DeadType.ALIVE));

        // defensive error will also quench
        boolean quenches = isDefensiveError(t) || fireHit || broken || defFail;
        Log.i("QuenchDefense", "throw: " + t.getThrowIdx() + ": fireHit: " + fireHit + ", " +
                "broken: " + broken + ", defFail: " + defFail + ", quenches: " + quenches);
        return quenches;
    }

    @Override
    public void setFireCounts(Throw t, Throw previous_throw) {
        int prevOffCount = previous_throw.offenseFireCount;
        int prevDefCount = previous_throw.defenseFireCount;

        if (stokesOffensiveFire(previous_throw)) {
            prevOffCount++;
        } else {
            prevOffCount = 0;
        }
        if (quenchesDefensiveFire(previous_throw)) {
            prevDefCount = 0;
        }

        t.offenseFireCount = prevDefCount;
        t.defenseFireCount = prevOffCount;

        Log.i("Throw.setFireCounts()", "o=" + prevDefCount + ", d=" + prevOffCount);
    }
}