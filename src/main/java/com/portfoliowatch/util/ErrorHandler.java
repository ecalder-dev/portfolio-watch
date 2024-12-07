package com.portfoliowatch.util;

import com.portfoliowatch.util.exception.NoDataException;

import java.util.Optional;

public final class ErrorHandler {

    public static void validateNonNull(Object object, String message) throws NoDataException {
        if (object instanceof Optional) {
            if (!((Optional<?>) object).isPresent()) {
                throw new NoDataException(message);
            }
        } else if (object == null) {
            throw new NoDataException(message);
        }
    }

    public static void validateTrue(Boolean condition, String message) throws IllegalArgumentException {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }
}
