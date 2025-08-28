package com.paf.exercise.exercise.authorization.domain;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginData {
    private String username;
    private String password;
    private Boolean adminLogin;
}
