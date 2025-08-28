package com.paf.exercise.exercise.web.exception.error;

import org.springframework.http.HttpStatus;

public class UserIsNotAdmin extends  PafError {
    public UserIsNotAdmin() {
        super("USER_IS_NOT_ADMIN", HttpStatus.FORBIDDEN,"User is not admin");
    }
}
