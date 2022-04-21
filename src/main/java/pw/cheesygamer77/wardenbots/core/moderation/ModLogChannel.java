package pw.cheesygamer77.wardenbots.core.moderation;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pw.cheesygamer77.wardenbots.internal.db.DatabaseManager;

import java.util.Locale;

/**
 * Enum class representing a particular mod log channel type.
 *
 * NOTE: Each value directly corresponds to the name of a column within the
 */
public enum ModLogChannel {
    MOD_ACTIONS,
    JOINS,
    LEAVES,
    TIMEOUTS,
    KICKS,
    BANS,
    MESSAGE_EDITS,
    MESSAGE_DELETES,
    TEXT_FILTER,
    LINK_FILTER,
    ESCALATIONS,
    COMMAND_INVOKES,
    RAIDS,
    USER_CHANGES,
    VOICE_EVENTS,
    THREAD_EVENTS;

    /**
     * Returns the name of the mod log channel type in title case form
     * @return The title case form of the type
     */
    public @NotNull String getTitle() {
        return WordUtils.capitalizeFully(name().toLowerCase(Locale.ROOT).replace("_", " "));
    }

    /**
     * Returns the name of the mod log channel type's corresponding database column
     * @return The database column name of the type
     */
    public @NotNull String getDatabaseColumnName() {
        return getTitle().replace(" ", "") + "ChannelID";
    }

    /**
     * Returns the {@link TextChannel} associated with this mod log channel type
     * @param guild The guild to retrieve the mod log channel for
     * @return The channel corresponding for this type if successfully found, null otherwise
     */
    public @Nullable TextChannel fetchLogChannel(@NotNull Guild guild) {
        return DatabaseManager.fetchModLogChannel(guild, this);
    }
}
