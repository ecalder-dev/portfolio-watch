package com.portfoliowatch.util.comparator;

import com.portfoliowatch.model.dto.LotDto;

import java.util.Comparator;

public class LotComparator implements Comparator<LotDto> {
    @Override
    public int compare(LotDto lotDto1, LotDto lotDto2) {
        if (lotDto1 == null && lotDto2 == null) {
            return 0;
        } else if (lotDto1 != null && lotDto2 == null) {
            return -1;
        } else if (lotDto1 == null) {
            return 1;
        }

        if (lotDto1.getDateTransacted() == null &&
                lotDto2.getDateTransacted() == null) {
            return 0;
        } else if (lotDto1.getDateTransacted() != null &&
                lotDto2.getDateTransacted() == null) {
            return -1;
        } else if (lotDto1.getDateTransacted() == null &&
                lotDto2.getDateTransacted() != null) {
            return 1;
        } else if (lotDto1.getDateTransacted().before(lotDto2.getDateTransacted())){
            return -1;
        } else if (lotDto1.getDateTransacted().after(lotDto2.getDateTransacted())) {
            return 1;
        }

        return Double.compare(lotDto1.getPrice(), lotDto2.getPrice()); }
}
