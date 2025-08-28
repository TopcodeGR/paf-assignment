package com.paf.exercise.exercise.web.exception.error;


import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PafError {

    private  String code;
    private HttpStatus httpStatus;
    private  String message;

}
