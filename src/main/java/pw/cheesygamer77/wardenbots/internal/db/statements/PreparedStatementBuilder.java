package pw.cheesygamer77.wardenbots.internal.db.statements;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Defines a factory class for a {@link PreparedStatement}.
 *
 * Oftentimes, we want to define the structure of a PreparedStatement without
 * needing to obtain a {@link Connection}, hence why this exists.
 *
 * The above is accomplished by overriding the {@link PreparedStatementBuilder#prepare(PreparedStatement)} method
 * and setting the statement's arguments accordingly.
 */
public class PreparedStatementBuilder {
    private final @NotNull String sql;

    public PreparedStatementBuilder(final @NotNull String sql) {
        this.sql = sql;
    }

    /**
     * Sets the arguments of the {@link PreparedStatement} to be used in the SQL query. This method should be
     * overridden.
     * @param preparedStatement The statement built by an external connection
     * @throws SQLException If setting the arguments failed
     */
    protected void prepare(final PreparedStatement preparedStatement) throws SQLException {}

    /**
     * Builds the {@link PreparedStatement} given a connection
     * @param connection The connection to build the statement on
     * @return The built statement
     * @throws SQLException If building the statement failed
     */
    public PreparedStatement build(final @NotNull Connection connection) throws SQLException {
        final PreparedStatement statement = connection.prepareStatement(sql);

        prepare(statement);
        return statement;
    }
}
