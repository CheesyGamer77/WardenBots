package pw.cheesygamer77.wardenbots.commands.moderation;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import pw.cheesygamer77.cheedautilities.DiscordColor;
import pw.cheesygamer77.wardenbots.internal.commands.BanPermissionLockedSlashCommand;

public class UnbanCommand extends BanPermissionLockedSlashCommand {
    public UnbanCommand() {
        super(Commands.slash("unban", "Unbans a user from the server")
                .addOption(OptionType.USER, "user", "The user to unban", true)
                .addOption(OptionType.STRING, "reason", "The reason behind unbanning the user"));
    }


    @Override
    public void invoke(@NotNull SlashCommandInteractionEvent event) {
        // TODO: Allow guilds to specify whether replies to mod command invocations should be ephemeral or not
        if(event.getGuild() == null) return;

        event.deferReply().queue();
        InteractionHook hook = event.getHook();

        // get target
        OptionMapping targetMapping = event.getOption("user");
        User target;
        if(targetMapping == null)
            return;
        else
            target = targetMapping.getAsUser();

        // get reason (if applicable)
        OptionMapping reasonMapping = event.getOption("reason");
        String reason = null;
        if(reasonMapping != null)
            reason = reasonMapping.getAsString();

        // do the banning
        // TODO: Add modlog
        event.getGuild().unban(target)
                .reason(reason)
                .flatMap(ra -> hook.editOriginalEmbeds(new EmbedBuilder()
                        .setDescription(":white_check_mark: " + target.getAsTag() + " was unbanned")
                        .setColor(DiscordColor.BRAND_GREEN)
                        .build()))
                .queue();
    }
}
