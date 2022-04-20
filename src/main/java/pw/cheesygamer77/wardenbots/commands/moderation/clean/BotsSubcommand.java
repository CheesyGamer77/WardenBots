package pw.cheesygamer77.wardenbots.commands.moderation.clean;

import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;
import pw.cheesygamer77.wardenbots.commands.moderation.clean.internal.CleanSubcommand;

import java.util.function.Predicate;

public class BotsSubcommand extends CleanSubcommand {
    public BotsSubcommand() {
        super("bots", "Cleans messages sent by bots");
    }

    @Override
    public @NotNull Predicate<Message> getPredicate() {
        return message -> message.getAuthor().isBot();
    }
}
