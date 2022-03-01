package pw.cheesygamer77.wardenbots.internal.db.statements;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Function;

/**
 * Interface for building SQL Statements
 * @param <T> The type returned by the statement's ResultSet resolver function
 */
public interface BuildableSQLStatement<T> {
    Logger LOGGER = LoggerFactory.getLogger(BuildableSQLStatement.class);

    /**
     * Returns the raw SQL associated with this statement
     * @return The SQL
     */
    @NotNull String getSQL();

    /**
     * Returns the {@link PreparedStatementBuilder} used to build the {@link PreparedStatement}
     * used on an SQL query
     * @return The statement builder
     */
    @NotNull PreparedStatementBuilder getStatementBuilder();

    /**
     * Returns the function used to map a result set returned from a query
     * to a useful datatype
     * @return The function
     */
    @NotNull Function<ResultSet, T> getResultSetMapper();

    /**
     * Executes this statement as an SQL query
     * @param connection The {@link Connection} to execute this query on
     * @return The resulting {@link ResultSet}
     * @throws SQLException If executing the query failed
     */
    default ResultSet executeQuery(@NotNull Connection connection) throws SQLException {
        return getStatementBuilder().build(connection).executeQuery();
    }

    /**
     * Executes this statement as an SQL Query, then maps the resulting {@link ResultSet}
     * to the appropriate datatype using {@link BuildableSQLStatement#getResultSetMapper()}
     * @param connection The {@link Connection} to execute this query on
     * @return The result {@link T} if the ResultSet was mapped successfully, null otherwise
     */
    default @Nullable T executeQueryThenMap(@NotNull Connection connection) {
        try {
            return getResultSetMapper().apply(executeQuery(connection));
        }
        catch (SQLException error) {
            LOGGER.error("Encountered an unexpected SQLException while executing+mapping query result: ", error);
            return null;
        }
    }
}
