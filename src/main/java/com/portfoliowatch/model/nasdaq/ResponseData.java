package com.portfoliowatch.model.nasdaq;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class ResponseData<T> {
        private T data;
        private String message;
}
