package pw.cheesygamer77.wardenbots.commands.clean;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;
import pw.cheesygamer77.cheedautilities.DiscordColor;
import pw.cheesygamer77.wardenbots.commands.clean.internal.CleanSubcommand;

import java.util.function.Predicate;

public class BotsSubcommand extends CleanSubcommand {
    public BotsSubcommand() {
        super("bots", "Cleans messages sent by bots");
    }

    @Override
    public @NotNull Predicate<Message> getPredicate() {
        return message -> message.getAuthor().isBot();
    }

    @Override
    public @NotNull Message getResultMessage(int count, @NotNull TextChannel channel) {
        return new MessageBuilder()
                .setEmbeds(new EmbedBuilder()
                        .setDescription("Cleaned " + count + " messages in " + channel.getAsMention())
                        .setColor(DiscordColor.BRAND_GREEN)
                        .build())
                .build();
    }
}
