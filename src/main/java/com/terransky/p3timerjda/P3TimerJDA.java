package com.terransky.p3timerjda;

import com.terransky.p3timerjda.utilities.general.Config;
import com.terransky.p3timerjda.utilities.general.ConfigHandler;
import com.terransky.p3timerjda.utilities.listeners.InteractionListener;
import com.terransky.p3timerjda.utilities.listeners.ListeningForEvents;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.messages.MessageRequest;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public class P3TimerJDA {

    public static final List<String> WATCH_LIST = new ArrayList<>() {{
        add("for Mentions");
        add("https://github.com/dumbdemon");
        add("https://github.com/guilhermesantos0");
    }};
    public static final String NAME;
    private static final ConfigHandler HANDLER;
    private static final String TOKEN;
    private static final AtomicReference<RolesConfig> rolesConfigAtomicReference;

    static {
        try {
            HANDLER = ConfigHandler.getInstance();
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Unable to load config", e);
        }
        TOKEN = getConfig().getToken();
        rolesConfigAtomicReference = new AtomicReference<>(new RolesConfig());
        NAME = getConfig().getName();
    }

    static void main() {
        if (TOKEN == null || TOKEN.isBlank())
            throw new IllegalArgumentException("Unable to start bot. No bot token was set.");

        DefaultShardManagerBuilder shards = DefaultShardManagerBuilder.createDefault(TOKEN)
            .enableIntents(
                GatewayIntent.MESSAGE_CONTENT
            );

        MessageRequest.setDefaultUseComponentsV2(true);
        Random random = new Random(new Date().getTime());
        shards.setActivity(Activity.watching(WATCH_LIST.get(random.nextInt(WATCH_LIST.size()))));
        ShardManager shardManager = shards.build();

        shardManager.addEventListener(
            new ListeningForEvents(),
            new InteractionListener()
        );
    }

    public static Config getConfig() {
        return HANDLER.getConfig();
    }

    public static AtomicReference<RolesConfig> getRolesConfig() {
        return rolesConfigAtomicReference;
    }
}
