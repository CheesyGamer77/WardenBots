package pw.cheesygamer77.wardenbots.internal.db;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pw.cheesygamer77.wardenbots.internal.db.statements.BuildableSQLStatement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Function;

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

    /**
     * Maps a given ResultSet to a given resolver function
     * @param rs The input result set
     * @param resolver The function used to resolve the ResultSet
     * @param <T> The type returned by the resolver
     * @return The resolved ResultSet, or null if the ResultSet is closed
     * @throws SQLException If something got broke
     */
    private static <T> @Nullable T mapResultOrNull(@NotNull ResultSet rs, @NotNull Function<ResultSet, T> resolver) throws SQLException {
        if(!rs.isClosed())
            return resolver.apply(rs);

        LOGGER.warn("Attempted to resolve a closed ResultSet!");
        return null;
    }

    /**
     * Retrieves one ResultSet from the database, given a particular {@link BuildableSQLStatement}.
     * @param statement The statement to execute as the query
     * @param <T> The data type returned by the statement's {@link ResultSet} resolver
     * @return The resolved {@link ResultSet}, or null if it could not be resolved
     */
    public static <T> @Nullable T retrieveOne(@NotNull BuildableSQLStatement<T> statement) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL)) {
            ResultSet result = statement.getStatementBuilder().build(connection).executeQuery();
            return mapResultOrNull(result, statement.getResultSetMapper());
        }
        catch (SQLException error) {
            LOGGER.error("Encountered an SQLiteException: " + ExceptionUtils.getStackTrace(error));
        }
        catch (Exception error) {
            LOGGER.error("Encountered an unknown error: " + ExceptionUtils.getStackTrace(error));
        }

        return null;
    }
}
