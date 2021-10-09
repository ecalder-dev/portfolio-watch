package com.portfoliowatch.model.nasdaq;

import lombok.Data;

@Data
public class ResponseData<T> {
        private T data;
        private String message;
}
