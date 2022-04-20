package pw.cheesygamer77.wardenbots.commands.moderation.role;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import org.jetbrains.annotations.NotNull;
import pw.cheesygamer77.wardenbots.commands.moderation.role.internal.RoleModifyingSubcommand;

public class AddSubcommand extends RoleModifyingSubcommand {
    public AddSubcommand() {
        super(new SubcommandData("add", "Adds a role to a user"), "add", "to");
    }

    @Override
    public AuditableRestAction<Void> doRoleChange(@NotNull Member member, @NotNull Role role) {
        return member.getGuild().addRoleToMember(member, role);
    }
}
