package pw.cheesygamer77.wardenbots.core;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import pw.cheesygamer77.cheedautilities.DiscordColor;

public final class EmbedUtil {
    public static @NotNull MessageEmbed getSuccess(@NotNull String description) {
        return new EmbedBuilder()
                .setDescription(":white_check_mark: " + description)
                .setColor(DiscordColor.BRAND_GREEN)
                .build();
    }
}
