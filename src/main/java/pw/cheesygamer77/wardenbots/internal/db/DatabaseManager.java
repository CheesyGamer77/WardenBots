package pw.cheesygamer77.wardenbots.internal.db;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pw.cheesygamer77.wardenbots.core.moderation.ModlogChannel;
import pw.cheesygamer77.wardenbots.internal.Hasher;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

/**
 * Misc utility functions used to make queries to the database
 */
public final class DatabaseManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseManager.class);
    private static final String JDBC_URL = "jdbc:sqlite:warden.db";

    /**
     * Returns the JDBC URL for the database
     * @return The url
     */
    public static String getURL() {
        return JDBC_URL;
    }

    private static void logQueryError(Throwable error) {
        LOGGER.error("Encountered an unexpected error while executing query: ", error);
    }

    /**
     * Fetches the mod log channel configuration for a particular {@link Guild}.
     *
     * This fetches the entire configuration. To fetch an exact {@link TextChannel} mod log channel,
     * use {@link DatabaseManager#fetchModLogChannel(Guild, ModlogChannel)} instead.
     *
     * If an error occurs or there is no configuration for the guild, an empty mapping will be returned.
     * @param guild The guild to fetch the mod log channels for
     * @return A Hash Map of {@link ModlogChannel} and Long pairs. Each Long corresponds to the stored
     * channel ID for the particular ModlogChannel.
     */
    public static @NotNull HashMap<ModlogChannel, Long> fetchAllModLogChannels(@NotNull Guild guild) {
        HashMap<ModlogChannel, Long> out = new HashMap<>();

        try (Connection connection = DriverManager.getConnection(getURL())) {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM " + Table.MODLOG_CHANNELS + " WHERE Guild = ?"
            );

            statement.setString(1, Hasher.hashify(guild.getId()));

            ResultSet rs = statement.executeQuery();

            if(!rs.isClosed())
                // add each entry to the output map
                for (ModlogChannel channel : ModlogChannel.values())
                    out.put(channel, rs.getLong(channel.getDatabaseColumnName()));
            else
                LOGGER.warn(
                        "Got a closed ResultSet while fetching Mod Log Channel configuration for guild "
                                + guild.getId() + "! Is the guild registered?"
                );
        }
        catch (Exception error) {
            logQueryError(error);
        }

        return out;
    }

    /**
     * Fetches a {@link TextChannel} designated as a particular {@link ModlogChannel} for a {@link Guild}
     * @param guild The guild to fetch the channel from
     * @param channel The channel type to fetch
     * @return The {@link TextChannel} used for a particular {@link ModlogChannel}, or null if it doesn't exist.
     */
    public static @Nullable TextChannel fetchModLogChannel(@NotNull Guild guild, @NotNull ModlogChannel channel) {
        HashMap<ModlogChannel, Long> channelIDMapping = fetchAllModLogChannels(guild);
        LOGGER.debug("Mod Log Channel configuration for guild " + guild.getId() + " is: " + channelIDMapping);

        Long channelID = channelIDMapping.getOrDefault(channel, null);
        if(channelID != null) {
            TextChannel out = guild.getTextChannelById(channelID);
            if(out != null && out.canTalk())
                return out;
        }

        return null;
    }
}
