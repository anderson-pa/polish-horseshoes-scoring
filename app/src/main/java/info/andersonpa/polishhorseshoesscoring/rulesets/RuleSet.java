package info.andersonpa.polishhorseshoesscoring.rulesets;

import android.content.Context;
import android.widget.ImageView;

import info.andersonpa.polishhorseshoesscoring.db.Throw;

public interface RuleSet {
    int getId();

    void setContext(Context context);

    String getDescription();

    boolean useAutoFire();

    // Primary setting functions ==============================================
    void setThrowType(Throw t, int throw_type);

    void setThrowResult(Throw t, int throw_result);

    void setDeadType(Throw t, int dead_type);

    void setIsTipped(Throw t, boolean is_tipped);

    void setOwnGoals(Throw t, boolean[] own_goals);

    void setDefErrors(Throw t, boolean[] def_errors);

    // Scores and UI ==========================================================
    int[] getScoreDifferentials(Throw t);

    int[] getHitPointDifferentials(Throw t);

    int[] getFinalScores(Throw t);

    int[] getFinalHitPoints(Throw t);

    String getSpecialString(Throw t);

    void setThrowDrawable(Throw t, ImageView iv);

    // Special Rules ==========================================================
    boolean isOffenseOnHill(Throw t);

    boolean isFiredOn(Throw t);

    boolean isOnFire(Throw t);

    void setFireCounts(Throw t, Throw previous_throw);

    // Validation =============================================================
    boolean isValid(Throw t, Context context);

    boolean isValid(Throw t);

    // Convenience Definitions ================================================
    boolean isStackHit(Throw t);

    boolean isOffensiveError(Throw t);

    boolean isDefensiveError(Throw t);
}