package pw.cheesygamer77.wardenbots.commands.config;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import pw.cheesygamer77.cheedautilities.commands.slash.SlashCommand;

public class ConfigCommands extends SlashCommand {
    public ConfigCommands() {
        super(Commands.slash("config", "Guild bot configuration commands"));
        addPredicate(event -> event.getMember() != null && event.getMember().hasPermission(Permission.MANAGE_SERVER));
        addSubcommand(new ModLogSubcommand());
    }

    @Override
    public void invoke(@NotNull SlashCommandInteractionEvent event) {}
}
