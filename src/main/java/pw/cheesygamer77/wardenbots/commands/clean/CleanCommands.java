package pw.cheesygamer77.wardenbots.commands.clean;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import pw.cheesygamer77.cheedautilities.commands.slash.SlashCommand;

import java.util.Objects;

public class CleanCommands extends SlashCommand {
    public static final OptionData COUNT_OPTION = new OptionData(
            OptionType.INTEGER, "count", "The number of messages to clean")
            .setRequired(true)
            .setRequiredRange(2, 100);

    public static final OptionData CHANNEL_OPTION = new OptionData(
            OptionType.CHANNEL, "channel", "The channel to clean messages from")
            .setChannelTypes(ChannelType.TEXT);

    public CleanCommands() {
        super(Commands.slash("clean", "Commands to clean up unwanted messages"));
        addPredicate(event -> event.getGuild() != null && Objects.requireNonNull(event.getMember()).hasPermission(Permission.MESSAGE_MANAGE));
        addSubcommand(new AnySubcommand());
    }

    @Override
    public void invoke(@NotNull SlashCommandInteractionEvent event) {}
}
