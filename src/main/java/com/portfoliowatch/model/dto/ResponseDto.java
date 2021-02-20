package com.portfoliowatch.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

@Getter @Setter
@AllArgsConstructor
public class ResponseDto<T> {
    private T data;
    private String error;
    private int status;
}
