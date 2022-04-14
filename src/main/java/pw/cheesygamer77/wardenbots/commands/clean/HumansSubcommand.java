package pw.cheesygamer77.wardenbots.commands.clean;

import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;
import pw.cheesygamer77.wardenbots.commands.clean.internal.CleanSubcommand;

import java.util.function.Predicate;

public class HumansSubcommand extends CleanSubcommand {
    public HumansSubcommand() {
        super("humans", "Cleans messages sent by humans");
    }

    @Override
    public @NotNull Predicate<Message> getPredicate() {
        return message -> !message.getAuthor().isBot();
    }
}
