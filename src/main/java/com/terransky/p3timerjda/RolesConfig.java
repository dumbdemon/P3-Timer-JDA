package com.terransky.p3timerjda;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.terransky.p3timerjda.utilities.command.StandardResponse;
import com.terransky.p3timerjda.utilities.general.TimeoutTask;
import com.terransky.p3timerjda.utilities.general.Timestamp;
import com.terransky.p3timerjda.utilities.interfaces.DatumPojo;
import com.terransky.p3timerjda.utilities.interfaces.roles.RoleConfig;
import com.terransky.p3timerjda.utilities.interfaces.roles.RolesDatum;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class RolesConfig {

    private final Path rolePath = Paths.get("roleConfigs.json");
    private final Logger log = LoggerFactory.getLogger(getClass());

    public RolesConfig() {
    }

    public boolean addServer(long serverID) {
        HashSet<RolesDatum> rolesData = new HashSet<>(getRolesDatum().toList());
        Optional<RolesDatum> optionalRolesDatum = rolesData.stream()
            .filter(rolesDatum -> rolesDatum.getServerID() == serverID)
            .findFirst();

        if (optionalRolesDatum.isPresent()) {
            return false;
        }

        rolesData.add(new RolesDatum(serverID));

        return saveConfig(rolesData);
    }

    public boolean removeServer(long serverID) {
        HashSet<RolesDatum> rolesData = new HashSet<>(getRolesDatum().toList());
        rolesData.stream()
            .filter(rolesDatum -> rolesDatum.getServerID() == serverID)
            .findFirst()
            .ifPresent(rolesData::remove);

        return saveConfig(rolesData);
    }

    private Optional<RoleConfig> getRole(long serverID, long roleID) {
        return getRolesDatum().filter(rolesDatum -> rolesDatum.getServerID() == serverID)
            .first()
            .flatMap(rolesDatum -> rolesDatum
                .getRoles()
                .stream().filter(roleConfig -> roleConfig.getRoleID() == roleID)
                .findFirst()
            );
    }

    public boolean addRole(long serverID, long roleID, long timeout) {
        List<RoleConfig> roleConfig = new ArrayList<>(getRolesDatum().toList()
            .stream().filter(rolesDatum -> rolesDatum.getServerID() == serverID)
            .findFirst()
            .orElse(new RolesDatum(serverID))
            .getRoles()
        );
        if (roleConfig.stream().anyMatch(config -> config.getRoleID() == roleID))
            return false;

        return saveRoleConfig(serverID, new RoleConfig(roleID, timeout));
    }

    public boolean updateRole(long serverID, long roleID, long timeout) {
        Optional<RoleConfig> optionalRoleConfig = getRole(serverID, roleID);
        if (optionalRoleConfig.isEmpty()) return false;
        RoleConfig roleConfig = optionalRoleConfig.get();

        if (roleConfig.getTimeout() == timeout)
            return false;
        else roleConfig.setTimeout(timeout);

        return saveRoleConfig(serverID, roleConfig);
    }

    public boolean updateRole(long serverID, long roleID, boolean isTimeout) {
        Optional<RoleConfig> optionalRoleConfig = getRole(serverID, roleID);
        if (optionalRoleConfig.isEmpty()) return false;
        RoleConfig roleConfig = optionalRoleConfig.get();

        if (roleConfig.isTimeout() == isTimeout)
            return false;
        else roleConfig.setTimeout(isTimeout);

        return saveRoleConfig(serverID, roleConfig);
    }

    public boolean setTimeout(@NotNull Guild guild, Role role) {
        Optional<RolesDatum> optionalRolesDatum = getRolesDatum().first(rolesDatum -> rolesDatum.getServerID() == guild.getIdLong());

        if (optionalRolesDatum.isEmpty())
            return false;

        RolesDatum rolesDatum = optionalRolesDatum.get();
        List<RoleConfig> roleConfigs = new ArrayList<>(rolesDatum.getRoles());
        Optional<RoleConfig> roleConfigOptional = roleConfigs.stream()
            .filter(roleConfig -> roleConfig.getRoleID() == role.getIdLong())
            .findFirst();

        if (roleConfigOptional.isEmpty()) {
            return false;
        }

        RoleConfig roleConfig = roleConfigOptional.get();

        LocalDateTime now = LocalDateTime.now();
        roleConfig.setTimeout(true);
        roleConfig.setStartTime(now.toEpochSecond(ZoneOffset.of("+0")));

        role.getManager().setMentionable(false).queue();
        new Timer().schedule(new TimeoutTask(role, guild.getIdLong()), TimeUnit.SECONDS.toMillis(roleConfig.getTimeout()));
        if (rolesDatum.getReportingChannelID() != RolesDatum.getDisabledChannel()) {
            TextChannel channel = guild.getChannelById(TextChannel.class, rolesDatum.getReportingChannelID());
            if (channel == null)
                return true;
            LocalDateTime expiresAt = now.plusSeconds(roleConfig.getTimeout());
            channel.sendMessageComponents(
                StandardResponse.getResponseContainer("Role Mentioned",
                    String.format("Time out for `%s` was set. Expires %s.",
                        role.getAsMention(),
                        Timestamp.getDateAsTimestamp(expiresAt.toEpochSecond(ZoneOffset.of("+0")), Timestamp.RELATIVE)
                    )
                )
            ).queue();
        }
        return saveRoleConfig(guild.getIdLong(), roleConfig);
    }

    public void verifyRoles(@NotNull Guild guild) throws InterruptedException {
        long serverID = guild.getIdLong();

        List<RoleConfig> rolesConfigs = new ArrayList<>(getRolesDatum().filter(datum -> datum.getServerID() == serverID)
            .first()
            .orElse(new RolesDatum(serverID))
            .getRoles()
        );

        for (RoleConfig roleConfig : rolesConfigs) {
            if (guild.getRoleById(roleConfig.getRoleID()) == null) {
                rolesConfigs.remove(roleConfig);
                continue;
            }

            if (roleConfig.isTimeout()) {
                LocalDateTime startTime = LocalDateTime.ofEpochSecond(roleConfig.getStartTime(), 0, ZoneOffset.of("+0"));
                LocalDateTime now = LocalDateTime.now();
                if (startTime.plusSeconds(roleConfig.getTimeout()).isBefore(now)) {
                    rolesConfigs.remove(roleConfig);
                    roleConfig.setTimeout(false);
                    rolesConfigs.add(roleConfig);
                } else {
                    long elapsedTime = now.toEpochSecond(ZoneOffset.of("+0")) - startTime.toEpochSecond(ZoneOffset.of("+0"));
                    new Timer().schedule(new TimeoutTask(guild.getRoleById(roleConfig.getRoleID()), serverID), TimeUnit.SECONDS.toMillis(roleConfig.getTimeout() - elapsedTime));
                }
            }
            Thread.sleep(200);
        }

        saveRoleConfig(serverID, rolesConfigs);
    }

    public boolean removeRole(long serverID, long roleID) {
        List<RoleConfig> rolesConfigs = new ArrayList<>(getRolesDatum().first(datum -> datum.getServerID() == serverID)
            .orElse(new RolesDatum(serverID))
            .getRoles()
        );
        rolesConfigs.stream()
            .filter(roleConfig -> roleConfig.getRoleID() == roleID)
            .findFirst()
            .ifPresent(rolesConfigs::remove);

        return saveRoleConfig(serverID, rolesConfigs);
    }

    public long getReportingChannel(long serverID) {
        Optional<RolesDatum> rolesDatum = getRolesDatum().first(datum -> datum.getServerID() == serverID);

        AtomicLong channelID = new AtomicLong(0L);

        rolesDatum.ifPresent(datum -> channelID.set(datum.getReportingChannelID()));

        return channelID.get();
    }

    public boolean setReportingChannel(long serverID, long channelID) {
        HashSet<RolesDatum> rolesData = new HashSet<>(getRolesDatum().toList());
        RolesDatum rolesDatum = rolesData.stream().filter(datum -> datum.getServerID() == serverID)
            .findFirst()
            .orElse(new RolesDatum(serverID));

        rolesData.remove(rolesDatum);
        rolesDatum.setReportingChannelID(channelID);
        rolesData.add(rolesDatum);

        return saveConfig(rolesData);
    }

    private boolean saveRoleConfig(long serverID, List<RoleConfig> roleConfigs) {
        List<RolesDatum> rolesData = new ArrayList<>(getRolesDatum().toList());

        RolesDatum rolesDatum = rolesData.stream().filter(roleData -> roleData.getServerID() == serverID)
            .findFirst()
            .orElse(new RolesDatum(serverID));
        rolesData.remove(rolesDatum);

        rolesDatum.setRoles(roleConfigs);
        rolesData.add(rolesDatum);

        return saveConfig(rolesData);
    }

    private boolean saveRoleConfig(long serverID, RoleConfig roleConfig) {
        List<RolesDatum> rolesData = new ArrayList<>(getRolesDatum().toList());

        RolesDatum rolesDatum = rolesData.stream().filter(roleData -> roleData.getServerID() == serverID)
            .findFirst()
            .orElse(new RolesDatum(serverID));
        rolesData.remove(rolesDatum);

        List<RoleConfig> roleConfigs = new ArrayList<>(rolesDatum.getRoles());

        roleConfigs.stream().filter(config -> Objects.equals(config.getRoleID(), roleConfig.getRoleID()))
            .findFirst()
            .ifPresent(roleConfigs::remove);

        roleConfigs.add(roleConfig);

        rolesDatum.setRoles(roleConfigs);
        rolesData.add(rolesDatum);

        return saveConfig(rolesData);
    }

    @NotNull
    @Contract(" -> new")
    private DatumPojo<RolesDatum> getRolesDatum() {
        try (FileInputStream rolesStream = new FileInputStream(rolePath.toFile())) {
            return new DatumPojo<>(getObjectMapper().readValue(rolesStream, new TypeReference<>() {
            }));
        } catch (IOException ignore) {
            log.warn("No Roles Config. Generating one...");
            return new DatumPojo<>(List.of());
        }
    }

    private boolean saveConfig(@NotNull Collection<RolesDatum> rolesDatum) {
        try {
            new DatumPojo<>(rolesDatum).saveAsJsonFile(rolePath.toFile());
            return true;
        } catch (IOException e) {
            log.error("Unable to save roles config.", e);
            return false;
        }
    }

    @NotNull
    @Contract(" -> new")
    private ObjectMapper getObjectMapper() {
        return new ObjectMapper(
            JsonFactory.builder()
                .enable(StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION)
                .build()
        );
    }
}
