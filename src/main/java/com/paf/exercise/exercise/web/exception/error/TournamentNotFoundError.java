package com.paf.exercise.exercise.web.exception.error;

import org.springframework.http.HttpStatus;

public class TournamentNotFoundError  extends PafError {
    public TournamentNotFoundError() {
        super("TOURNAMENT_NOT_FOUND", HttpStatus.NOT_FOUND ,"Tournament not found");
    }
}