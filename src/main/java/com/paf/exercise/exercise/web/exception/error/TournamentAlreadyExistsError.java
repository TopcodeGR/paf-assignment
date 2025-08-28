package com.paf.exercise.exercise.web.exception.error;

import org.springframework.http.HttpStatus;

public class TournamentAlreadyExistsError extends PafError {
    public TournamentAlreadyExistsError() {
        super("TOURNAMENT_ALREADY_EXISTS", HttpStatus.CONFLICT ,"Tournament already exists");
    }
}
