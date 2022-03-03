package pw.cheesygamer77.wardenbots.internal.layouts;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class UserChangesLayouts {
    public static @NotNull ActionRow getDefault() {
        return ActionRow.of(
                Button.secondary("options", "Options").asDisabled()
        );
    }

    public static class Nickname {
        public static class Options {
            @Contract(" -> new")
            public static @NotNull ActionRow getMenu() {
                return ActionRow.of(
                        Button.danger("clear", "Clear Nickname").asDisabled(),
                        Button.secondary("cancel", "Cancel").asDisabled()
                );
            }
        }
    }

    public static class RoleAdd {
        public static class Options {
            @Unmodifiable
            public static @NotNull List<ActionRow> getMenu(@NotNull Member target) {
                // set the base select menu
                SelectMenu.Builder builder = SelectMenu.create("roles")
                        .setPlaceholder("Remove Roles");

                // add the roles added to the menu as options
                // note that this list is truncated to the
                // maximum number of options allowed on a select menu
                List<Role> roles = target.getRoles().stream()
                        .sorted(Comparator.comparing(Role::getPosition))
                        .collect(Collectors.toList());
                int optionCount = Math.min(roles.size(), SelectMenu.OPTIONS_MAX_AMOUNT);
                for(Role role : roles.subList(0, optionCount)) {
                    String name = role.getName();
                    name = name.substring(0, Math.min(name.length(), SelectOption.LABEL_MAX_LENGTH));
                    builder.addOption(name, role.getId(),"Role ID: " + role.getId());
                }

                return List.of(
                        ActionRow.of(
                                builder
                                        .setRequiredRange(1, optionCount)
                                        .setDisabled(true)
                                        .build()
                        )
                );
            }
        }
    }
}
