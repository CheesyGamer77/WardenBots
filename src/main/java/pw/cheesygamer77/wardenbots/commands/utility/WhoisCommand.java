package pw.cheesygamer77.wardenbots.commands.utility;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import pw.cheesygamer77.cheedautilities.StringUtil;
import pw.cheesygamer77.cheedautilities.commands.slash.SlashCommand;
import pw.cheesygamer77.wardenbots.internal.TimeUtil;

import java.time.Instant;
import java.util.Comparator;
import java.util.stream.Collectors;

public class WhoisCommand extends SlashCommand {
    public WhoisCommand() {
        super(
                Commands.slash("whois", "Returns information about a user")
                        .addOption(OptionType.USER, "user", "The user to get information about")
        );
    }

    private static @NotNull String getUserFlagEmojiString(@NotNull User.UserFlag flag) {
        switch(flag) {
            case BUG_HUNTER_LEVEL_1:
                return "<:bug_hunter1:848324246363045920>";
            case BUG_HUNTER_LEVEL_2:
                return "<:bug_hunter2:848324259202072590>";
            case CERTIFIED_MODERATOR:
                return "<:certified_moderator:848324273164779520>";
            case EARLY_SUPPORTER:
                return "<:early_supporter:848344299976523797>";
            case HYPESQUAD:
                return "<:hypesquad_events:848324337055301683>";
            case HYPESQUAD_BALANCE:
                return "<:balance:848324174145650689>";
            case HYPESQUAD_BRAVERY:
                return "<:bravery:848324228725211217>";
            case HYPESQUAD_BRILLIANCE:
                return "<:brilliance:848324236291342348>";
            case PARTNER:
                return "<:partner:848324397164134400>";
            case STAFF:
                return "<:staff:848324432295624764>";
            case VERIFIED_DEVELOPER:
                return "<:bot_developer:848342709877604372>";
            default:
                return "";
        }
    }

    private static @NotNull EmbedBuilder getBaseUserInfoBuilder(@NotNull User target) {
        // setup base
        EmbedBuilder base = new EmbedBuilder()
                .setAuthor(target.getAsTag(), null, target.getEffectiveAvatarUrl())
                .setTitle("User Info")
                .setFooter("User ID: " + target.getId())
                .setTimestamp(Instant.now());

        // add user flags value
        StringBuilder userFlagsValueBuilder = new StringBuilder();
        target.getFlags().stream()
                .sorted(Comparator.comparing(User.UserFlag::getName))
                .filter(flag -> !getUserFlagEmojiString(flag).isEmpty())
                .collect(Collectors.toList())
                .forEach(flag -> userFlagsValueBuilder
                        .append("• ")
                        .append(getUserFlagEmojiString(flag))
                        .append(" ")
                        .append(StringUtil.toTitleCase(flag.getName()))
                        .append("\n")
                );

        String userFlagsValue = userFlagsValueBuilder.toString().strip();
        if(!userFlagsValue.isEmpty())
            base.addField("User Flags", userFlagsValue, false);
        else
            base.addField("User Flags", "[N/A]", false);

        return base;
    }

    @Override
    public void invoke(@NotNull SlashCommandInteractionEvent event) {
        OptionMapping userMapping = event.getOption("user");

        // set target user (the command author if no user specified)
        User target;
        if(userMapping != null)
            // target is the argument provided
            target = userMapping.getAsUser();
        else
            // target is the current author
            target = event.getUser();

        MessageBuilder baseMsg = new MessageBuilder().setContent(target.getId());
        EmbedBuilder baseEmbed = getBaseUserInfoBuilder(target);

        // get user guild join status
        // we don't do this for `/whois` commands ran in dm's cause there's no reason to
        Guild guild = event.getGuild();
        if(guild != null) {
            MessageEmbed embed;

            // is the target a member of the current guild?
            Member member = guild.getMember(target);
            RestAction<?> action = null;
            if(member != null) {
                baseEmbed.setColor(member.getColor());

                StringBuilder statusBase = new StringBuilder("Joined " + TimeUtil.getTimeDescription(member.getTimeJoined()));
                // is the member timed out?
                if (member.isTimedOut()) {
                    statusBase.insert(0, "TIMED OUT\n");
                }

                embed = baseEmbed
                        .addField("Status", statusBase.toString(), false)
                        .build();
            } else {
                // target is not a member of the guild

                // TODO: get ban status
                /*
                action = guild.retrieveBan(target)
                        .flatMap(ban -> baseEmbed.addField("Status", "BANNED", false))
                        .onErrorFlatMap(error -> baseEmbed.addField("Status", "Not Joined", false));
                */
                baseEmbed.addField("Status", "Not Joined", false);
                embed = baseEmbed.build();
            }

            if(action == null) action = event.reply(baseMsg.setEmbeds(embed).build());
            action.queue();
        }
        else {
            event.reply(baseMsg.setEmbeds(baseEmbed.build()).build()).queue();
        }
    }
}