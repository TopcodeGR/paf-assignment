package com.paf.exercise.exercise.web.exception;

import com.paf.exercise.exercise.web.exception.error.PafError;
import lombok.Getter;

import java.util.List;

@Getter
public class GlobalException extends RuntimeException {

    private final PafError error;

    private final Object[] data;


    public GlobalException(PafError error){
        this(error, List.of());
    }

    public GlobalException(PafError error, Object... data){
        this(error,null, data);
    }

    public GlobalException(PafError error, Throwable cause, Object... data){
        super(error.getMessage(), cause);
        this.error = error;
        this.data =  data;
    }
}
