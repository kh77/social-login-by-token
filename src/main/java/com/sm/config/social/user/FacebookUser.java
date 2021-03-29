package com.sm.config.social.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FacebookUser extends GenericUser{

    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    Map<String, Object> attributes;
  //  private FacebookPicture picture;

    public FacebookUser(){}


    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
}