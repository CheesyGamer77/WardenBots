package pw.cheesygamer77.wardenbots.core.builders;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

/**
 * An extension of {@link net.dv8tion.jda.api.EmbedBuilder} to provide a couple
 * utility methods that Warden uses for constructing embeds
 */
public class EmbedBuilder extends net.dv8tion.jda.api.EmbedBuilder {
    /**
     * Sets the author of the {@link net.dv8tion.jda.api.entities.MessageEmbed}, based off
     * of the given {@link User}'s name and avatar
     * @param user The user to use for the embed author
     * @param asTag Whether to use the user's full Discord tag as the author name or not
     * @return The modified builder
     */
    public EmbedBuilder setAuthor(@NotNull User user, boolean asTag) {
        String name = asTag ? user.getAsTag() : user.getName();
        return (EmbedBuilder) this.setAuthor(name, null, user.getEffectiveAvatarUrl());
    }

    /**
     * Sets the author of the {@link net.dv8tion.jda.api.entities.MessageEmbed} based off
     * of the given {@link Member}.
     *
     * If the member has a guild-specific name/avatar, the guild-specific name/avatar will be used
     * @param member The member to use for the embed author
     * @return The modified builder
     */
    public EmbedBuilder setAuthor(@NotNull Member member) {
        return (EmbedBuilder) this.setAuthor(member.getEffectiveName(), null, member.getEffectiveAvatarUrl());
    }

    /**
     * Sets the footer of the {@link net.dv8tion.jda.api.entities.MessageEmbed} based
     * off of the given {@link User}'s Discord ID and avatar.
     *
     * This is mostly intended for mod logs.
     * @param user The user to use for the embed footer
     * @return The modified builder
     */
    public EmbedBuilder setFooter(@NotNull User user) {
        return (EmbedBuilder) this.setFooter("User ID: " + user.getId(), null);
    }
}
