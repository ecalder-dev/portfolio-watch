package com.portfoliowatch.util;

import com.portfoliowatch.model.entity.CorporateAction;
import com.portfoliowatch.model.entity.Transaction;
import com.portfoliowatch.model.entity.Transfer;
import com.portfoliowatch.model.entity.base.Base;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;

public class ActionComparator implements Comparator<Base> {
    @Override
    public int compare(Base entity1, Base entity2) {
        LocalDate d1 = getDate(entity1);
        LocalDate d2 = getDate(entity2);
        int dateOnlyComparison = compareByDate(d1, d2);
        if (dateOnlyComparison != 0) return dateOnlyComparison;
        int priorityComparison = compareActionPriority(entity1, entity2);
        if (priorityComparison != 0) return priorityComparison;
        //Else, return by order of insertion.
        return compareByDateTime(entity1.getDatetimeCreated(), entity2.getDatetimeCreated());
    }

    private int compareByDateTime(Date d1, Date d2) {
        if (d1 == null && d2 == null) {
            return 0;
        } else if (d1 == null) {
            return 1;
        } else if (d2 == null) {
            return -1;
        } else {
            return d1.compareTo(d2);
        }
    }

    private int compareByDate(LocalDate d1, LocalDate d2) {
        if (d1 == null && d2 == null) {
            return 0;
        } else if (d1 == null) {
            return 1;
        } else if (d2 == null) {
            return -1;
        } else {
            int yearComparison = Integer.compare(d1.getYear(), d2.getYear());
            if (yearComparison != 0) return yearComparison;
            int monthComparison = Integer.compare(d1.getMonthValue(), d2.getMonthValue());
            if (monthComparison != 0) return monthComparison;
            return Integer.compare(d1.getDayOfMonth(), d2.getDayOfMonth());
        }
    }

    private int compareActionPriority(Base entity1, Base entity2) {
        if (entity1 == null && entity2 == null) {
            return 0;
        }
        if (entity1 == null) {
            return 1;
        }
        if (entity2 == null) {
            return -1;
        }
        return Integer.compare(getClassPriority(entity1), getClassPriority(entity2));
    }

    private int getClassPriority(Base entity) {
        Class<?> clazz = entity.getClass();
        if (clazz == CorporateAction.class) {
            return 1;
        } else if (clazz == Transfer.class) {
            return 2;
        } else if (clazz == Transaction.class) {
            return 3;
        } else {
            return Integer.MAX_VALUE;  // Default: unknown class type gets lowest priority
        }
    }


    private LocalDate getDate(Base entity) {
        Date date;
        if (entity instanceof Transaction) {
            Transaction transaction = (Transaction) entity;
            date = transaction.getDateTransacted();
        } else if (entity instanceof Transfer) {
            Transfer transfer = (Transfer) entity;
            date = transfer.getDateTransacted();
        } else if (entity instanceof CorporateAction) {
            CorporateAction corporateAction = (CorporateAction) entity;
            date = corporateAction.getDateOfEvent();
        } else {
            return null;
        }
        if (date == null) {
            return null;
        } if (date instanceof java.sql.Date) {
            return ((java.sql.Date) date).toLocalDate();
        } else {
            return date.toInstant()
                    .atZone(ZoneId.systemDefault())  // Convert Instant to ZonedDateTime
                    .toLocalDate();
        }
    }
}
