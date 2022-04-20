package pw.cheesygamer77.wardenbots.commands.moderation.role;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import org.jetbrains.annotations.NotNull;
import pw.cheesygamer77.wardenbots.commands.moderation.role.internal.RoleModifyingSubcommand;

public class RemoveSubcommand extends RoleModifyingSubcommand {
    public RemoveSubcommand() {
        super(new SubcommandData("remove", "Removes a role from a user"), "remove", "from");
    }

    @Override
    public AuditableRestAction<Void> doRoleChange(@NotNull Member member, @NotNull Role role) {
        return member.getGuild().removeRoleFromMember(member, role);
    }
}
