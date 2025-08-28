package com.paf.exercise.exercise.authorization.domain;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KeycloakTokenIntrospectionResponse {
    private Boolean active;
}
