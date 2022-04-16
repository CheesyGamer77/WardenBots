package pw.cheesygamer77.wardenbots.core;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import pw.cheesygamer77.wardenbots.internal.db.DatabaseManager;

/**
 * Enum class representing a variety of user types.
 *
 * The types are as follows:
 * <ul>
 *     <li>
 *         <code>SUPER_USER</code>: Users that should be exempt from content filtering in a server. There are three types:
 *         <ul>
 *             <li>
 *                 Permission-Implied: Some users may be designated as Super Users when their server-wide
 *                 permissions deem them to be worthy of this privilege. Required permissions are:
 *                 <ul>
 *                     <li>Manage Messages</li>
 *                     <li>Timeout Members</li>
 *                     <li>Kick Users</li>
 *                     <li>Ban Users</li>
 *                 </ul>
 *                 A user having Administrator permissions is also automatically deemed as a Super User.
 *             </li>
 *             <li>
 *                 Manual: Some users can be manually deemed as super users within the database for a server.
 *                 Each guild may have up to 10 manually defined Super Users and an unlimited number of
 *                 Permission-Implied Super Users.
 *             </li>
 *             <li>
 *                 Global: Global Super Users are exempt from content filtering in any server Warden is in. This is
 *                 reserved for Warden Project Management.
 *             </li>
 *         </ul>
 *     </li>
 *     <li><code>NORMAL</code>: The default user type. These users are not Super Users.</li>
 *     <li><code>WARDEN_DEVELOPER</code>: Warden Project Management. These users are essentially a global version of {@link UserType#SUPER_USER}.</li>
 * </ul>
 */
public enum UserType {
    WARDEN_DEVELOPER(true),
    SUPER_USER(true, true),
    NORMAL(false),
    UNKNOWN(false);

    final boolean isSuperUser;
    final boolean isGuildSpecific;

    UserType(boolean isSuperUser) {
        this(isSuperUser, false);
    }

    UserType(boolean isSuperUser, boolean isGuildSpecific) {
        this.isSuperUser = isSuperUser;
        this.isGuildSpecific = isGuildSpecific;
    }

    public boolean isSuperUser() {
        return isSuperUser;
    }

    public boolean isGuildSpecific() { return isGuildSpecific; }

    /**
     * Fetches a user's UserType from the database
     * @param user The user to retrieve the UserType of
     * @return The type
     */
    public static @NotNull UserType fetch(@NotNull User user) {
        return DatabaseManager.fetchUserType(user);
    }

    public static @NotNull UserType fetch(@NotNull Member member) {
        return fetch(member.getUser());
    }
}
