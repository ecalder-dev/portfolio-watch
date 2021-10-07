package com.portfoliowatch.util;

import com.portfoliowatch.model.dto.Lot;

import java.util.Comparator;

public class LotComparator implements Comparator<Lot> {
    @Override
    public int compare(Lot lot1, Lot lot2) {
        if (lot1 == null && lot2 == null) {
            return 0;
        } else if (lot1 != null && lot2 == null) {
            return -1;
        } else if (lot1 == null) {
            return 1;
        }

        if (lot1.getDateTransacted() == null &&
                lot2.getDateTransacted() == null) {
            return 0;
        } else if (lot1.getDateTransacted() != null &&
                lot2.getDateTransacted() == null) {
            return -1;
        } else if (lot1.getDateTransacted() == null &&
                lot2.getDateTransacted() != null) {
            return 1;
        } else if (lot1.getDateTransacted().before(lot2.getDateTransacted())){
            return -1;
        } else if (lot1.getDateTransacted().after(lot2.getDateTransacted())) {
            return 1;
        }

        return Double.compare(lot1.getPrice(), lot2.getPrice()); }
}
