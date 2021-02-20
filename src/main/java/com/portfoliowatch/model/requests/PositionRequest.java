package com.portfoliowatch.model.requests;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PositionRequest {
    private Long portfolioId;
    private String ticker;
    private Double shareCount;
    private Double cost;
}
