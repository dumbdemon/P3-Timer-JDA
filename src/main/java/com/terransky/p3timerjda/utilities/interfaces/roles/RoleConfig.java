package com.terransky.p3timerjda.utilities.interfaces.roles;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
    "role_id",
    "timeout",
    "start_time",
    "is_timeout"
})
public class RoleConfig {

    @JsonProperty("role_id")
    private Long roleID;
    @JsonProperty("timeout")
    private Long timeout;
    @JsonProperty("start_time")
    private Long startTime;
    @JsonProperty("is_timeout")
    private Boolean isTimeout;

    @SuppressWarnings("unused")
    public RoleConfig() {
    }

    public RoleConfig(Long roleID, Long timeout) {
        this(roleID, timeout, 0L, false);
    }

    public RoleConfig(Long roleID, Long timeout, Long startTime, Boolean isTimeout) {
        this.roleID = roleID;
        this.timeout = timeout;
        this.startTime = startTime;
        this.isTimeout = isTimeout;
    }

    @JsonProperty("role_id")
    public Long getRoleID() {
        return roleID;
    }

    @JsonProperty("role_id")
    public void setRoleID(Long roleID) {
        this.roleID = roleID;
    }

    @JsonProperty("timeout")
    public Long getTimeout() {
        return timeout;
    }

    @JsonProperty("timeout")
    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    @JsonProperty("is_timeout")
    public void setTimeout(Boolean timeout) {
        isTimeout = timeout;
    }

    @JsonProperty("start_time")
    public Long getStartTime() {
        return startTime;
    }

    @JsonProperty("start_time")
    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    @JsonProperty("is_timeout")
    public Boolean isTimeout() {
        return isTimeout;
    }
}
