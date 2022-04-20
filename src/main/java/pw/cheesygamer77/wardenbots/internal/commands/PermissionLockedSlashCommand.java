package pw.cheesygamer77.wardenbots.internal.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;
import pw.cheesygamer77.cheedautilities.commands.slash.SlashCommand;
import pw.cheesygamer77.wardenbots.core.EmbedUtil;

public abstract class PermissionLockedSlashCommand extends SlashCommand {
    protected Permission[] permissionsRequired;

    public PermissionLockedSlashCommand(@NotNull SlashCommandData data, Permission... permissionsRequired) {
        super(data);
        this.permissionsRequired = permissionsRequired;
    }

    @Override
    public void call(@NotNull SlashCommandInteractionEvent event) {
        // require the command to be ran in a guild
        if(event.getGuild() == null || event.getMember() == null) {
            event.replyEmbeds(EmbedUtil.getFailure("This command cannot be ran in direct messages"))
                    .setEphemeral(true)
                    .queue();
            return;
        }

        // check permissions
        for(Permission permission : this.permissionsRequired) {
            if(!event.getMember().hasPermission(permission)) {
                event.replyEmbeds(EmbedUtil.getFailure(
                        "The `" + permission.getName() + "` is required to use this command"))
                        .setEphemeral(true)
                        .queue();
                return;
            }
        }

        super.call(event);
    }
}
