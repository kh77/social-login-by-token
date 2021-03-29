package com.sm.config.social.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IOSInfo extends GenericUser {
    public IOSInfo(){}
}
