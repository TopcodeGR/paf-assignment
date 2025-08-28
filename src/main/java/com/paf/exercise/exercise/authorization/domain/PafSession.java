package com.paf.exercise.exercise.authorization.domain;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "paf_session")
public class PafSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @Column(name = "session_id")
    private String sessionId;

    @NotNull
    @Column(name = "access_token")
    private String accessToken;

    @NotNull
    @Column(name = "refresh_token")
    private String refreshToken;

    @NotNull
    @Column(name = "keycloak_session")
    private String keycloakSession;

    @NotNull
    @Column(name = "id_token")
    private String idToken;

    @NotNull
    @Column(name = "user_id")
    private String userId;

    @NotNull
    @Column(name = "is_admin")
    private Boolean isAdmin;
}
