package pw.cheesygamer77.wardenbots.commands.moderation;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import pw.cheesygamer77.cheedautilities.DiscordColor;
import pw.cheesygamer77.wardenbots.internal.commands.BanPermissionLockedSlashCommand;

public class BanCommand extends BanPermissionLockedSlashCommand {
    public BanCommand() {
        super(Commands.slash("ban", "Bans a user from the server")
                .addOption(OptionType.USER, "user", "The user to ban", true)
                .addOption(OptionType.STRING, "reason", "The reason behind banning the user")
                .addOptions(new OptionData(
                        OptionType.INTEGER,
                        "delete_days",
                        "The number of days to delete messages sent by the user")
                        .setRequiredRange(0, 7)
                ));
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

        // get the number of days to delete their messages, or default to 0 days
        OptionMapping daysMapping = event.getOption("delete_days");
        int deleteDays = 0;
        if(daysMapping != null) deleteDays = daysMapping.getAsInt();

        // do the banning
        // TODO: Add modlog
        event.getGuild().ban(target, deleteDays)
                .reason(reason)
                .flatMap(ra -> hook.editOriginalEmbeds(new EmbedBuilder()
                        .setDescription(":white_check_mark: " + target.getAsTag() + " was banned")
                        .setColor(DiscordColor.BRAND_GREEN)
                        .build()))
                .queue();
    }
}
