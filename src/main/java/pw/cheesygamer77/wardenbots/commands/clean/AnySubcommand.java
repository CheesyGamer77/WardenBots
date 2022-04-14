package pw.cheesygamer77.wardenbots.commands.clean;

import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;
import pw.cheesygamer77.wardenbots.commands.clean.internal.CleanSubcommand;

import java.util.function.Predicate;

public class AnySubcommand extends CleanSubcommand {
    public AnySubcommand() {
        super("any", "Cleans an amount of messages");
    }

    @Override
    public @NotNull Predicate<Message> getPredicate() {
        return m -> true;
    }
}
