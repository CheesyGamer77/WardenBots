package pw.cheesygamer77.wardenbots.commands.moderation.role.internal;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import org.jetbrains.annotations.NotNull;
import pw.cheesygamer77.cheedautilities.DiscordColor;
import pw.cheesygamer77.cheedautilities.commands.slash.Subcommand;

import java.util.Objects;

public abstract class RoleModifyingSubcommand extends Subcommand {
    protected final String verb;
    protected final String preposition;

    public RoleModifyingSubcommand(@NotNull SubcommandData data, @NotNull String verb, @NotNull String preposition) {
        super(data
                .addOption(OptionType.ROLE, "role", "The role to "+ verb + " to the user", true)
                .addOption(OptionType.USER, "user", "The user to " + verb + " the role " + preposition, true)
                .addOption(OptionType.STRING, "reason", "Reason behind " + verb + "ing the role")
        );

        this.verb = verb;
        this.preposition = preposition;
    }

    public abstract AuditableRestAction<Void> doRoleChange(@NotNull Member member, @NotNull Role role);

    @Override
    public final void invoke(@NotNull SlashCommandInteractionEvent event) {
        // TODO: Allow guilds to specify whether replies to mod command invocations should be ephemeral or not
        if(event.getGuild() == null) return;

        event.deferReply().queue();

        // get target role
        Role role = Objects.requireNonNull(event.getOption("role")).getAsRole();

        // get target member
        User targetUser = Objects.requireNonNull(event.getOption("user")).getAsUser();
        Member target = event.getGuild().getMember(targetUser);
        if(target == null) {
            return;
        }

        // do the role change then send message
        doRoleChange(target, role)
                .reason(event.getOption("reason", map -> map == null ? null : map.getAsString()))
                .flatMap(success -> event.getHook().editOriginalEmbeds(
                        new EmbedBuilder()
                                .setDescription(":white_check_mark: Changed roles for " + target.getAsMention())
                                .setColor(DiscordColor.BRAND_GREEN)
                                .build()
                ))
                .queue();
    }
}
