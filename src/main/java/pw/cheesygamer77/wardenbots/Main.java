package pw.cheesygamer77.wardenbots;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.data.DataObject;

import javax.security.auth.login.LoginException;
import java.io.InputStream;

public class Main {
    public static void main(String[] args) throws LoginException, InterruptedException {
        // load config json
        InputStream resource = Main.class.getResourceAsStream("/config.json");

        if (resource == null)
            throw new IllegalArgumentException("file is not found!");

        DataObject config = DataObject.fromJson(resource).getObject("warden");
        DataObject activityData = config.getObject("activity");

        // build JDA
        JDA jda = JDABuilder.createDefault(config.getString("token"))
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
                .build()
                .awaitReady();
    }
}