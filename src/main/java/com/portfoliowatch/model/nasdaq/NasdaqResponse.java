package com.portfoliowatch.model.nasdaq;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class NasdaqResponse<NasdaqData>{

    private NasdaqData data;

}
