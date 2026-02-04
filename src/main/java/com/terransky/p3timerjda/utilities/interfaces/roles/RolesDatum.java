package com.terransky.p3timerjda.utilities.interfaces.roles;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonPropertyOrder({
    "server_id",
    "reporting_channel_id",
    "role_data"
})
public class RolesDatum {

    @JsonProperty("server_id")
    private Long serverID;
    @JsonProperty("reporting_channel_id")
    private Long reportingChannelID;
    @JsonProperty("role_data")
    private List<RoleConfig> roleConfigs;

    @SuppressWarnings("unused")
    public RolesDatum() {
    }

    public RolesDatum(long serverID) {
        this.serverID = serverID;
        this.reportingChannelID = getDisabledChannel();
        this.roleConfigs = new ArrayList<>();
    }

    @JsonIgnore
    public static long getDisabledChannel() {
        return 0L;
    }

    @JsonProperty("server_id")
    public Long getServerID() {
        return serverID;
    }

    @JsonProperty("server_id")
    public void setServerID(Long serverID) {
        this.serverID = serverID;
    }

    @JsonProperty("reporting_channel_id")
    public Long getReportingChannelID() {
        return reportingChannelID;
    }

    @JsonProperty("reporting_channel_id")
    public void setReportingChannelID(Long reportingChannelID) {
        this.reportingChannelID = reportingChannelID;
    }

    @JsonProperty("role_data")
    public List<RoleConfig> getRoles() {
        return List.copyOf(roleConfigs);
    }

    @JsonProperty("role_data")
    public void setRoles(List<RoleConfig> roleConfigs) {
        this.roleConfigs = List.copyOf(roleConfigs);
    }

    @JsonIgnore
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RolesDatum rolesDatum)
            return Objects.equals(this.getServerID(), rolesDatum.getServerID());
        return false;
    }
}
