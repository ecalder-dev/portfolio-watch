package com.portfoliowatch.util;

import com.portfoliowatch.model.tdameritrade.TDAmeriPosition;

import java.util.Comparator;

public class TDAmeriPositionDtoComparator implements Comparator<TDAmeriPosition> {

    @Override
    public int compare(TDAmeriPosition pos1, TDAmeriPosition pos2) {
        int comparison;
        comparison = Double.compare(pos1.getCurrentDayProfitLossPercentage(), pos2.getCurrentDayProfitLossPercentage());
        if (comparison == 0) {
            comparison = pos1.getInstrument().getSymbol().compareTo(pos2.getInstrument().getSymbol());
        }
        return comparison * -1;
    }
}