package pw.cheesygamer77.wardenbots.commands.role;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.TimeFormat;
import org.jetbrains.annotations.NotNull;
import pw.cheesygamer77.cheedautilities.commands.slash.Subcommand;

import java.awt.*;
import java.util.Objects;

public class InfoSubcommand extends Subcommand {
    public InfoSubcommand() {
        super(new SubcommandData("info", "Returns information about a role")
                .addOption(OptionType.ROLE, "role", "The role to retrieve information for", true)
        );
    }

    @Override
    public void invoke(@NotNull SlashCommandInteractionEvent event) {
        if(event.getGuild() == null || event.getMember() == null) return;

        Role role = Objects.requireNonNull(event.getOption("role")).getAsRole();

        // get role color for later
        // default to black if not specified
        Color color = role.getColor();
        if(color == null) color = Color.BLACK;

        // Color#getRGB() very annoyingly sets the leftmost 8 bits to the alpha value, so we truncate it here
        int colorValue = color.getRGB() & 0x00FFFFFF;

        MessageBuilder base = new MessageBuilder()
                .setContent(role.getId())
                .setEmbeds(
                        new EmbedBuilder()
                                .setTitle("Role Info")
                                .setColor(color)
                                .addField("Name", role.getName(), false)
                                .addField("Mention", "`" + role.getAsMention() + "`", false)
                                .addField(
                                        "Created",
                                        TimeFormat.DATE_TIME_SHORT.format(role.getTimeCreated()),
                                        false
                                )
                                .addField(
                                        "Color",
                                        "Hex: `#" + Integer.toHexString(colorValue) + "`\n" +
                                                "RGB: `(" + color.getRed() + ", " + color.getGreen() + ", " + color.getBlue() + ")`",
                                        true
                                )
                                .addField(
                                        "Total Members",
                                        String.valueOf(role.getGuild().getMembersWithRoles(role).size()),
                                        true
                                )
                                .setFooter("Role ID: " + role.getId())
                                .build()
                );

        if(event.getMember().hasPermission(Permission.MANAGE_ROLES))
            base.setActionRows(ActionRow.of(
                    Button.primary("role-info:view-members:" + role.getId(), "View Role Members")).asDisabled());

        event.reply(base.build()).queue();
    }
}
