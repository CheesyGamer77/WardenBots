package pw.cheesygamer77.wardenbots.commands.moderation.role;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import pw.cheesygamer77.cheedautilities.commands.slash.SlashCommand;

public class RoleCommands extends SlashCommand {
    public RoleCommands() {
        super(Commands.slash("role", "Role Commands"));
        addSubcommand(new InfoSubcommand());
        addSubcommand(new AddSubcommand());
        addSubcommand(new RemoveSubcommand());
    }

    @Override
    public void invoke(@NotNull SlashCommandInteractionEvent event) {}
}
