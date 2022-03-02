package pw.cheesygamer77.wardenbots.internal.serializers;

import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.time.OffsetDateTime;

/**
 * Represents a "serializable" message
 *
 * This is essentially a heavily minimized {@link Message} implementing
 * {@link Serializable} in order to make this cache-able within ehcache
 */
public final class SerializableMessage implements Serializable {
    private final long id;
    private final String content;
    private final SerializableUser author;
    private final OffsetDateTime createdAt;
    private final SerializableTextChannel channel;

    public SerializableMessage(@NotNull Message message) {
        this.id = message.getIdLong();
        this.content = message.getContentRaw();
        this.author = new SerializableUser(message.getAuthor());
        this.createdAt = message.getTimeCreated();

        // TODO: This throws IllegalStateException with messages built from MessageBuilder
        this.channel = new SerializableTextChannel(message.getTextChannel());
    }

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public SerializableUser getAuthor() { return author; }

    public OffsetDateTime getCreatedAt() { return createdAt; }

    public SerializableTextChannel getTextChannel() { return channel; }
}
