package pw.cheesygamer77.wardenbots.internal.db;

/**
 * Misc utility functions used to make queries to the database
 */
public final class DatabaseManager {
    private static final String JDBC_URL = "jdbc:sqlite:warden.db";

    /**
     * Returns the JDBC URL for the database
     * @return The url
     */
    public static String getURL() {
        return JDBC_URL;
    }
}
