package pw.cheesygamer77.wardenbots.listeners;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import pw.cheesygamer77.wardenbots.core.DiscordColor;
import pw.cheesygamer77.wardenbots.core.builders.EmbedBuilder;
import pw.cheesygamer77.wardenbots.core.moderation.ModLogChannel;

import java.time.Instant;

@SuppressWarnings("unused")
public class JoinsNLeavesListener extends ListenerAdapter {
    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        // TODO: Add Anti-Raid integration

        TextChannel channel = ModLogChannel.JOINS.fetch(event.getGuild());
        if(channel != null) {
            Member member = event.getMember();
            String memberID = member.getId();

            channel.sendMessage(
                    new MessageBuilder()
                            .setContent(memberID)
                            .setEmbeds(
                                    new EmbedBuilder()
                                            .setAuthor(event.getUser(), true)
                                            .setTitle("User Joined")
                                            .setDescription(member.getAsMention() + " joined the server")
                                            .setColor(DiscordColor.BRAND_GREEN)
                                            // TODO: Add field for showing account creation datetime delta
                                            // TODO: Add field for showing user risk factor
                                            .setFooter("User ID: " + memberID)
                                            .setTimestamp(Instant.now())
                                            .build()
                            )
                            .build()
            ).queue();
        }
    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        // TODO: Add checks for leaves vs kicks vs bans

        TextChannel channel = ModLogChannel.LEAVES.fetch(event.getGuild());
        if(channel != null) {
            User user = event.getUser();
            String userID = user.getId();

            channel.sendMessage(
                    new MessageBuilder()
                            .setContent(userID)
                            .setEmbeds(
                                    new EmbedBuilder()
                                            .setAuthor(user, true)
                                            .setTitle("User Left")
                                            .setDescription(user.getAsTag() + " left the server")
                                            .setColor(DiscordColor.BRAND_RED)
                                            .setFooter("User ID: " + userID)
                                            .setTimestamp(Instant.now())
                                            .build()
                            )
                            .build()
            ).queue();
        }
    }
}
