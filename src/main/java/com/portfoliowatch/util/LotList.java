package com.portfoliowatch.util;

import java.util.LinkedList;
import java.util.List;

public class LotList extends LinkedList<Lot> {

    private final LotComparator lotComparator;

    public LotList() {
        super();
        lotComparator = new LotComparator();
    }

    public boolean add(Lot lot) {
        if (size() == 0) {
            return super.add(lot);
        } if (size() == 1) {
            if (lotComparator.compare(lot, this.get(0)) < 0) {
                super.add(0, lot);
                return true;
            }
        } else {
            for (int i = 0; i < this.size(); i++) {
                if (lotComparator.compare(lot, this.get(i)) < 0) {
                    super.add(i, lot);
                    return true;
                } else if (i == this.size() - 1) {
                    return super.add(lot);
                }
            }
        }
        return false;
    }

    public boolean addAll(List<Lot> lotList) {
        boolean added = super.addAll(lotList);
        if (added) {
            this.sort(lotComparator);
        }
        return added;
    }
}
