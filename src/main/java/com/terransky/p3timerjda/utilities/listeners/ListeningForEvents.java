package com.terransky.p3timerjda.utilities.listeners;

import com.terransky.p3timerjda.Managers;
import com.terransky.p3timerjda.P3TimerJDA;
import com.terransky.p3timerjda.utilities.command.BotColors;
import com.terransky.p3timerjda.utilities.command.StandardResponse;
import com.terransky.p3timerjda.utilities.exceptions.DiscordAPIException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.session.SessionDisconnectEvent;
import net.dv8tion.jda.api.events.session.SessionInvalidateEvent;
import net.dv8tion.jda.api.events.session.SessionResumeEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.CloseCode;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class ListeningForEvents extends ListenerAdapter {
    private final Logger log = LoggerFactory.getLogger(ListeningForEvents.class);

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        JDA jda = event.getJDA();

        jda.updateCommands()
            .addCommands((new Managers.SlashCommands()).getCommandData())
            .addCommands((new Managers.UserContextMenu()).getCommandData())
            .addCommands((new Managers.MessageContextMenu()).getCommandData())
            .queue(commands -> log.info("{} global commands loaded!", commands.size()), DiscordAPIException::new);

        createInviteLinkFile(jda.getInviteUrl(
            Permission.MANAGE_ROLES,
            Permission.MESSAGE_SEND
        ));

        long timer = TimeUnit.MINUTES.toMillis(10);
        new Timer().scheduleAtFixedRate(new SetWatcherTask(jda.getShardManager()), timer, timer);
    }

    @Override
    public void onSessionDisconnect(@NotNull SessionDisconnectEvent event) {
        CloseCode closeCode = event.getCloseCode();
        log.warn(closeCode == null ? "Disconnected from Discord" : closeCode.getMeaning());
    }

    @Override
    public void onSessionResume(@NotNull SessionResumeEvent event) {
        log.warn("Reconnected to Discord.");
    }

    @Override
    public void onSessionInvalidate(@NotNull SessionInvalidateEvent event) {
        log.warn("Discord invalidated session.");
    }

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        long serverID = event.getGuild().getIdLong();
        if (P3TimerJDA.getRolesConfig().get().addServer(serverID))
            log.info("Server `{}` Added to Configs", serverID);
        else log.warn("Failed to add server `{}`.", serverID);
    }

    @Override
    public void onGuildLeave(@NotNull GuildLeaveEvent event) {
        long serverID = event.getGuild().getIdLong();
        if (P3TimerJDA.getRolesConfig().get().removeServer(serverID))
            log.info("Removed server `{}` from config", serverID);
        else log.warn("Unable to remove server.");
    }

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        try {
            P3TimerJDA.getRolesConfig().get().verifyRoles(event.getGuild());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getMember() == null ||
            event.getAuthor().isBot() ||
            event.getMember().hasPermission(Permission.MESSAGE_MENTION_EVERYONE) ||
            event.getMessage().getMentions().getRoles().isEmpty()
        )
            return;

        List<Role> roles = event.getMessage().getMentions().getRoles();
        for (Role role : roles) {
            if (P3TimerJDA.getRolesConfig().get().setTimeout(event.getGuild(), role)) {
                TextChannel channel = event.getGuild()
                    .getChannelById(TextChannel.class, P3TimerJDA.getRolesConfig().get().getReportingChannel(event.getGuild().getIdLong()));
                if (channel != null)
                    channel.sendMessageComponents(StandardResponse.getResponseContainer(P3TimerJDA.NAME,
                        String.format("%s went on timeout.", role.getAsMention()),
                        BotColors.SUB_DEFAULT
                    )).queue();
            }
        }
    }

    private void createInviteLinkFile(@NotNull String inviteUrl) {
        File inviteLink = new File("inviteLink.txt");

        if (!inviteLink.exists()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(inviteLink))) {
                writer.write(inviteUrl);
                log.info("Invite link created at \"{}\"", inviteLink.getAbsolutePath());
            } catch (IOException e) {
                log.error("Unable to create invite link file.", e);
            }
        } else {
            try (BufferedReader reader = new BufferedReader(new FileReader(inviteLink))) {
                if (inviteUrl.equals(reader.readLine())) {
                    log.info("Invite link exists at \"{}\". Moving on.", inviteLink.getAbsolutePath());
                } else try (BufferedWriter writer = new BufferedWriter(new FileWriter(inviteLink))) {
                    writer.write(inviteUrl);
                    log.info("Invite Link file has been modified at \"{}\"", inviteLink.getAbsolutePath());
                }
            } catch (IOException e) {
                log.error("Unable to read invite link file.", e);
            }
        }
    }

    public static class SetWatcherTask extends TimerTask {

        private final ShardManager shardManager;
        private final List<String> watchList = P3TimerJDA.WATCH_LIST;

        public SetWatcherTask(ShardManager shardManager) {
            this.shardManager = shardManager;
        }

        @Override
        public void run() {
            Random random = new Random(new Date().getTime());
            shardManager.setActivity(Activity.watching(watchList.get(random.nextInt(watchList.size()))));
        }
    }
}
