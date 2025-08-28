package com.paf.exercise.exercise.web.exception.error;

import org.springframework.http.HttpStatus;

public class PlayerNotFoundError extends PafError {
    public PlayerNotFoundError() {
        super("PLAYER_NOT_FOUND", HttpStatus.NOT_FOUND ,"Player not found");
    }
}