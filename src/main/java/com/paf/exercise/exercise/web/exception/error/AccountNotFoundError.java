package com.paf.exercise.exercise.web.exception.error;

import org.springframework.http.HttpStatus;

public class AccountNotFoundError extends PafError {
    public AccountNotFoundError() {
        super("ACCOUNT_NOT_FOUND", HttpStatus.NOT_FOUND ,"Account not found");
    }
}
