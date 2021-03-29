package com.sm.config.social.ios;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DecodeResponse {
    public String iss;
    public String aud;
    public int exp;
    public int iat;
    public String sub;
    public String at_hash;
    public String email;
    public String email_verified;
    public int auth_time;
    public boolean nonce_supported;

    public DecodeResponse(){}

    public String getIss() {
        return iss;
    }

    public void setIss(String iss) {
        this.iss = iss;
    }

    public String getAud() {
        return aud;
    }

    public void setAud(String aud) {
        this.aud = aud;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getIat() {
        return iat;
    }

    public void setIat(int iat) {
        this.iat = iat;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getAt_hash() {
        return at_hash;
    }

    public void setAt_hash(String at_hash) {
        this.at_hash = at_hash;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail_verified() {
        return email_verified;
    }

    public void setEmail_verified(String email_verified) {
        this.email_verified = email_verified;
    }

    public int getAuth_time() {
        return auth_time;
    }

    public void setAuth_time(int auth_time) {
        this.auth_time = auth_time;
    }

    public boolean isNonce_supported() {
        return nonce_supported;
    }

    public void setNonce_supported(boolean nonce_supported) {
        this.nonce_supported = nonce_supported;
    }
}
