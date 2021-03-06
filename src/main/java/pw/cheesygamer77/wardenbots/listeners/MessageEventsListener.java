package pw.cheesygamer77.wardenbots.listeners;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.jetbrains.annotations.NotNull;
import pw.cheesygamer77.cheedautilities.DiscordColor;
import pw.cheesygamer77.wardenbots.core.builders.EmbedBuilder;
import pw.cheesygamer77.wardenbots.core.moderation.ModLogEvent;
import pw.cheesygamer77.wardenbots.internal.serializers.SerializableMessage;

import java.time.Duration;
import java.time.Instant;

/**
 * Class for handling Discord message events, in particular the following:
 * <ol>
 *     <li>Message Create</li>
 *     <li>Message Edit</li>
 *     <li>Message Delete</li>
 * </ol>
 *
 * This listener contains its own internal cache in order to track incoming Discord messages for the purpose
 * of moderation logs. This cache is automatically updated as messages are sent, edited, and deleted
 */
@SuppressWarnings("unused")
public class MessageEventsListener extends ListenerAdapter {
    private static final CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
            .withCache(
                    "messageCache",
                    CacheConfigurationBuilder.newCacheConfigurationBuilder(
                            Long.class,
                            SerializableMessage.class,
                            ResourcePoolsBuilder.heap(1000)
                    ).withExpiry(
                            ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofDays(1))
                    )
            )
            .build(true);

    private final Cache<Long, SerializableMessage> messageCache = cacheManager.getCache(
            "messageCache",
            Long.class,
            SerializableMessage.class
    );

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        // TODO: Add content filter integration

        // only cache guild messages and non-system messages
        // this is because non-guild messages have no sense to be handled, and system messages
        // have no need to be logged in addition to the fact that they contain their own weird set of quirks
        Message message = event.getMessage();
        if(message.isFromGuild() && !message.getType().isSystem())
            messageCache.put(event.getMessageIdLong(), new SerializableMessage(event.getMessage()));
    }

    @Override
    public void onMessageUpdate(@NotNull MessageUpdateEvent event) {
        // TODO: Add content filter integration

        long messageID = event.getMessageIdLong();
        Message after = event.getMessage();

        // ignore dm messages, system messages, and messages that are in response to an interaction
        if(!after.isFromGuild() || after.getType().isSystem() || after.getInteraction() != null) return;

        TextChannel channel = ModLogEvent.MESSAGE_EDITS.fetchLogChannel(event.getGuild());
        if(channel != null) {
            User author = event.getAuthor();

            EmbedBuilder base = (EmbedBuilder) new EmbedBuilder()
                    .setAuthor(author, true)
                    .setDescription("Message sent by " + author.getAsMention() + " was edited in " + event.getChannel().getAsMention())
                    .setColor(DiscordColor.YELLOW)
                    .setTimestamp(Instant.now());

            if (messageCache.containsKey(messageID)) {
                // message is currently cached
                SerializableMessage entry = messageCache.get(messageID);

                // don't log message updates unless they're edits
                if(entry.getContent().equals(after.getContentRaw())) return;

                base.setTitle("Message Edited")
                        .addFields(entry.getContent(), true, "Before")
                        .setFooter(after)
                        .setTimestamp(Instant.now());
            }
            else
                return;

            base.addFields(after, true, "After");

            channel.sendMessage(
                    new MessageBuilder()
                            .setContent(String.valueOf(messageID))
                            .setEmbeds(base.build())
                            .build()
            ).queue();
        }

        // update cache entry
        messageCache.put(messageID, new SerializableMessage(after));
    }

    @Override
    public void onMessageDelete(@NotNull MessageDeleteEvent event) {
        TextChannel channel = ModLogEvent.MESSAGE_DELETES.fetchLogChannel(event.getGuild());
        if(channel != null) {
            MessageEmbed logEmbed;
            if(messageCache.containsKey(event.getMessageIdLong())) {
                // cached message was deleted
                SerializableMessage cachedMessage = messageCache.get(event.getMessageIdLong());

                logEmbed = new EmbedBuilder()
                        .setAuthor(cachedMessage.getAuthor())
                        .setTitle("Message Deleted")
                        .setDescription(
                                "Message sent by " + cachedMessage.getAuthor().getAsMention()
                                        + " was deleted in " + cachedMessage.getTextChannel().getAsMention())
                        .setColor(DiscordColor.GOLD)
                        .addFields(cachedMessage, true, "Message")
                        .setFooter(cachedMessage)
                        .setTimestamp(Instant.now())
                        .build();
            }
            else {
                // uncached message was deleted
                logEmbed = new EmbedBuilder()
                        .setTitle("Message Deleted (Uncached)")
                        .setDescription("An uncached message was deleted")
                        .setColor(DiscordColor.DARK_GOLD)
                        .setFooter("Message ID: " + event.getMessageIdLong())
                        .setTimestamp(Instant.now())
                        .build();
            }

            channel.sendMessage(
                    new MessageBuilder()
                            .setContent(event.getMessageId())
                            .setEmbeds(logEmbed)
                            .build()
            ).queue();
        }

        messageCache.remove(event.getMessageIdLong());
    }
}
