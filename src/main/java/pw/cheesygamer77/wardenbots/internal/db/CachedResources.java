package pw.cheesygamer77.wardenbots.internal.db;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pw.cheesygamer77.wardenbots.core.moderation.ModLogEvent;
import pw.cheesygamer77.wardenbots.internal.db.internal.GuildLogConfiguration;

import java.time.Duration;

/**
 * Resource Manager class that serves as the main interface between the database and Warden's cache
 * <br>This class is designed to abstract away whether a requested resource is cached, using the database
 * as a fallback
 */
public final class CachedResources {
    private static final Logger LOGGER = LoggerFactory.getLogger(CachedResources.class);

    private static final CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
            .withCache(
                    "guildLoggingChannelIDCache",
                    CacheConfigurationBuilder.newCacheConfigurationBuilder(
                            Long.class,
                            GuildLogConfiguration.class,
                            ResourcePoolsBuilder.heap(1000)
                    ).withExpiry(
                            ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofDays(1))
                    )
            )
            .build(true);

    private static final Cache<Long, GuildLogConfiguration> guildLogConfigCache = cacheManager.getCache(
            "guildLoggingChannelIDCache",
            Long.class, GuildLogConfiguration.class
    );
    
    public static @NotNull GuildLogConfiguration getLogConfiguration(@NotNull Guild guild) {
        // fetch from our cache first if able
        long guildID = guild.getIdLong();
        if(guildLogConfigCache.containsKey(guildID))
            return guildLogConfigCache.get(guildID);

        // cache miss
        LOGGER.debug("Caching Guild Logging Configuration for guild " + guildID);
        GuildLogConfiguration config = DatabaseManager.fetchLogChannelConfiguration(guild);
        guildLogConfigCache.put(guildID, config);

        return config;
    }

    /**
     * Gets the {@link TextChannel} associated with a particular {@link ModLogEvent} for a particular {@link Guild}.
     * <br>This will cache the full configuration if the entry is not already present in the configuration cache.
     * This will return {@code null} if the mapped channel ID does not exist in the map OR if the channel ID is not
     * associated with a particular channel.
     * @param event The logging event to get the log channel for
     * @param guild The guild to retrieve the configuration of
     * @return The channel associated with the given event if set and valid, or null otherwise
     */
    public static @Nullable TextChannel getLogChannel(@NotNull ModLogEvent event, @NotNull Guild guild) {
        GuildLogConfiguration config = getLogConfiguration(guild);
        Long channelId = config.getChannelID(event);

        if(channelId != null) {
            TextChannel channel = guild.getTextChannelById(channelId);
            return channel != null && channel.canTalk() ? channel : null;
        }

        return null;
    }

    /**
     * Sets the logging {@link TextChannel} used for a particular {@link ModLogEvent}
     * @param event The logging event to set the log channel of
     * @param channel The channel to set for the logging event
     */
    public static void setLogChannel(@NotNull ModLogEvent event, @NotNull TextChannel channel) {
        // get (and cache) the config for the guild
        Guild guild = channel.getGuild();
        GuildLogConfiguration config = getLogConfiguration(guild);

        // set cache entry
        config = config.setChannelID(event, channel);
        guildLogConfigCache.put(guild.getIdLong(), config);

        // set db entry
        DatabaseManager.setModLogChannel(event, channel);
    }
}
