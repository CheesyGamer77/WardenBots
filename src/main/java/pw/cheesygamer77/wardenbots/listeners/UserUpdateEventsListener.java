package pw.cheesygamer77.wardenbots.listeners;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import pw.cheesygamer77.cheedautilities.DiscordColor;
import pw.cheesygamer77.wardenbots.core.builders.EmbedBuilder;
import pw.cheesygamer77.wardenbots.core.moderation.ModLogEvent;
import pw.cheesygamer77.wardenbots.internal.layouts.UserChangesLayouts;

import java.awt.*;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class UserUpdateEventsListener extends ListenerAdapter {
    @Override
    public void onGuildMemberUpdateNickname(@NotNull GuildMemberUpdateNicknameEvent event) {
        TextChannel channel = ModLogEvent.USER_CHANGES.fetchLogChannel(event.getGuild());
        if(channel != null) {
            String before = event.getOldNickname();
            String after = event.getNewNickname();
            User user = event.getUser();

            // set title, color, and embed fields depending on
            // whether the user set, cleared, or changed their nickname
            String title;
            Color color;
            List<MessageEmbed.Field> fields;
            if (before == null && after != null) {
                title = "Nickname Set";
                color = DiscordColor.BLUE;
                fields = List.of(new MessageEmbed.Field("Nickname", after, false));
            }
            else if (before != null && after == null) {
                title = "Nickname Cleared";
                color = DiscordColor.DARK_BLUE;
                fields = List.of(new MessageEmbed.Field("Original Nickname", before, false));
            }
            else {
                title = "Nickname Changed";
                color = DiscordColor.TEAL;
                fields = List.of(
                        new MessageEmbed.Field("Before", before, false),
                        new MessageEmbed.Field("After", after, false)
                );
            }

            // set base embed
            EmbedBuilder base = (EmbedBuilder) new EmbedBuilder()
                    .setAuthor(user, true)
                    .setTitle(title)
                    .setDescription(user.getAsMention() + " changed their nickname")
                    .setColor(color)
                    .setFooter("User ID: " + user.getId())
                    .setTimestamp(Instant.now());

            // add additional previously defined fields
            for(MessageEmbed.Field field : fields) {
                base.addField(field);
            }

            // send log
            channel.sendMessage(
                    new MessageBuilder()
                            .setContent(user.getId())
                            .setEmbeds(base.build())
                            .setActionRows(
                                    UserChangesLayouts.getDefault()
                            )
                            .build()
            ).queue();
        }
    }

    @Override
    public void onGuildMemberRoleAdd(@NotNull GuildMemberRoleAddEvent event) {
        TextChannel channel = ModLogEvent.USER_CHANGES.fetchLogChannel(event.getGuild());
        if(channel != null) {
            Member member = event.getMember();

            // create a list of role mentions sorted by role position
            List<String> added = event.getRoles().stream()
                    .sorted(Comparator.comparing(Role::getPosition))
                    .map(r -> r.getAsMention() + ": " + r.getId())
                    .collect(Collectors.toList());

            // I like proper grammar
            String title, description;
            if(added.size() == 1) {
                title = "Role Added";
                description = member.getAsMention() + " had a role added";
            }
            else {
                title = "Roles Added [" + added.size() + "]";
                description = member.getAsMention() + " had " + added.size() + " roles added";
            }

            // build the embed
            MessageEmbed embed = new EmbedBuilder()
                    .setAuthor(member.getUser(), true)
                    .setTitle(title)
                    .setDescription(description)
                    .setColor(DiscordColor.FUCHSIA)
                    .addField("Added", String.join("\n", added), false)
                    .setFooter(added.size() + " roles added\nUser ID: " + member.getId())
                    .setTimestamp(Instant.now())
                    .build();

            // send the log
            channel.sendMessage(
                    new MessageBuilder()
                            .setContent(member.getId())
                            .setEmbeds(embed)
                            .setActionRows(UserChangesLayouts.getDefault())
                            .build()
            ).queue();
        }
    }

    @Override
    public void onGuildMemberRoleRemove(@NotNull GuildMemberRoleRemoveEvent event) {
        TextChannel channel = ModLogEvent.USER_CHANGES.fetchLogChannel(event.getGuild());
        if(channel != null) {
            Member member = event.getMember();

            // create a list of role mentions sorted by role position
            List<String> removed = event.getRoles().stream()
                    .sorted(Comparator.comparing(Role::getPosition))
                    .map(r -> r.getAsMention() + ": " + r.getId())
                    .collect(Collectors.toList());

            // I like proper grammar
            String title, description;
            if(removed.size() == 1) {
                title = "Role Removed";
                description = member.getAsMention() + " had a role removed";
            }
            else {
                title = "Roles Removed [" + removed.size() + "]";
                description = member.getAsMention() + " had " + removed.size() + " roles removed";
            }

            // build the embed
            MessageEmbed embed = new EmbedBuilder()
                    .setAuthor(member.getUser(), true)
                    .setTitle(title)
                    .setDescription(description)
                    .setColor(DiscordColor.PURPLE)
                    .addField("Removed", String.join("\n", removed), false)
                    .setFooter(removed.size() + " roles removed\nUser ID: " + member.getId())
                    .setTimestamp(Instant.now())
                    .build();

            // send the log
            channel.sendMessage(
                    new MessageBuilder()
                            .setContent(member.getId())
                            .setEmbeds(embed)
                            .setActionRows(UserChangesLayouts.getDefault())
                            .build()
            ).queue();
        }
    }
}
