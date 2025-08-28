package com.paf.exercise.exercise.web.exception.error;

import org.springframework.http.HttpStatus;

public class PlayerAlreadyExistsError extends PafError {
    public PlayerAlreadyExistsError() {
        super("PLAYER_ALREADY_EXISTS", HttpStatus.CONFLICT ,"Player already exists");
    }
}
