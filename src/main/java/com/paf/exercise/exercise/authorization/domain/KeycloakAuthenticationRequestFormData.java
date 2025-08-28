package com.paf.exercise.exercise.authorization.domain;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KeycloakAuthenticationRequestFormData {
    private String grantType;
    private String username;
    private String password;
    private String scope;
}


