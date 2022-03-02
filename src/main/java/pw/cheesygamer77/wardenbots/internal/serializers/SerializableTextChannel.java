package pw.cheesygamer77.wardenbots.internal.serializers;

import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public final class SerializableTextChannel implements Serializable {
    private final long id;

    public SerializableTextChannel(@NotNull TextChannel channel) {
        this.id = channel.getIdLong();
    }

    public String getAsMention() {
        return "<#" + id + ">";
    }
}
