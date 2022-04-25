package pw.cheesygamer77.wardenbots.internal.db.internal;

import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pw.cheesygamer77.wardenbots.core.moderation.ModLogEvent;

import java.io.Serializable;
import java.util.HashMap;

public class GuildLogConfiguration implements Serializable {
    private final HashMap<ModLogEvent, Long> map;

    public GuildLogConfiguration(@NotNull HashMap<ModLogEvent, Long> map) {
        this.map = map;
    }

    public @Nullable Long getChannelID(@NotNull ModLogEvent event) {
        return map.getOrDefault(event, null);
    }

    public GuildLogConfiguration setChannelID(@NotNull ModLogEvent event, @NotNull TextChannel channel) {
        map.put(event, channel.getIdLong());
        return this;
    }
}
