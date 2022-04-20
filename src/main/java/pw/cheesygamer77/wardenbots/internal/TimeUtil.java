package pw.cheesygamer77.wardenbots.internal;

import net.dv8tion.jda.api.utils.TimeFormat;
import org.jetbrains.annotations.NotNull;

import java.time.temporal.TemporalAccessor;

public final class TimeUtil {
    /**
     * Returns the {@link TimeFormat#DATE_TIME_SHORT} followed by the {@link TimeFormat#RELATIVE} parenthesized
     * of a given {@link TemporalAccessor}
     * @return The string containing the above
     */
    public static @NotNull String getTimeDescription(@NotNull TemporalAccessor accessor) {
        return TimeFormat.DATE_TIME_SHORT.format(accessor) + " (" + TimeFormat.RELATIVE.format(accessor) + ")";
    }
}
