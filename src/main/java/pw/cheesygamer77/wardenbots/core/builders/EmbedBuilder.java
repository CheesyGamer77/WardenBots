package pw.cheesygamer77.wardenbots.core.builders;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import pw.cheesygamer77.wardenbots.internal.serializers.SerializableMessage;
import pw.cheesygamer77.wardenbots.internal.serializers.SerializableUser;

import java.awt.*;
import java.util.List;

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
    public @NotNull EmbedBuilder setAuthor(@NotNull User user, boolean asTag) {
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
    public @NotNull EmbedBuilder setAuthor(@NotNull Member member) {
        return (EmbedBuilder) this.setAuthor(member.getEffectiveName(), null, member.getEffectiveAvatarUrl());
    }

    /**
     * Sets the author of the {@link MessageEmbed} based off of the given {@link SerializableUser}'s tag and avatar url
     * @param user The user to use for the embed author
     * @return The modified builder
     */
    public @NotNull EmbedBuilder setAuthor(@NotNull SerializableUser user) {
        return (EmbedBuilder) setAuthor(user.getAsTag(), null, user.getAvatarUrl());
    }

    public @NotNull EmbedBuilder setTitle(String title) {
        return (EmbedBuilder) super.setTitle(title);
    }

    public @NotNull EmbedBuilder setDescription(String description) {
        return (EmbedBuilder) super.setDescription(description);
    }

    public @NotNull EmbedBuilder setColor(Color color) {
        return (EmbedBuilder) super.setColor(color);
    }

    /**
     * Adds one or more {@link MessageEmbed.Field}s depending on the length of the given String content
     * @param content The string to split between multiple fields if needed
     * @param inline Whether to add the fields inline with eachother
     * @param base The default embed field name. The first field added will have this as its name. Subsequent fields will
     *        have the name {@code base + " (cont.)}
     * @return The modified builder
     */
    public @NotNull EmbedBuilder addFields(@NotNull String content, boolean inline, String base) {
        if(content.isEmpty())
            return this;

        List<String> chunks = Lists.newArrayList(Splitter.fixedLength(MessageEmbed.VALUE_MAX_LENGTH).split(content));
        if(chunks.size() >= 1) {
            this.addField(base, chunks.get(0), inline);
        }

        chunks.remove(0);
        for(String chunk : chunks)
            this.addField(base + " (cont.)", chunk, inline);

        return this;
    }

    /**
     * Adds one or more {@link MessageEmbed.Field}s to the embed depending on the {@link Message}'s
     * content length. This is mostly intended for mod logs
     * @param message The message to use
     * @param inline Whether to add the fields inline with eachother
     * @param base The default embed field name. The first field added will have this as its name. Subsequent fields will
     *             have the name {@code base + " (cont.)}
     * @return The modified builder
     */
    public @NotNull EmbedBuilder addFields(@NotNull Message message, boolean inline, String base) {
        return addFields(message.getContentRaw(), inline, base);
    }

    /**
     * Adds one or more {@link MessageEmbed.Field}s to the embed depending on the {@link SerializableMessage}'s
     * content length. This is
     * @param message The message to use
     * @param inline Whether to add the fields inline with eachother
     * @param base The default embed field name. The first field added will have this as its name. Subsequent fields will
     *             have the name {@code base + " (cont.)}
     * @return The modified builder
     */
    public @NotNull EmbedBuilder addFields(@NotNull SerializableMessage message, boolean inline, String base) {
        return addFields(message.getContent(), inline, base);
    }

    /**
     * Sets the footer of the {@link net.dv8tion.jda.api.entities.MessageEmbed} based
     * off of the given {@link User}'s Discord ID and avatar.
     *
     * This is mostly intended for mod logs.
     * @param user The user to use for the embed footer
     * @return The modified builder
     */
    public @NotNull EmbedBuilder setFooter(@NotNull User user) {
        return (EmbedBuilder) this.setFooter("User ID: " + user.getId(), null);
    }

    /**
     * Sets the footer of the {@link MessageEmbed} based off of the given {@link Message}'s Discord ID.
     * This is mostly intended for mod logs.
     * @param message The message to use for the embed footer
     * @return The modified builder
     */
    public @NotNull EmbedBuilder setFooter(@NotNull Message message) {
        return (EmbedBuilder) this.setFooter("User ID: " + message.getAuthor().getId() + "\nMessage ID: " + message.getId());
    }

    /**
     * Sets the footer of the {@link MessageEmbed} based off of the given {@link SerializableMessage}'s Discord ID.
     * This is mostly intended for mod logs.
     * @param message The message to use for the embed footer
     * @return The modified builder
     */
    public @NotNull EmbedBuilder setFooter(@NotNull SerializableMessage message) {
        return (EmbedBuilder) this.setFooter("User ID: " + message.getAuthor().getId() + "\nMessage ID: " + message.getId());
    }
}
