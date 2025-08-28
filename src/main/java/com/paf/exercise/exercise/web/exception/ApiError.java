package com.paf.exercise.exercise.web.exception;

import lombok.*;

import java.util.Date;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiError {

    private String message;
    private Integer status;
    private Date timestamp;
    private String code;

    private Object[] data;
}
