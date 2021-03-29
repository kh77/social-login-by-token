package com.sm.config.social.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LinkedinInfo extends GenericUser {

    @JsonProperty("localizedFirstName")
    private String firstName;
    @JsonProperty("localizedLastName")
    private String lastName;

    public LinkedinInfo(){}
}
