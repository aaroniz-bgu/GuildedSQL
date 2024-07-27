package github.aaroniz.api;

import java.util.List;

/**
 * Since this hasn't meant to be a serious database (atm).
 * If you'd like to filter results you should specify this using this interface.
 */
@FunctionalInterface
public interface GuildedFilter {
    boolean filter(String serializedData);
}
