package pw.cheesygamer77.wardenbots.commands.clean;

import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;
import pw.cheesygamer77.wardenbots.commands.clean.internal.CleanSubcommand;

import java.util.function.Predicate;

public class TextSubcommand extends CleanSubcommand {
    public TextSubcommand() {
        super("text", "Cleans messages containing any text");
    }

    @Override
    public @NotNull Predicate<Message> getPredicate() {
        return message -> !message.getContentStripped().isEmpty();
    }
}
