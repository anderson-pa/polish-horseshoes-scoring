package info.andersonpa.polishhorseshoesscoring.throwstats;

import info.andersonpa.polishhorseshoesscoring.db.Throw;

import java.util.Set;

/**
 * This class provides one method, which maps a throw to an string.
 * Should be used to e.g. count whether a throw meets some certain criterion,
 * like being either a bottle, pole, or cup hit.
 */
public interface ThrowIndicator {

    String indicate(Throw t);

    Set<String> enumerate();

}
