package com.paf.exercise.exercise.authorization.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "paf.auth")
public class PafAuthenticationConfigurationProperties {
    private String clientId;
    private String clientSecret;
    private String issuerUri;
    private String adminRoleName;
    private String pafSessionCookieName;
    private String tokenIssuanceUri;
    private String logoutUri;
    private String introspectUri;
    private String userIdHeaderName;
    private String isAdminHeaderName;
    private List<String> securedPaths;
}


