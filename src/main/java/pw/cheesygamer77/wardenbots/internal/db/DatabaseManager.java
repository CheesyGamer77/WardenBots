package pw.cheesygamer77.wardenbots.internal.db;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.UnavailableGuildJoinedEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pw.cheesygamer77.wardenbots.core.UserType;
import pw.cheesygamer77.wardenbots.core.moderation.ModLogEvent;
import pw.cheesygamer77.wardenbots.core.moderation.UserReputation;
import pw.cheesygamer77.wardenbots.internal.Hasher;
import pw.cheesygamer77.wardenbots.internal.db.internal.GuildLogConfiguration;

import java.sql.*;
import java.util.HashMap;

/**
 * Misc utility functions used to make queries to the database
 * <br>NOTE: Access to cached data should be done through the methods provided by {@link CachedResources}
 */
public final class DatabaseManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseManager.class);
    private static final String JDBC_URL = "jdbc:sqlite:warden.db";

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL);
    }

    private static void logQueryError(Throwable error) {
        LOGGER.error("Encountered an unexpected error while executing query: ", error);
    }

    /**
     * Fetches the {@link pw.cheesygamer77.wardenbots.internal.db.internal.GuildLogConfiguration} for a particular
     * {@link Guild}.
     * <br>This fetches the entire configuration. To fetch an exact {@link TextChannel} mod log channel,
     * use {@link CachedResources#getLogChannel(ModLogEvent, Guild)} instead.
     *
     * If an error occurs or there is no configuration for the guild, an empty mapping will be returned.
     * @param guild The guild to fetch the log configuration for
     * @return The configuration object
     * @see DatabaseManager#setModLogChannel(ModLogEvent, TextChannel)
     */
    public static @NotNull GuildLogConfiguration fetchLogChannelConfiguration(@NotNull Guild guild) {
        HashMap<ModLogEvent, Long> out = new HashMap<>();

        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM " + Table.MODLOG_CHANNELS + " WHERE Guild = ?"
            );

            statement.setString(1, Hasher.hashify(guild.getId()));

            ResultSet rs = statement.executeQuery();

            if(!rs.isClosed())
                // add each entry to the output map
                // longs are (very annoyingly) returned as 0 when set to null (or just not set at all..)
                for (ModLogEvent channel : ModLogEvent.values()) {
                    long id = rs.getLong(channel.getDatabaseColumnName());
                    if(id != 0)
                        out.put(channel, id);
                }
            else
                LOGGER.warn(
                        "Got a closed ResultSet while fetching Mod Log Channel configuration for guild "
                                + guild.getId() + "! Is the guild registered?"
                );
        }
        catch (Exception error) {
            logQueryError(error);
        }

        return new GuildLogConfiguration(out);
    }

    /**
     * Sets a {@link TextChannel} to be used as a particular {@link Guild}'s {@link ModLogEvent}
     * @param type The {@link ModLogEvent} type to set the channel to
     * @param channel The {@link TextChannel} to set as the log channel
     * @return Whether the channel was set successfully or not
     * @see DatabaseManager#fetchLogChannelConfiguration(Guild)
     */
    public static boolean setModLogChannel(@NotNull ModLogEvent type, @NotNull TextChannel channel) {
        String guildHash = Hasher.hashify(channel.getGuild().getId());
        String columnName = type.getDatabaseColumnName();

        try(Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO " + Table.MODLOG_CHANNELS + "(Guild, " + columnName + ") VALUES (?, ?) " +
                            "ON CONFLICT(Guild) DO UPDATE SET " + columnName + " = ?"
            );

            statement.setString(1, guildHash);
            statement.setLong(2, channel.getIdLong());
            statement.setLong(3, channel.getIdLong());

            statement.executeUpdate();
            return true;
        }
        catch (SQLException error) {
            logQueryError(error);
        }

        return false;
    }

    /**
     * Runs all the necessary queries to register a new {@link Guild}
     * into Warden's database, given the guild's ID. Attempts to register an already registered guild
     * will be ignored silently.
     * @param guildID The ID of the guild to register
     */
    private static void registerNewGuild(@NotNull Long guildID) {
        String guildHash = Hasher.hashify(guildID);

        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT OR IGNORE INTO " + Table.MODLOG_CHANNELS + "(Guild) VALUES (?)"
            );

            statement.setString(1, guildHash);
            statement.executeUpdate();

            LOGGER.debug("Registered new guild: " + guildID);
        }
        catch (Exception error) {
            logQueryError(error);
        }
    }

    /**
     * Runs all the necessary queries to register a new {@link Guild} into Warden's database.
     * Attempts to register an already registered guild will be ignored silently.
     * @param guild The guild to register
     */
    public static void registerNewGuild(@NotNull Guild guild) {
        registerNewGuild(guild.getIdLong());
    }

    /**
     * Runs all the necessary queries to register an unavailable {@link Guild} into
     * Warden's database. Attempts to register an already registered guild will be ignored silently.
     * @param event The {@link UnavailableGuildJoinedEvent} containing the unavailable guild's ID
     */
    public static void registerNewGuild(@NotNull UnavailableGuildJoinedEvent event) {
        registerNewGuild(event.getGuildIdLong());
    }

    /**
     * Sets the {@link UserReputation} for a particular {@link Member}.
     * If a reputation entry already exists, the existing value is overridden.
     * @param member The member to set the reputation for
     * @param value The value to set
     * @see UserReputation.Level
     */
    public static void setUserReputation(@NotNull Member member, double value) {
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO " + Table.USER_REPUTATION + "(User, Guild, ReputationLevel) VALUES (?, ?, ?) " +
                            "ON CONFLICT(User, Guild) DO UPDATE SET ReputationLevel = ?"
            );

            statement.setString(1, Hasher.hashify(member.getIdLong()));
            statement.setString(2, Hasher.hashify(member.getGuild().getIdLong()));
            statement.setDouble(3, value);
            statement.setDouble(4, value);

            statement.executeUpdate();
        }
        catch (Exception error) {
            logQueryError(error);
        }
    }

    /**
     * Fetches the {@link UserReputation} for a particular {@link Member}.
     *
     * If the user has no entry in the database, the user is set to a default reputation level.
     * If an error occurred while fetching, a default reputation level is returned, though unlike
     * missing entries, the default reputation level is not set in the database itself.
     * @param member The user to fetch the reputation for
     * @return The user's reputation
     */
    public static @NotNull UserReputation fetchUserReputation(@NotNull Member member) {
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM " + Table.USER_REPUTATION + " WHERE User = ? AND Guild = ?"
            );

            statement.setString(1, Hasher.hashify(member.getIdLong()));
            statement.setString(2, Hasher.hashify(member.getGuild().getIdLong()));

            ResultSet rs = statement.executeQuery();

            if(!rs.isClosed())
                return new UserReputation(rs.getDouble("ReputationLevel"));

            // insert new reputation entry
            setUserReputation(member, 0d);
            return new UserReputation(0d);
        }
        catch (SQLException error) {
            logQueryError(error);
        }

        return new UserReputation(0d);
    }

    public static @NotNull UserType fetchUserType(@NotNull User user) {
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM " + Table.GLOBAL_USER_DATA + " WHERE User = ? LIMIT 1"
            );

            statement.setString(1, Hasher.hashify(user.getId()));

            ResultSet rs = statement.executeQuery();

            if(!rs.isClosed())
                return UserType.valueOf(rs.getString("UserType"));
        } catch (SQLException error) {
            logQueryError(error);
        }

        return UserType.UNKNOWN;
    }
}
