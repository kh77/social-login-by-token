package com.sm.config.social.ios;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IOSConfig {

    @Value("${ios.client_id}")
    private String clientId;

    @Value("${ios.team_id}")
    private String teamId;

    @Value("${ios.kid}")
    private String kid;

    @Value("${ios.file_name}")
    private String fileName;

    public String getClientId() {
        return clientId;
    }

    public String getTeamId() {
        return teamId;
    }

    public String getKid() {
        return kid;
    }

    public String getFileName() {
        return fileName;
    }
}
