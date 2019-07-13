package info.andersonpa.polishhorseshoesscoring.rulesets;

import info.andersonpa.polishhorseshoesscoring.db.Throw;
import info.andersonpa.polishhorseshoesscoring.enums.ThrowType;

public class RuleSet02 extends RuleSet01 {
    @Override
    public int getId() {
        return 2;
    }

    @Override
    public String getDescription() {
        return "Bottle hit scores 1 like drop; stalwart can't win.";
    }

    private boolean isLegalBottleHit(Throw t) {
        boolean isHit = (t.throwType == ThrowType.BOTTLE &&
                !t.isLineFault &&
                !t.isGoaltend &&
                !isOffenseOnHill(t));
        return isHit;
    }

    @Override
    protected void handleCatch(Throw t, int[] diffs) {
        super.handleCatch(t, diffs);
        if (isLegalBottleHit(t)) {
            diffs[0] = 1;
        }
    }

    @Override
    protected void handleStalwart(Throw t, int[] diffs) {
        super.handleStalwart(t, diffs);
        if (isDefenseOnHill(t)) {
            diffs[1] = 0;
        }
        if (isLegalBottleHit(t)) {
            diffs[0] = 1;
        }
    }
}
