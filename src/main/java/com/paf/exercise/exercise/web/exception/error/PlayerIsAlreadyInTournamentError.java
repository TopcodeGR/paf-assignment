package com.paf.exercise.exercise.web.exception.error;

import org.springframework.http.HttpStatus;

public class PlayerIsAlreadyInTournamentError extends PafError {
    public PlayerIsAlreadyInTournamentError() {
        super("PLAYER_IS_ALREADY_IN_TOURNAMENT", HttpStatus.CONFLICT ,"Player is already in tournament");
    }
}

