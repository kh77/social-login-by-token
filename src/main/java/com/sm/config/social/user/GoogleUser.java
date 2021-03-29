package com.sm.config.social.user;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GoogleUser extends GenericUser{
    @JsonProperty("sub")
    private String id;
//    @JsonProperty("given_name")
//    private String givenName;
    private String name;
//    @JsonProperty("family_name")
//    private String familyName;
    private String rawUserInfo;
    private String picture;

    @JsonProperty("given_name")
    private String firstName;
    @JsonProperty("family_name")
    private String lastName;

    public GoogleUser(){}



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public String getRawUserInfo() {
        return rawUserInfo;
    }

    public void setRawUserInfo(String rawUserInfo) {
        this.rawUserInfo = rawUserInfo;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}