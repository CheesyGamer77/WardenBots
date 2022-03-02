package pw.cheesygamer77.wardenbots.core.moderation;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import pw.cheesygamer77.wardenbots.internal.db.DatabaseManager;

import java.awt.*;
import java.io.Serializable;

/**
 * Represents a User's Reputation Level
 *
 * Fetching a reputation level can be done via {@link UserReputation}
 */
public class UserReputation implements Serializable {
    public enum Level {
        DANGEROUS(Color.RED.darker()),
        RESTRICTED(Color.RED.brighter()),
        AT_RISK(Color.ORANGE),
        QUESTIONABLE(Color.YELLOW),
        DEFAULT(Color.GRAY),
        SLIGHTLY_TRUSTED(Color.CYAN.darker()),
        LOW_RISK(Color.CYAN.brighter()),
        TRUSTED(Color.GREEN),
        VERY_TRUSTED(Color.GREEN.brighter());

        final Color color;

        Level(@NotNull Color color) {
            this.color = color;
        }

        public @NotNull Color getColor() {
            return color;
        }

        public static Level fromValue(double value) {
            if(value <= -4)
                return Level.DANGEROUS;
            else if(value > -4 && value <= -3)
                return Level.RESTRICTED;
            else if(value > -3 && value <= -2)
                return Level.AT_RISK;
            else if(value > -2 && value <= -1)
                return Level.QUESTIONABLE;
            else if(value > -1 && value <= 1)
                return Level.DEFAULT;
            else if(value > 1 && value <= 2)
                return Level.SLIGHTLY_TRUSTED;
            else if(value > 2 && value <= 3)
                return Level.LOW_RISK;
            else if(value > 3 && value <= 4)
                return Level.TRUSTED;
            else
                return Level.VERY_TRUSTED;
        }
    }

    private final double value;

    public UserReputation(double value) {
        this.value = value;
    }

    /**
     * Fetches a UserReputation object for a given {@link Member}
     * @param member The member to retrieve the reputation for
     * @return The reputation object if retrieved successfully, null otherwise
     */
    public static @NotNull UserReputation fetch(@NotNull Member member) {
        return DatabaseManager.fetchUserReputation(member);
    }

    /**
     * Creates a {@link MessageEmbed} based off of a {@link Member}'s reputation
     * @param member The member to show the reputation of
     * @return The built embed
     */
    public MessageEmbed getEmbed(@NotNull Member member) {
        Level level = Level.fromValue(value);

        return new EmbedBuilder()
                .setTitle("User Reputation")
                .setDescription(member.getAsMention() + " has the following reputation data")
                .setColor(level.getColor())
                .addField(getEmbedField(member))
                .build();
    }

    /**
     * Creates a {@link MessageEmbed.Field} containing a representation
     * @param member The member used to get the embed field of
     * @return The built embed field
     */
    public MessageEmbed.Field getEmbedField(@NotNull Member member) {
        Level level = Level.fromValue(value);

        return new MessageEmbed.Field(
                "Value",
                value + " (" + level.name() + ")",
                false
        );
    }
}
