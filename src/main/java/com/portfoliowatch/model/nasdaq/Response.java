package com.portfoliowatch.model.nasdaq;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class Response<NasdaqData>{

    private NasdaqData data;

}
