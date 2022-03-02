package pw.cheesygamer77.wardenbots.internal.serializers;

import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class SerializableUser {
    private final long id;
    private final String name;
    private final String discriminator;
    private final String avatarUrl;

    public SerializableUser(@NotNull User user) {
        this.id = user.getIdLong();
        this.name = user.getName();
        this.discriminator = user.getDiscriminator();
        this.avatarUrl = user.getEffectiveAvatarUrl();
    }

    public long getId() {
        return id;
    }

    @Contract(pure = true)
    public @NotNull String getAsTag() {
        return this.name + "#" + this.discriminator;
    }

    public @NotNull String getAvatarUrl() {
        return avatarUrl;
    }

    @Contract(pure = true)
    public @NotNull String getAsMention() {
        return "<@" + id + ">";
    }
}
