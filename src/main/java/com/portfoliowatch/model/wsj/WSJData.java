package com.portfoliowatch.model.wsj;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter @Getter
public class WSJData {
    private List<WSJInstrument> instruments;
}
