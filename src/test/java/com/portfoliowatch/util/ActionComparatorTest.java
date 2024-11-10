package com.portfoliowatch.util;

import com.portfoliowatch.model.entity.CorporateAction;
import com.portfoliowatch.model.entity.Transaction;
import com.portfoliowatch.model.entity.Transfer;
import com.portfoliowatch.model.entity.base.Base;
import com.portfoliowatch.util.enums.CorporateActionType;
import com.portfoliowatch.util.enums.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Date;
import java.util.PriorityQueue;

import static org.junit.jupiter.api.Assertions.*;

class ActionComparatorTest {

    private ActionComparator comparator;

    @BeforeEach
    void setUp() {
        comparator = new ActionComparator();
    }

    @Test
    void testCompare_TransactionVsTransfer_ShouldPrioritizeTransaction() {
        Transfer transfer = new Transfer();
        transfer.setDateTransacted(date(8,2,2023));
        transfer.setId(1L);
        Transfer transfer1 = new Transfer();
        transfer1.setDateTransacted(date(7,1,2023));
        transfer1.setId(2L);

        Transaction buyTransaction = new Transaction();
        buyTransaction.setDateTransacted(date(7,1,2022));
        buyTransaction.setType(TransactionType.BUY);
        buyTransaction.setId(3L);
        Transaction buyTransaction1 = new Transaction();
        buyTransaction1.setDateTransacted(date(7,1,2023,7,1,1));
        buyTransaction1.setType(TransactionType.BUY);
        buyTransaction1.setId(5L);

        Transaction sellTransaction = new Transaction();
        sellTransaction.setDateTransacted(date(7,1,2023,7,0,0));
        sellTransaction.setType(TransactionType.SELL);
        sellTransaction.setId(7L);

        CorporateAction corporateAction = new CorporateAction();
        corporateAction.setType(CorporateActionType.SPIN);
        corporateAction.setDateOfEvent(date(7,1,2023));
        corporateAction.setId(8L);

        PriorityQueue<Base> testQueue = new PriorityQueue<>(comparator);
        testQueue.add(buyTransaction1);
        testQueue.add(sellTransaction);
        testQueue.add(transfer);
        testQueue.add(corporateAction);
        testQueue.add(transfer1);
        testQueue.add(buyTransaction);

        /*while (!testQueue.isEmpty()) {
            Base entityA = testQueue.poll();
            System.out.println(entityA.toString());
        }*/

        Base entityA = testQueue.poll();
        Base entityB = testQueue.poll();
        Base entityC = testQueue.poll();
        Base entityD = testQueue.poll();
        Base entityE = testQueue.poll();
        Base entityF = testQueue.poll();

        assertNotNull(entityA);
        assertEquals(entityA.getId(), 3L);
        assertNotNull(entityB);
        assertEquals(entityB.getId(), 8L);
        assertNotNull(entityC);
        assertEquals(entityC.getId(), 2L);
        assertNotNull(entityD);
        assertEquals(entityD.getId(), 7L);
        assertNotNull(entityE);
        assertEquals(entityE.getId(), 5L);
        assertNotNull(entityF);
        assertEquals(entityF.getId(), 1L);
    }

    private Date date(int month, int day, int year) {
        return date(month, day, year, 0, 0, 0);
    }

    private Date date(int month, int day, int year, int hour, int min, int sec) {
        return Date.from(LocalDate.of(year, month, day).atTime(hour, min, sec).atZone(java.time.ZoneId.systemDefault()).toInstant());
    }
}
