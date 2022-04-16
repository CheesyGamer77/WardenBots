package pw.cheesygamer77.wardenbots.commands.config;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;
import pw.cheesygamer77.cheedautilities.DiscordColor;
import pw.cheesygamer77.cheedautilities.commands.ChoiceUtil;
import pw.cheesygamer77.cheedautilities.commands.slash.Subcommand;
import pw.cheesygamer77.wardenbots.core.EmbedUtil;
import pw.cheesygamer77.wardenbots.core.moderation.ModLogChannel;
import pw.cheesygamer77.wardenbots.internal.db.DatabaseManager;

import java.util.HashMap;
import java.util.Locale;

public class ModLogSubcommand extends Subcommand {
    public ModLogSubcommand() {
        super(new SubcommandData("modlogs", "Mod Log configuration commands").addOptions(
                new OptionData(OptionType.STRING, "type", "The type of mod log channel to get or set")
                        .addChoices(ChoiceUtil.fromEnum(ModLogChannel.class)),
                new OptionData(OptionType.CHANNEL, "channel", "If specified, the channel to set for the mod log type")
                        .setChannelTypes(ChannelType.TEXT)
        ));
    }

    private @NotNull MessageEmbed getFullConfigurationEmbed(@NotNull Guild guild) {
        EmbedBuilder base = new EmbedBuilder()
                .setAuthor(guild.getName(), null, guild.getIconUrl())
                .setTitle("Logging Configuration")
                .setColor(DiscordColor.BLURPLE)
                .setFooter("Guild ID: " + guild.getId());

        HashMap<ModLogChannel, Long> channelMapping = DatabaseManager.fetchAllModLogChannels(guild);
        StringBuilder descriptionBuilder = new StringBuilder();
        for(ModLogChannel type : ModLogChannel.values()) {
            Long channelId = channelMapping.getOrDefault(type, null);
            descriptionBuilder
                    .append("• `").append(type.getTitle()).append("`: ")  // channel key
                    .append(channelId == null ? "[NOT SET]" : "<#" + channelId + "> (`" + channelId + "`)")  // channel mention + id
                    .append("\n");
        }

        return base.setDescription(descriptionBuilder.toString()).build();
    }

    private @NotNull MessageEmbed getChannelTypeConfigurationEmbed(@NotNull Guild guild, @NotNull ModLogChannel type) {
        TextChannel channel = type.fetch(guild);

        StringBuilder descriptionBuilder = new StringBuilder()
                .append("Mod Log Type `").append(type.getTitle()).append("` is ");

        if(channel == null)
            descriptionBuilder.append("[NOT SET]");
        else
            descriptionBuilder
                    .append("set to ").append(channel.getAsMention())  // channel mention
                    .append(" (channel id: `").append(channel.getId()).append("`)");  // channel id

        return new EmbedBuilder()
                .setAuthor(guild.getName(), null, guild.getIconUrl())
                .setTitle("Logging Configuration")
                .setDescription(descriptionBuilder.toString())
                .setColor(DiscordColor.BLURPLE)
                .build();
    }

    @Override
    public void invoke(@NotNull SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();

        if(guild == null) return;

        MessageEmbed embed;
        OptionMapping typeMapping = event.getOption("type");
        if(typeMapping == null)
            embed = getFullConfigurationEmbed(guild);
        else {
            ModLogChannel type = ModLogChannel.valueOf(
                    typeMapping.getAsString()
                            .toUpperCase(Locale.ROOT)
                            .replace(" ", "_")
            );

            OptionMapping channelMapping = event.getOption("channel");
            if(channelMapping == null)
                embed = getChannelTypeConfigurationEmbed(guild, type);
            else {
                // set modlog channel
                TextChannel channel = channelMapping.getAsTextChannel();
                if(channel != null) {
                    boolean success = DatabaseManager.setModLogChannel(type, channelMapping.getAsTextChannel(), guild);
                    if(success)
                        embed = EmbedUtil.getSuccess("Set `" + type.getTitle() + "` logging channel to " +
                                channel.getAsMention() + " (channel id: `" + channel.getId() + "`)"
                        );
                    else
                        embed = EmbedUtil.getFailure("Something went very wrong. Please contact my developer.");
                }
                else
                    embed = EmbedUtil.getFailure("Only Text Channels are allowed to be used as logging channels");
            }
        }

        event.replyEmbeds(embed).queue();
    }
}
