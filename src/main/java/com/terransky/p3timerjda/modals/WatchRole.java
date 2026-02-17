package com.terransky.p3timerjda.modals;

import com.terransky.p3timerjda.P3TimerJDA;
import com.terransky.p3timerjda.commands.AddRole;
import com.terransky.p3timerjda.utilities.command.*;
import com.terransky.p3timerjda.utilities.exceptions.DiscordAPIException;
import com.terransky.p3timerjda.utilities.exceptions.FailedInteractionException;
import com.terransky.p3timerjda.utilities.interfaces.interactions.ModalInteractionImpl;
import com.terransky.p3timerjda.utilities.interfaces.roles.RoleConfig;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.selections.SelectOption;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import net.dv8tion.jda.api.modals.Modal;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WatchRole extends ModalInteractionImpl<List<Role>> {

    private final String MODAL_ROLES = "roles-list";
    private final String MODAL_TIMEOUT = "timeout";
    private final String MODAL_TIMEOUT_LENGTH = "timeout-length";

    public WatchRole() {
        super("watch-role", "Role Watch");
    }

    @Override
    public Modal getContructedModal(@NotNull List<Role> roles) {
        return getBuilder()
            .addComponents(
                Label.of("Role", StringSelectMenu.create(MODAL_ROLES)
                    .addOptions(
                        new ArrayList<>() {{
                            roles.forEach(role -> add(SelectOption.of(role.getName(), role.getId())));
                        }}
                    )
                    .setDefaultOptions(
                        new ArrayList<>() {{
                            if (roles.size() == 1) {
                                Role role = roles.get(0);
                                add(SelectOption.of(role.getName(), role.getId()));
                            }
                        }}
                    )
                    .setRequired(true)
                    .build()
                ), Label.of("Timeout", TextInput.create(MODAL_TIMEOUT, TextInputStyle.SHORT)
                    .setRequired(true)
                    .build()
                ),
                Label.of("Interval", StringSelectMenu.create(MODAL_TIMEOUT_LENGTH)
                    .addOptions(
                        SelectOption.of("Seconds", "sec"),
                        SelectOption.of("Minutes", "min"),
                        SelectOption.of("Hours", "hr")
                    )
                    .setRequired(true)
                    .build()
                )
            )
            .build();
    }

    @Override
    public void execute(@NotNull ModalInteractionEvent event, EventBlob blob) throws FailedInteractionException, IOException {
        event.deferReply().setEphemeral(true).queue();
        Optional<ModalMapping> optionalRole = Optional.ofNullable(event.getValue(MODAL_ROLES));
        Optional<ModalMapping> optionalTimeout = Optional.ofNullable(event.getValue(MODAL_TIMEOUT));
        Optional<ModalMapping> optionalInterval = Optional.ofNullable(event.getValue(MODAL_TIMEOUT_LENGTH));
        String roleID = optionalRole.orElseThrow(DiscordAPIException::new).getAsStringList().get(0);
        String interval = optionalInterval.orElseThrow(DiscordAPIException::new).getAsStringList().get(0);
        int timeout;

        try {
            timeout = Integer.parseInt(optionalTimeout.orElseThrow(DiscordAPIException::new).getAsString());
        } catch (NumberFormatException e) {
            event.getHook().sendMessageComponents(StandardResponse.getResponseContainer("Timeout is not a number.", String.format("## Provided%n> %s", optionalTimeout.orElseThrow(DiscordAPIException::new).getAsString()), BotColors.ERROR))
                .queue();
            return;
        }

        if (timeout == 0) {
            event.getHook().sendMessageComponents(StandardResponse.getResponseContainer("Timeout cannot be 0.", String.format("## Provided%n> %s", optionalTimeout.orElseThrow(DiscordAPIException::new).getAsString()), BotColors.ERROR))
                .queue();
            return;
        }

        Role role = blob.getGuild().getRoleById(roleID);

        WatchedRole watchedRole = AddRole.getWatchedRole(event, blob, interval, timeout, role);
        if (watchedRole == null) return;

        if (P3TimerJDA.getRolesConfig().get().addRole(blob.getGuildIdLong(), watchedRole.watchedRole().getIdLong(), watchedRole.timeout())) {
            RoleConfig roleConfig = P3TimerJDA.getRolesConfig().get().getRole(blob.getGuildIdLong(), watchedRole.watchedRole().getIdLong()).orElseThrow(IOException::new);
            TimeData timeData = TimeData.getTimeData(roleConfig.getTimeout());
            event.getHook().sendMessageComponents(StandardResponse.getResponseContainer(P3TimerJDA.NAME,
                String.format("%s has already been added with a timeout of %s %s%s.",
                    watchedRole.watchedRole().getAsMention(),
                    timeData.timeout(),
                    timeData.getLengthType(),
                    timeData.timeout() > 1 ? "s" : "")
            )).setEphemeral(true).queue();
            return;
        }

        event.getHook().sendMessageComponents(StandardResponse.getResponseContainer(P3TimerJDA.NAME,
            String.format("%s added with a timeout of %s %s%s.",
                watchedRole.watchedRole().getAsMention(),
                watchedRole.baseTimeout(),
                watchedRole.getLengthType(),
                watchedRole.baseTimeout() > 1 ? "s" : ""
            )
        )).queue();
    }
}
