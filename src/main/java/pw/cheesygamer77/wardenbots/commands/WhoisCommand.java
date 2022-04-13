package pw.cheesygamer77.wardenbots.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.utils.TimeFormat;
import org.jetbrains.annotations.NotNull;
import pw.cheesygamer77.cheedautilities.commands.slash.SlashCommand;

import java.time.Instant;
import java.time.temporal.TemporalAccessor;

public class WhoisCommand extends SlashCommand {
    public WhoisCommand() {
        super(
                Commands.slash("whois", "Returns information about a user")
                        .addOption(OptionType.USER, "user", "The user to get information about")
        );
    }

    /**
     * Returns the {@link TimeFormat#DATE_TIME_SHORT}, followed by the {@link TimeFormat#RELATIVE} parenthesized
     * @return The string containing the above
     */
    private static @NotNull String getTimeDescription(@NotNull TemporalAccessor accessor) {
        return TimeFormat.DATE_TIME_SHORT.format(accessor) + " (" + TimeFormat.RELATIVE.format(accessor) + ")";
    }

    private static @NotNull EmbedBuilder getBaseUserInfoBuilder(@NotNull IMentionable target) {
        return new EmbedBuilder()
                .setTitle("User Info")
                .setDescription(target.getAsMention() + " has the following information")
                .setFooter("User ID: " + target.getId())
                .setTimestamp(Instant.now());
    }

    private static @NotNull MessageEmbed getUserInfoEmbed(@NotNull Member member) {
        return getBaseUserInfoBuilder(member)
                .setAuthor(member.getUser().getAsTag(), null, member.getEffectiveAvatarUrl())
                .setColor(member.getColor())
                .build();
    }

    private static @NotNull MessageEmbed getUserInfoEmbed(@NotNull User user) {
        return getBaseUserInfoBuilder(user)
                .setAuthor(user.getAsTag(), null, user.getEffectiveAvatarUrl())
                .addField(
                        "Account Created",
                        getTimeDescription(user.getTimeCreated()), false)
                .build();
    }

    @Override
    public void invoke(@NotNull SlashCommandInteractionEvent event) {
        OptionMapping userMapping = event.getOption("user");

        // set target user (the command author if no user specified)
        User target;
        if(userMapping != null)
            target = userMapping.getAsUser();
        else
            target = event.getUser();

        event.reply(new MessageBuilder()
                .setContent(target.getId())
                .setEmbeds(getUserInfoEmbed(target))
                .build()
        ).queue();
    }
}

