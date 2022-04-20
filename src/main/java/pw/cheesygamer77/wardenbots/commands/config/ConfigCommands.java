package pw.cheesygamer77.wardenbots.commands.config;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import pw.cheesygamer77.wardenbots.internal.commands.PermissionLockedSlashCommand;

public class ConfigCommands extends PermissionLockedSlashCommand {
    public ConfigCommands() {
        super(Commands.slash("config", "Guild bot configuration commands"), Permission.MANAGE_SERVER);
        addSubcommand(new ModLogSubcommand());
    }

    @Override
    public void invoke(@NotNull SlashCommandInteractionEvent event) {}
}
