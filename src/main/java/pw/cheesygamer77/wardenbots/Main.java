package pw.cheesygamer77.wardenbots;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.reflections.Reflections;
import org.slf4j.LoggerFactory;
import pw.cheesygamer77.cheedautilities.commands.CommandListener;
import pw.cheesygamer77.wardenbots.commands.BanCommand;
import pw.cheesygamer77.wardenbots.commands.UnbanCommand;
import pw.cheesygamer77.wardenbots.commands.WhoisCommand;
import pw.cheesygamer77.wardenbots.commands.clean.CleanCommands;
import pw.cheesygamer77.wardenbots.commands.config.ConfigCommands;
import pw.cheesygamer77.wardenbots.commands.role.RoleCommands;

import javax.security.auth.login.LoginException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws LoginException, InterruptedException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        // load config json
        InputStream resource = Main.class.getResourceAsStream("/config.json");

        if (resource == null)
            throw new IllegalArgumentException("file is not found!");

        // gather all listeners
        List<Object> out = new ArrayList<>();
        Reflections reflections = new Reflections("pw.cheesygamer77.wardenbots.listeners");
        Set<Class<? extends ListenerAdapter>> adapters = reflections.getSubTypesOf(ListenerAdapter.class);
        for(Class<? extends ListenerAdapter> adapter : adapters) {
            out.add(adapter.getDeclaredConstructor().newInstance());
            LoggerFactory.getLogger(adapter).debug("Successfully loaded");
        }

        // retrieve config
        DataObject config = DataObject.fromJson(resource).getObject("warden");
        DataObject activityData = config.getObject("activity");

        // build command listener
        CommandListener commandListener = new CommandListener();
        commandListener.addCommand(new WhoisCommand());
        commandListener.addCommand(new CleanCommands());
        commandListener.addCommand(new BanCommand());
        commandListener.addCommand(new UnbanCommand());
        commandListener.addCommand(new RoleCommands());
        commandListener.addCommand(new ConfigCommands());

        // build JDA
        JDABuilder.createDefault(config.getString("token"))
                .setChunkingFilter(ChunkingFilter.ALL)
                .setStatus(OnlineStatus.valueOf(config.getString("status")))
                .setActivity(
                        Activity.of(
                                Activity.ActivityType.valueOf(activityData.getString("type")),
                                activityData.getString("name")
                        )
                )
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableIntents(
                        GatewayIntent.GUILD_EMOJIS,
                        GatewayIntent.GUILD_VOICE_STATES,
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_BANS)
                .addEventListeners(
                        out.toArray()
                )
                .addEventListeners(
                        commandListener
                )
                .build()
                .awaitReady()
                .setRequiredScopes("applications.commands");
    }
}
