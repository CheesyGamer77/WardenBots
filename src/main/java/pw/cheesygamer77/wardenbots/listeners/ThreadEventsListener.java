package pw.cheesygamer77.wardenbots.listeners;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pw.cheesygamer77.cheedautilities.DiscordColor;
import pw.cheesygamer77.wardenbots.core.builders.EmbedBuilder;
import pw.cheesygamer77.wardenbots.core.moderation.ModLogEvent;

import java.time.Instant;
import java.util.Locale;

@SuppressWarnings("unused")
public class ThreadEventsListener extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadEventsListener.class);

    private @NotNull Message getCreateLogMessage(@NotNull Member owner, @NotNull ThreadChannel thread) {
        IThreadContainer parent = thread.getParentChannel();
        String threadID = thread.getId();

        return new MessageBuilder()
                .setContent(threadID)
                .setEmbeds(
                        new EmbedBuilder()
                                .setAuthor(owner)
                                .setTitle("Thread Created")
                                .setDescription(
                                        "Thread " + thread.getAsMention() +
                                                " was created in parent channel " + parent.getAsMention()
                                )
                                .setColor(DiscordColor.BLURPLE)
                                .addField(
                                        "Thread",
                                        "`" + thread.getName() + "` (ID: `" + thread.getId() +"`)",
                                        false
                                )
                                .addField(
                                        "Parent Channel",
                                        "`" + parent.getName() + "` (ID: `" + parent.getId() + "`)",
                                        false
                                )
                                .addField(
                                        "Auto-Archives After",
                                        "`" + thread.getAutoArchiveDuration().name()
                                                .replace("TIME_", "")
                                                .replace("_", " ")
                                                .toLowerCase(Locale.ROOT) + "` of inactivity",
                                        false
                                )
                                .setFooter(
                                        "Thread ID: " + threadID + "\nParent Channel ID: "
                                                + parent.getId() + "\nOwner ID: " + owner.getId()
                                )
                                .setTimestamp(Instant.now())
                                .build()
                )
                .build();
    }

    @Override
    public void onChannelCreate(@NotNull ChannelCreateEvent event) {
        if(event.getChannelType().isThread()) {
            TextChannel channel = ModLogEvent.THREAD_EVENTS.fetchLogChannel(event.getGuild());
            if(channel != null) {
                long threadID = event.getChannel().getIdLong();

                // fetch thread channel for additional log data
                ThreadChannel thread = event.getGuild().getThreadChannelById(threadID);
                if(thread != null) {
                    // retrieve the owner of the thread and send log
                    event.getGuild().retrieveMemberById(thread.getOwnerIdLong(), false)
                            .flatMap(owner -> channel.sendMessage(getCreateLogMessage(owner, thread)))
                            .queue();
                }
                else {
                    LOGGER.warn("Newly created thread " + threadID + " in guild " + event.getGuild().getId() + " is null!");
                }
            }
        }
    }

    @Override
    public void onChannelDelete(@NotNull ChannelDeleteEvent event) {
        if(event.getChannelType().isThread()) {
            TextChannel channel = ModLogEvent.THREAD_EVENTS.fetchLogChannel(event.getGuild());
            if(channel != null)
                channel.sendMessage(
                        new MessageBuilder()
                                .setContent(event.getChannel().getId())
                                .setEmbeds(
                                        new EmbedBuilder()
                                                .setTitle("Thread Deleted")
                                                .setDescription("Thread `" + event.getChannel().getName() + "` was deleted")
                                                .setColor(DiscordColor.BRAND_RED)
                                                .setFooter("Channel ID: " + event.getChannel().getId())
                                                .setTimestamp(Instant.now())
                                                .build()
                                )
                                .build()
                ).queue();
        }
    }
}
