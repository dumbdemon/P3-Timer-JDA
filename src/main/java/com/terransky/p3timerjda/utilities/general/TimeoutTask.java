package com.terransky.p3timerjda.utilities.general;

import com.terransky.p3timerjda.P3TimerJDA;
import net.dv8tion.jda.api.entities.Role;
import org.slf4j.LoggerFactory;

import java.util.TimerTask;

public class TimeoutTask extends TimerTask {

    private final Role role;
    private final long serverID;

    public TimeoutTask(Role role, long serverID) {
        this.role = role;
        this.serverID = serverID;
    }

    @Override
    public void run() {
        role.getManager()
            .setMentionable(true)
            .queue(ignore -> {
                if (!P3TimerJDA.getRolesConfig().get().updateRole(serverID, role.getIdLong(), false)) {
                    LoggerFactory.getLogger(getClass()).error("Unable to update role settings.");
                }
            });
    }
}
