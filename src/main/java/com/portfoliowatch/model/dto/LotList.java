package com.portfoliowatch.model.dto;

import com.portfoliowatch.util.LotComparator;
import lombok.Data;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

@Data
public class LotList extends LinkedList<Lot> {

    private final LotComparator lotComparator;

    private double totalShares;

    private double totalPrice;

    public LotList() {
        super();
        lotComparator = new LotComparator();
    }

    public boolean add(Lot lot) {
        boolean added = super.add(lot);
        if (added) {
            totalShares += lot.getShares();
            totalPrice += lot.getShares() * lot.getPrice();
            this.sort(lotComparator);
        }
        return added;
    }

    public boolean addAll(List<Lot> lotList) {
        boolean added = super.addAll(lotList);
        if (added) {
            lotList.forEach(l -> {
                totalShares += l.getShares();
                totalPrice += l.getShares() * l.getPrice();

            });
            this.sort(lotComparator);
        }
        return added;
    }

    public Lot peak() {
        if (this.size() == 0) {
            return null;
        } else {
            return this.get(0);
        }
    }

    public boolean remove(Lot lot) {
        boolean removed = super.remove(lot);
        if (removed) {
            totalShares -= lot.getShares();
            totalPrice -= lot.getPrice() * lot.getShares();
        }
        return removed;
    }

    public boolean removeAll(List<Lot> lotList) {
        boolean removed = super.removeAll(lotList);
        if (removed) {
            lotList.forEach(l -> {
                totalShares -= l.getShares();
                totalPrice -= l.getShares() * l.getPrice();
            });
        }
        return removed;
    }
}
