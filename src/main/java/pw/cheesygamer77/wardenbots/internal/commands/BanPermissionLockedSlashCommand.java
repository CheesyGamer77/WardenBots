package pw.cheesygamer77.wardenbots.internal.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;

public abstract class BanPermissionLockedSlashCommand extends PermissionLockedSlashCommand {
    public BanPermissionLockedSlashCommand(@NotNull SlashCommandData data) {
        super(data, Permission.BAN_MEMBERS);
    }
}
