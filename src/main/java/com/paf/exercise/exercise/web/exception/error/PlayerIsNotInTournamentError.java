package com.paf.exercise.exercise.web.exception.error;

import org.springframework.http.HttpStatus;

public class PlayerIsNotInTournamentError extends PafError {
    public PlayerIsNotInTournamentError() {
        super("PLAYER_IS_NOT_IN_TOURNAMENT", HttpStatus.NOT_FOUND ,"Player is not in tournament");
    }
}

