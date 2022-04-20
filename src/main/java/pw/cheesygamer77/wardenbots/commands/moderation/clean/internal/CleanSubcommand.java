package pw.cheesygamer77.wardenbots.commands.moderation.clean.internal;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;
import pw.cheesygamer77.cheedautilities.commands.slash.Subcommand;
import pw.cheesygamer77.wardenbots.commands.moderation.clean.CleanCommands;
import pw.cheesygamer77.wardenbots.core.EmbedUtil;

import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class CleanSubcommand extends Subcommand {
    public CleanSubcommand(@NotNull String name, @NotNull String description) {
        super(new SubcommandData(name, description).addOptions(CleanCommands.COUNT_OPTION, CleanCommands.CHANNEL_OPTION));
    }

    public abstract @NotNull Predicate<Message> getPredicate();

    public @NotNull Message getResultMessage(int count, @NotNull TextChannel channel) {
        return new MessageBuilder().setEmbeds(
                EmbedUtil.getSuccess("Cleaned " + count + " messages in " + channel.getAsMention())
        ).build();
    }

    protected CompletableFuture<Void> clean(int count, @NotNull TextChannel channel) {
        return channel.getIterableHistory()
                .takeAsync(count)
                .thenApply(messages -> messages.stream()
                        .filter(getPredicate())
                        .collect(Collectors.toList()))
                .thenAccept(channel::purgeMessages);
    }

    @Override
    public void invoke(@NotNull SlashCommandInteractionEvent event) {
        // TODO: Allow guilds to specify whether replies to mod command invocations should be ephemeral or not
        event.deferReply().queue();
        InteractionHook hook = event.getInteraction().getHook();

        // get count
        OptionMapping countMapping = event.getOption("count");
        if(countMapping == null) {
            hook.editOriginal("`count` option cannot be empty").queue();
            return;
        }
        int count = countMapping.getAsInt();

        // get target channel
        TextChannel channel;
        OptionMapping channelMapping = event.getOption("channel");
        if(channelMapping == null)
            channel = event.getTextChannel();
        else {
            channel = channelMapping.getAsTextChannel();

            // somewhat redundant in the current configuration but best practice
            if(channel == null) {
                hook.editOriginal("`channel` option needs to be a text channel").queue();  // boring, but works
                return;
            }
        }

        // do purging
        clean(count, channel)
                .thenAccept(onComplete -> hook
                        .editOriginal(getResultMessage(count, channel))
                        .queue());
    }
}
