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

    public boolean addRole(long serverID, long roleID, long length) {
        HashSet<RolesDatum> rolesData = new HashSet<>(getRolesDatum().toList());
        Optional<RolesDatum> optionalRolesDatum = rolesData.stream().filter(data -> data.getServerID() == serverID)
            .findFirst();
        RolesDatum rolesDatum = optionalRolesDatum.orElseGet(() -> new RolesDatum(serverID));

        rolesData.remove(rolesDatum);
        List<RoleConfig> roleConfig = new ArrayList<>(rolesDatum.getRoles());
        if (roleConfig.stream().anyMatch(config -> config.getRoleID() == roleID))
            return false;

        roleConfig.add(new RoleConfig(roleID, length));
        rolesDatum.setRoles(roleConfig);
        rolesData.add(rolesDatum);

        return saveConfig(rolesData);
    }

    public boolean updateRole(long serverID, long roleID, long timeout) {
        Optional<RoleConfig> optionalRoleConfig = getRole(serverID, roleID);
        if (optionalRoleConfig.isEmpty()) return false;
        RoleConfig roleConfig = optionalRoleConfig.get();

        if (roleConfig.getTimeout() == timeout)
            return false;
        else roleConfig.setTimeout(timeout);

        return updateRole(serverID, roleConfig);
    }

    public boolean updateRole(long serverID, long roleID, boolean isTimeout) {
        Optional<RoleConfig> optionalRoleConfig = getRole(serverID, roleID);
        if (optionalRoleConfig.isEmpty()) return false;
        RoleConfig roleConfig = optionalRoleConfig.get();

        if (roleConfig.isTimeout() == isTimeout)
            return false;
        else roleConfig.setTimeout(isTimeout);

        return updateRole(serverID, null);
    }

    public boolean updateRole(long serverID, RoleConfig roleConfig) {
        HashSet<RolesDatum> rolesData = new HashSet<>(getRolesDatum().toList());
        Optional<RolesDatum> optionalRolesDatum = rolesData.stream().filter(data -> data.getServerID() == serverID)
            .findFirst();

        if (optionalRolesDatum.isEmpty())
            return false;

        RolesDatum newRolesDatum = optionalRolesDatum.get();
        rolesData.remove(optionalRolesDatum.get());
        List<RoleConfig> roles = new ArrayList<>(newRolesDatum.getRoles());
        roles.stream().filter(oldRoleConfig -> roleConfig.getRoleID().equals(oldRoleConfig.getRoleID()))
            .findFirst()
            .ifPresent(roles::remove);

        roles.add(roleConfig);
        newRolesDatum.setRoles(roles);
        rolesData.add(newRolesDatum);

        return saveConfig(rolesData);
    }

    public boolean setTimeout(@NotNull Guild guild, Role role) {
        HashSet<RolesDatum> rolesData = new HashSet<>(getRolesDatum().toList());
        Optional<RolesDatum> optionalRolesDatum = rolesData.stream().filter(rolesDatum -> rolesDatum.getServerID() == guild.getIdLong())
            .findFirst();

        if (optionalRolesDatum.isEmpty())
            return false;

        RolesDatum rolesDatum = optionalRolesDatum.get();
        rolesData.remove(rolesDatum);
        List<RoleConfig> roleConfigs = new ArrayList<>(rolesDatum.getRoles());
        Optional<RoleConfig> roleConfigOptional = roleConfigs.stream()
            .filter(roleConfig -> roleConfig.getRoleID() == role.getIdLong())
            .findFirst();

        if (roleConfigOptional.isEmpty()) {
            return false;
        }

        RoleConfig roleConfig = roleConfigOptional.get();
        roleConfigs.remove(roleConfig);

        LocalDateTime now = LocalDateTime.now();
        roleConfig.setTimeout(true);
        roleConfig.setStartTime(now.toEpochSecond(ZoneOffset.of("+0")));

        roleConfigs.add(roleConfig);
        rolesDatum.setRoles(roleConfigs);
        rolesData.add(rolesDatum);

        saveConfig(rolesData);
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
        return true;
    }

    public void verifyRoles(@NotNull Guild guild) throws InterruptedException {
        long serverID = guild.getIdLong();
        RolesDatum rolesDatum = getRolesDatum().filter(datum -> datum.getServerID() == serverID)
            .first()
            .orElse(new RolesDatum(serverID));

        int rolesModified = 0;
        for (RoleConfig role : rolesDatum.getRoles()) {
            if (guild.getRoleById(role.getRoleID()) == null) {
                rolesDatum.getRoles().remove(role);
                rolesModified++;
                continue;
            }

            if (role.isTimeout()) {
                LocalDateTime startTime = LocalDateTime.ofEpochSecond(role.getStartTime(), 0, ZoneOffset.of("+0"));
                LocalDateTime now = LocalDateTime.now();
                if (startTime.plusSeconds(role.getTimeout()).isBefore(now)) {
                    rolesDatum.getRoles().remove(role);
                    role.setTimeout(false);
                    rolesDatum.getRoles().add(role);
                    rolesModified++;
                } else {
                    long elapsedTime = now.toEpochSecond(ZoneOffset.of("+0")) - startTime.toEpochSecond(ZoneOffset.of("+0"));
                    new Timer().schedule(new TimeoutTask(guild.getRoleById(role.getRoleID()), serverID), TimeUnit.SECONDS.toMillis(role.getTimeout() - elapsedTime));
                }
            }
            Thread.sleep(200);
        }

        if (rolesModified > 0) {
            List<RolesDatum> rolesData = new ArrayList<>(getRolesDatum().toList());
            rolesData.stream().filter(datum -> datum.getServerID() == serverID)
                .findFirst()
                .ifPresent(rolesData::remove);
            rolesData.add(rolesDatum);
            saveConfig(rolesData);
        }
    }

    public boolean removeRole(long serverID, long roleID) {
        HashSet<RolesDatum> rolesData = new HashSet<>(getRolesDatum().toList());
        RolesDatum rolesDatum = rolesData.stream().filter(datum -> datum.getServerID() == serverID)
            .findFirst()
            .orElse(new RolesDatum(serverID));
        rolesData.remove(rolesDatum);

        List<RoleConfig> rolesConfigs = rolesDatum.getRoles();
        rolesConfigs.stream()
            .filter(roleConfig -> roleConfig.getRoleID() == roleID)
            .findFirst()
            .ifPresent(rolesConfigs::remove);
        rolesData.add(rolesDatum);

        return saveConfig(rolesData);
    }

    public long getReportingChannel(long serverID) {
        Optional<RolesDatum> rolesDatum = getRolesDatum().filter(datum -> datum.getServerID() == serverID)
            .first();

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
