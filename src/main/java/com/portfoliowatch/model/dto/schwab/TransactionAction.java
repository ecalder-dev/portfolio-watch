package com.portfoliowatch.model.dto.schwab;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum TransactionAction {
    BUY,
    SELL,
    TRANSFER,
    SPLIT;

    @JsonCreator
    public static TransactionAction fromString(String value) {
        return switch (value.toLowerCase()) {
            case "buy" -> BUY;
            case "sell" -> SELL;
            case "internal transfer" -> TRANSFER;
            case "stock split" -> SPLIT;
            default -> {
                log.info("No action called: " + value.toLowerCase());
                yield null;
            }
        };
    }
}
