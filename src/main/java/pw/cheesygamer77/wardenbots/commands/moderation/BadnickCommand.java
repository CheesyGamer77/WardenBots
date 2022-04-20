package pw.cheesygamer77.wardenbots.commands.moderation;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import pw.cheesygamer77.wardenbots.core.EmbedUtil;
import pw.cheesygamer77.wardenbots.internal.commands.PermissionLockedSlashCommand;

import java.util.Objects;

public class BadnickCommand extends PermissionLockedSlashCommand {
    public BadnickCommand() {
        super(
                Commands.slash("badnick", "Sets a user's nickname to 'nickname'")
                        .addOption(
                                OptionType.USER,
                                "user",
                                "The user to set the nickname of",
                                true
                        ),
                Permission.NICKNAME_MANAGE
        );
        addPredicate(event -> event.getMember() != null && event.getMember().hasPermission(Permission.NICKNAME_MANAGE));
    }

    @Override
    public void invoke(@NotNull SlashCommandInteractionEvent event) {
        // get the target user (required)
        Member member = Objects.requireNonNull(event.getOption("user")).getAsMember();
        if(member == null) {
            // user is not a member of the guild
            event.replyEmbeds(EmbedUtil.getFailure("User is not from the current guild"))
                    .setEphemeral(true)
                    .queue();
            return;
        }

        if(Objects.requireNonNull(event.getGuild()).getSelfMember().canInteract(member)) {
            String newNickname = "nickname";

            member.modifyNickname("nickname")
                    .flatMap(success -> event.replyEmbeds(
                            EmbedUtil.getSuccess(
                                    "Set nickname of " + member.getAsMention() + " to `" + newNickname + "`"
                            )
                    ))
                    .onErrorFlatMap(fail -> event.replyEmbeds(
                            EmbedUtil.getFailure("Something went very wrong when setting that user's nickname")
                    ))
                    .queue();
        }
    }
}
