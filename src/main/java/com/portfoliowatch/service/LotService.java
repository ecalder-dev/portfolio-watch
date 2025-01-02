package com.portfoliowatch.service;

import com.portfoliowatch.model.dto.CostBasisDto;
import com.portfoliowatch.model.dto.LotDto;
import com.portfoliowatch.model.dto.schwab.BrokerageTransaction;
import com.portfoliowatch.model.entity.Account;
import com.portfoliowatch.model.entity.CorporateAction;
import com.portfoliowatch.model.entity.Lot;
import com.portfoliowatch.model.entity.Transaction;
import com.portfoliowatch.model.entity.Transfer;
import com.portfoliowatch.model.entity.base.BaseEvent;
import com.portfoliowatch.repository.AccountRepository;
import com.portfoliowatch.repository.CorporateActionRepository;
import com.portfoliowatch.repository.LotRepository;
import com.portfoliowatch.repository.TransactionRepository;
import com.portfoliowatch.repository.TransferRepository;
import com.portfoliowatch.util.ActionComparator;
import com.portfoliowatch.util.enums.CorporateActionType;
import com.portfoliowatch.util.enums.LotSaleType;
import com.portfoliowatch.util.enums.TransactionType;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class LotService {

  private final LotRepository lotRepository;
  private final TransactionRepository transactionRepository;
  private final TransferRepository transferRepository;
  private final CorporateActionRepository corporateActionRepository;
  private final AccountRepository accountRepository;
  private final LotSaleService lotSaleService;

  private final int SCALE = 5;
  private final RoundingMode ROUNDING = RoundingMode.HALF_UP;
  private final Sort sortByTransactionDate = Sort.by(Sort.Direction.DESC, "dateTransacted");
  private static final Sort SORT_BY_TRANSACTION_DATE =
      Sort.by(Sort.Direction.DESC, "dateTransacted");

  public List<CostBasisDto> getCostBasis() {
    Set<String> ownedStocks = getOwnedStocks();
    return ownedStocks.stream()
        .map(this::calculateCostBasis)
        .filter(costBasisDto -> costBasisDto != null)
        .collect(Collectors.toList());
  }

  private CostBasisDto calculateCostBasis(String symbol) {
    List<Lot> ownedLots =
        lotRepository.findAllBySymbolAndSharesGreaterThan(
            SORT_BY_TRANSACTION_DATE, symbol, BigDecimal.ZERO);
    if (ownedLots.isEmpty()) {
      return null;
    }
    BigDecimal adjustedPrice = BigDecimal.ZERO;
    BigDecimal totalShares = BigDecimal.ZERO;
    CostBasisDto costBasisDto = new CostBasisDto();
    costBasisDto.setLatestTransactionDate(ownedLots.get(0).getDateTransacted());
    costBasisDto.setSymbol(symbol);
    for (Lot lot : ownedLots) {
      adjustedPrice = adjustedPrice.add(lot.getPrice().multiply(lot.getShares()));
      totalShares = totalShares.add(lot.getShares());
    }
    costBasisDto.setAdjustedPrice(adjustedPrice.divide(totalShares, SCALE, ROUNDING));
    costBasisDto.setTotalShares(totalShares.setScale(SCALE, ROUNDING));
    costBasisDto.setLotList(ownedLots.stream().map(LotDto::new).collect(Collectors.toList()));
    return costBasisDto;
  }

  public Set<String> getOwnedStocks() {
    return lotRepository.findAllUniqueSymbols();
  }

  /**
   * Reduces the available shares in lots associated with the given transaction. The method
   * processes the lots in the order of their transaction date (FIFO), reducing the number of shares
   * in each lot until the total shares to be sold are exhausted or no more lots are available.
   *
   * <p>If the shares to be sold exceed the available shares in the lots, the method logs an error
   * indicating the remaining shares cannot be sold due to a lack of available lots. If any lot
   * still contains shares after the transaction, the remaining shares are updated and the lot is
   * saved.
   *
   * @param transaction The transaction containing the details of the sale, including the number of
   *     shares to sell, the associated account, and the transaction date.
   */
  public void reduceLotsWith(Transaction transaction) {
    if (transaction == null) {
      log.error("Transaction is null");
      return;
    }
    Queue<Lot> lotQueue =
        new LinkedList<>(
            lotRepository.findBySymbolAndAccountAndSharesGreaterThanOrderByDateTransactedAsc(
                transaction.getSymbol(), transaction.getAccount(), BigDecimal.ZERO));
    if (lotQueue.isEmpty()) {
      log.error("No lots available for transaction: {}", transaction);
      return;
    }
    BigDecimal sharesToSell = transaction.getShares();
    Lot lot = lotQueue.poll();
    while (sharesToSell.compareTo(BigDecimal.ZERO) > 0 && lot != null) {
      if (sharesToSell.compareTo(lot.getShares()) >= 0) {
        sharesToSell = sharesToSell.subtract(lot.getShares());
        lotSaleService.recordLotSold(
            lot,
            lot.getShares(),
            transaction.getPrice(),
            transaction.getDateTransacted(),
            LotSaleType.TRANSACTIONAL);
        lot.setShares(BigDecimal.ZERO);
        lotRepository.save(lot);
        log.debug("Lot was cleared for: {}", transaction);
        lot = lotQueue.poll();
      } else {
        lot.setShares(lot.getShares().subtract(sharesToSell).setScale(SCALE, ROUNDING));
        lot.setDatetimeUpdated(transaction.getDatetimeUpdated());
        lotSaleService.recordLotSold(
            lot,
            sharesToSell,
            transaction.getPrice(),
            transaction.getDateTransacted(),
            LotSaleType.TRANSACTIONAL);
        lotRepository.save(lot);
        log.debug("Lot was updated for: {}", transaction);
        sharesToSell = BigDecimal.ZERO;
      }
    }
    if (sharesToSell.compareTo(BigDecimal.ZERO) > 0) {
      log.error("No lots left to sell for transaction: {}", transaction);
    }
  }

  /**
   * Creates a new lot based on the provided transaction and saves it to the repository. This method
   * takes the details from the given transaction and uses them to populate a new `Lot` object,
   * which is then saved to the database. The lot is associated with the same account as the
   * transaction and includes details such as the symbol, price, shares, transaction date, and
   * creation/update timestamps.
   *
   * @param transaction The transaction object containing the details for creating a new lot. This
   *     includes the symbol, price, account, number of shares, and the transaction date that will
   *     be assigned to the new lot.
   */
  public void createLotWith(Transaction transaction) {
    if (transaction == null) {
      log.error("Transaction is null");
      return;
    }
    Lot lot = new Lot();
    lot.setSymbol(transaction.getSymbol());
    lot.setPrice(transaction.getPrice());
    lot.setAccount(transaction.getAccount());
    lot.setShares(transaction.getShares());
    lot.setDateTransacted(transaction.getDateTransacted());
    lot.setDatetimeUpdated(new Date());
    lot.setDatetimeCreated(new Date());
    log.debug("Lot was created for: {}", transaction);
    lotRepository.save(lot);
  }

  /**
   * Transfers a list of lots from the current account to a new account as part of the given
   * transfer. This method updates the account for each lot in the list of lots to transfer and sets
   * the `datetimeUpdated` field to the current date and time. After updating the lots, the changes
   * are persisted to the repository in a batch operation.
   *
   * @param transfer The transfer object containing the details of the account transfer. It includes
   *     the new account to which the lots should be assigned and the list of lots to be
   *     transferred.
   */
  public void transferLotsWith(Transfer transfer) {
    if (transfer == null) {
      log.error("Transfer is null");
      return;
    }
    List<Lot> oldAccountLots =
        new LinkedList<>(
            lotRepository.findBySymbolAndAccountAndSharesGreaterThanOrderByDateTransactedAsc(
                transfer.getSymbol(), transfer.getFromAccount(), BigDecimal.ZERO));
    log.debug(
        "Transferring [{}] from Account[id={}] to Account[id={}]",
        transfer.getSymbol(),
        transfer.getFromAccount().getId(),
        transfer.getToAccount().getId());
    log.debug(
        "Attempting to transfer [{}] stocks from existing [{}]",
        transfer.getShares(),
        getTotalShares(transfer.getFromAccount(), transfer.getSymbol()));
    oldAccountLots.sort(Comparator.comparing(Lot::getDateTransacted));
    Queue<Lot> lotQueue = new LinkedList<>(oldAccountLots);

    BigDecimal sharesToTransfer = transfer.getShares();
    Lot lot = lotQueue.poll();
    while (sharesToTransfer.compareTo(BigDecimal.ZERO) > 0 && lot != null) {
      if (sharesToTransfer.compareTo(lot.getShares()) >= 0) {
        lot.setAccount(transfer.getToAccount());
        lot.setDatetimeUpdated(new Date());
        sharesToTransfer = sharesToTransfer.subtract(lot.getShares());
        log.debug(
            "Lot id [{}] was transferred to account [{} ({})]",
            lot.getId(),
            transfer.getToAccount().getAccountName(),
            transfer.getToAccount().getId());
        lotRepository.save(lot);
        lot = lotQueue.poll();
      } else {
        BigDecimal sharesToStay = lot.getShares().subtract(sharesToTransfer);
        lot.setShares(sharesToStay);
        lot.setDatetimeUpdated(new Date());
        lotRepository.save(lot);

        Lot newLot = new Lot();
        newLot.setShares(sharesToTransfer);
        newLot.setSymbol(lot.getSymbol());
        newLot.setAccount(transfer.getToAccount());
        newLot.setDatetimeCreated(new Date());
        newLot.setDatetimeUpdated(newLot.getDatetimeCreated());
        newLot.setDateTransacted(lot.getDateTransacted());
        newLot.setPrice(lot.getPrice());
        lotRepository.save(newLot);

        sharesToTransfer = BigDecimal.ZERO;
      }
    }
  }

  /**
   * Merges lots based on a corporate action (e.g., a stock split, reverse split, or other corporate
   * event). For each account, this method adjusts the shares and prices of affected lots according
   * to the corporate action's ratio and updates the lots in the database. If necessary, the method
   * handles fractional shares by creating a transaction to sell the fractional shares that cannot
   * be merged.
   *
   * <p>
   *
   * @param action The corporate action object containing the details of the action, including: -
   *     `oldSymbol`: The symbol of the stock before the corporate action. - `newSymbol`: The symbol
   *     of the stock after the corporate action. - `ratioAntecedent`: The ratio of shares before
   *     the corporate action. - `ratioConsequent`: The ratio of shares after the corporate action.
   *     - `price`: The price of the shares after the corporate action.
   */
  public void mergeSplitLotsWith(CorporateAction action) {
    if (action == null) {
      log.error("Corporate action is null");
      return;
    }
    List<Account> accounts = accountRepository.findAll();
    for (Account account : accounts) {
      List<Lot> affectedLots =
          lotRepository.findBySymbolAndAccountAndSharesGreaterThanOrderByDateTransactedAsc(
              action.getOldSymbol(), account, BigDecimal.ZERO);
      BigDecimal multiplier =
          action.getRatioConsequent().divide(action.getRatioAntecedent(), SCALE, ROUNDING);
      BigDecimal totalOriginalShares = BigDecimal.ZERO;
      BigDecimal totalLotPrice = BigDecimal.ZERO;
      if (affectedLots.isEmpty()) {
        continue;
      }
      for (Lot lot : affectedLots) {
        totalOriginalShares = totalOriginalShares.add(lot.getShares());
        totalLotPrice = totalLotPrice.add(lot.getShares().multiply(lot.getPrice()));
        lot.setShares(BigDecimal.ZERO);
        lot.setDatetimeUpdated(new Date());
      }
      BigDecimal totalNewShares = totalOriginalShares.multiply(multiplier);
      log.debug(
          "Total of {} shares of {} was converted to {} shares of {}",
          totalOriginalShares,
          action.getOldSymbol(),
          totalNewShares,
          action.getNewSymbol());
      BigDecimal sharesWhole = totalNewShares.setScale(0, RoundingMode.FLOOR);
      BigDecimal sharesPartials = totalNewShares.remainder(BigDecimal.ONE);
      BigDecimal newPrice = totalLotPrice.divide(totalNewShares, SCALE, ROUNDING);

      lotRepository.saveAll(affectedLots);

      Lot newLot = new Lot();
      newLot.setSymbol(action.getNewSymbol());
      newLot.setShares(sharesWhole);
      newLot.setPrice(newPrice);
      newLot.setDateTransacted(action.getDateOfEvent());
      newLot.setDatetimeCreated(new Date());
      newLot.setDatetimeUpdated(newLot.getDatetimeCreated());
      newLot.setAccount(account);
      lotRepository.save(newLot);

      if (sharesPartials.compareTo(BigDecimal.ZERO) > 0) {
        lotSaleService.recordLotSold(
            newLot, sharesPartials, action.getOriginalPrice(), action.getDateOfEvent(), LotSaleType.MERGE_PARTIAL);
      }
    }
  }

  /**
   * Splits the shares in lots according to the given corporate action (e.g., a stock split). This
   * method processes the lots associated with the old symbol of the corporate action and updates
   * their shares and prices based on the split ratio. The method ensures that the total number of
   * shares before and after the split is tracked. If there is any discrepancy due to fractional
   * shares, it logs the difference.
   *
   * <p>
   *
   * @param action The corporate action object containing the details of the split, including: -
   *     `oldSymbol`: The symbol of the stock before the split. - `newSymbol`: The symbol of the
   *     stock after the split (optional depending on business logic). - `ratioAntecedent`: The
   *     ratio of shares before the split. - `ratioConsequent`: The ratio of shares after the split.
   */
  public void splitLotsWith(CorporateAction action) {
    if (action == null) {
      log.error("Corporate action is null");
      return;
    }
    List<Account> accounts = accountRepository.findAll();
    for (Account account : accounts) {
      List<Lot> affectedLots =
          lotRepository.findBySymbolAndAccountAndSharesGreaterThanOrderByDateTransactedAsc(
              action.getOldSymbol(), account, BigDecimal.ZERO);
      BigDecimal multiplier =
          action.getRatioConsequent().divide(action.getRatioAntecedent(), SCALE, ROUNDING);
      for (Lot lot : affectedLots) {
        processLotSplit(lot, multiplier, action);
      }
      lotRepository.saveAll(affectedLots);
    }
  }

  private void processLotSplit(Lot lot, BigDecimal multiplier, CorporateAction action) {
    if (lot == null || multiplier == null || action == null) {
      log.error("Invalid parameters for processing lot split");
      return;
    }

    BigDecimal currentShares = lot.getShares();
    BigDecimal currentSharesPartials = currentShares.remainder(BigDecimal.ONE);
    BigDecimal newShares = lot.getShares().multiply(multiplier).setScale(SCALE, ROUNDING);
    BigDecimal newSharesWhole = newShares.setScale(0, RoundingMode.FLOOR);
    BigDecimal newSharesPartials = newShares.remainder(BigDecimal.ONE);
    BigDecimal newPrice = lot.getPrice().divide(multiplier, SCALE, ROUNDING);
    BigDecimal sharesToSell = BigDecimal.ZERO;

    if (currentShares.compareTo(BigDecimal.ONE) < 0 && newShares.compareTo(BigDecimal.ONE) < 0) {
      if (newSharesPartials.compareTo(currentSharesPartials) > 0) {
        lot.setShares(currentShares);
        sharesToSell = newSharesPartials.subtract(currentSharesPartials);
      } else {
        lot.setShares(BigDecimal.ZERO);
        sharesToSell = newShares;
      }
    } else {
      lot.setShares(newSharesWhole);
    }

    lot.setPrice(newPrice);
    lot.setDatetimeUpdated(new Date());
    lotRepository.save(lot);

    if (sharesToSell.compareTo(BigDecimal.ZERO) > 0) {
      lotSaleService.recordLotSold(
          lot, sharesToSell, action.getOriginalPrice(), action.getDateOfEvent(), LotSaleType.SPLIT_PARTIAL);
    }

    log.info(
        "Processed lot split for lot: {}, new shares: {}, new price: {}",
        lot.getId(),
        newShares,
        newPrice);
  }

  /**
   * Handles the spin-off of lots based on a corporate action (e.g., a stock spin-off). This method
   * processes the lots associated with the old symbol of the corporate action and updates their
   * prices and shares according to the spin-off ratios. It calculates the fair value of the parent
   * and child stocks, determines the proportionate spin-off value for each lot, and creates new
   * lots for the spun-off shares. If there are whole shares to spin off, they are saved as new lots
   * in the database.
   *
   * <p>
   *
   * @param action The corporate action object containing the details of the spin-off, including: -
   *     `oldSymbol`: The symbol of the stock before the spin-off. - `newSymbol`: The symbol of the
   *     stock after the spin-off (child stock). - `ratioAntecedent`: The ratio of shares before the
   *     spin-off. - `ratioConsequent`: The ratio of shares after the spin-off. - `price`: The fair
   *     value (price) of the parent stock. - `spinOffPrice`: The fair value (price) of the child
   *     stock after the spin-off. - `dateOfEvent`: The date of the corporate action event.
   */
  public void spinOffLotsWith(CorporateAction action) {
    if (action == null) {
      log.error("Corporate action is null");
      return;
    }
    List<Account> accounts = accountRepository.findAll();
    for (Account account : accounts) {
      Date now = new Date();
      BigDecimal multiplier =
          action.getRatioConsequent().divide(action.getRatioAntecedent(), SCALE, ROUNDING);
      BigDecimal parentFairValue = action.getOriginalPrice();
      BigDecimal childFairValue = action.getSpinOffPrice();
      BigDecimal proportionateSpinOffVal = childFairValue.multiply(multiplier);
      BigDecimal totalFairValue = parentFairValue.add(proportionateSpinOffVal);
      BigDecimal adjVal = parentFairValue.add(proportionateSpinOffVal);
      BigDecimal spinOffPercent = proportionateSpinOffVal.divide(adjVal, SCALE, ROUNDING);
      List<Lot> affectedLots =
          lotRepository.findBySymbolAndAccountAndSharesGreaterThanOrderByDateTransactedAsc(
              action.getOldSymbol(), account, BigDecimal.ZERO);
      if (!affectedLots.isEmpty()) {
        BigDecimal totalSpinOffPrice = BigDecimal.ZERO;
        BigDecimal totalLot = BigDecimal.ZERO;
        for (Lot lot : affectedLots) {
          BigDecimal currentPrice = lot.getPrice().multiply(lot.getShares());
          totalLot = totalLot.add(lot.getShares());
          totalSpinOffPrice = totalSpinOffPrice.add(currentPrice.multiply(spinOffPercent));
          BigDecimal parentToTotal = parentFairValue.divide(totalFairValue, SCALE, ROUNDING);
          lot.setPrice(lot.getPrice().multiply(parentToTotal).setScale(SCALE, ROUNDING));
          lot.setDatetimeUpdated(now);
        }
        lotRepository.saveAll(affectedLots);
        BigDecimal spinOffCount = totalLot.multiply(multiplier);
        BigDecimal spinOffWhole = spinOffCount.setScale(0, RoundingMode.FLOOR);
        BigDecimal spinOffPartials = spinOffCount.remainder(BigDecimal.ONE);
        BigDecimal spinOffCostBasis = totalSpinOffPrice.divide(spinOffCount, SCALE, ROUNDING);

        Lot lot = new Lot();
        lot.setSymbol(action.getNewSymbol());
        lot.setShares(spinOffWhole);
        lot.setPrice(spinOffCostBasis);
        lot.setAccount(account);
        lot.setDatetimeUpdated(now);
        lot.setDatetimeCreated(now);
        lot.setDateTransacted(action.getDateOfEvent());
        lotRepository.save(lot);

        // Sell off remaining partial shares
        if (spinOffWhole.compareTo(BigDecimal.ZERO) > 0) {
          lotSaleService.recordLotSold(
              lot,
              spinOffPartials,
              action.getSpinOffPrice(),
              action.getDateOfEvent(),
              LotSaleType.SPIN_OFF_PARTIAL);
        }
      }
    }
  }

  /**
   * Rebuilds all lots by processing all transactions, transfers, and corporate actions.
   *
   * <p>This method clears all existing lots in the system and then processes each transaction,
   * transfer, and corporate action in chronological order (sorted by transaction date). For each
   * type of entity (Transaction, Transfer, CorporateAction), it performs the corresponding action:
   * - Transactions are processed based on their type (BUY, SELL, GIFT) to either create new lots or
   * reduce existing ones. - Transfers are processed to transfer shares between accounts. -
   * Corporate actions (e.g., reverse splits, spin-offs, splits) are processed and adjust lots
   * according to the specific corporate action type.
   *
   * <p>The method follows these steps: 1. Deletes all existing lots in the system. 2. Retrieves all
   * transactions, transfers, and corporate actions from the repositories. 3. Sorts the entities
   * (transactions, transfers, and corporate actions) in chronological order by transaction date
   * using a priority queue and a custom comparator (`ActionComparator`). 4. Iterates through the
   * sorted queue, and for each entity: - If it is a transaction, it either creates or reduces lots
   * depending on the transaction type. - If it is a transfer, it updates the affected lots by
   * transferring them to a new account. - If it is a corporate action (reverse split, spin, or
   * split), it updates the affected lots accordingly.
   *
   * @see ActionComparator - The comparator used to sort entities by transaction date.
   */
  public void rebuildAllLots() {
    log.info("Rebuilding lot tables.");
    lotSaleService.deleteAll();
    lotRepository.deleteAll();

    PriorityQueue<BaseEvent> queue = getAllBaseEventsAsQueue();

    while (!queue.isEmpty()) {
      BaseEvent entity = queue.poll();
      if (entity instanceof Transaction transaction) {
        if (transaction.getType() == TransactionType.SELL) {
          reduceLotsWith(transaction);
        } else if (transaction.getType() == TransactionType.BUY
            || transaction.getType() == TransactionType.GIFT) {
          createLotWith(transaction);
        }
      } else if (entity instanceof Transfer transfer) {
        transferLotsWith(transfer);
      } else if (entity instanceof CorporateAction corporateAction) {
        CorporateActionType type = corporateAction.getType();
        if (type == CorporateActionType.MERGE) {
          mergeSplitLotsWith(corporateAction);
        } else if (type == CorporateActionType.SPIN) {
          spinOffLotsWith(corporateAction);
        } else if (type == CorporateActionType.SPLIT) {
          splitLotsWith(corporateAction);
        }
      }
    }
    log.info("Rebuilding complete.");
  }

  /**
   * Gets total amount of shares from given account and symbol.
   *
   * @param account The account to pull lots from.
   * @param symbol The symbol to filter the selection.
   * @return The total number of shares combined.
   */
  public BigDecimal getTotalShares(Account account, String symbol) {
    List<Lot> lots =
        lotRepository.findBySymbolAndAccountAndSharesGreaterThanOrderByDateTransactedAsc(
            symbol, account, BigDecimal.ZERO);
    return lots.stream()
        .map(Lot::getShares)
        .reduce(BigDecimal.ZERO, BigDecimal::add)
        .setScale(SCALE, ROUNDING);
  }

  private PriorityQueue<BaseEvent> getAllBaseEventsAsQueue() {
    List<Transaction> allTransactions = transactionRepository.findAll();
    List<Transfer> allTransfer = transferRepository.findAll();
    List<CorporateAction> allCorporateActions = corporateActionRepository.findAll();

    PriorityQueue<BaseEvent> queue = new PriorityQueue<>(new ActionComparator());
    queue.addAll(allTransactions);
    queue.addAll(allTransfer);
    queue.addAll(allCorporateActions);

    return queue;
  }

  /**
   * Processes a list of brokerage transactions from Schwab and converts them into corresponding
   * transactions, transfers, and corporate actions in the system. If `isSimulation` is false, the
   * method saves the processed events to the database and rebuilds all lots.
   *
   * @param brokerageTransactionList The list of brokerage transactions to process.
   * @param targetAccountId The ID of the target account for transactions.
   * @param transferAccountId The ID of the account to transfer shares to.
   * @param isSimulation If true, the method simulates the processing without saving to the
   *     database.
   * @return A list of processed base events (transactions, transfers, and corporate actions).
   */
  public List<BaseEvent> processSchwabTransactions(
      List<BrokerageTransaction> brokerageTransactionList,
      Long targetAccountId,
      Long transferAccountId,
      boolean isSimulation) {

    Account targetAccount = accountRepository.findById(targetAccountId).orElse(null);
    Account accountToTransfer = accountRepository.findById(transferAccountId).orElse(null);

    if (targetAccount == null || accountToTransfer == null) {
      log.error("Target account or transfer account is null");
      return List.of();
    }

    List<BaseEvent> events = new LinkedList<>();
    List<Transaction> transactions = new LinkedList<>();
    List<Transfer> transfers = new LinkedList<>();
    List<CorporateAction> corporateActions = new LinkedList<>();

    for (BrokerageTransaction brokerageTransaction : brokerageTransactionList) {
      if (brokerageTransaction.getAction() == null) {
        log.warn("Skipping transaction with null action: {}", brokerageTransaction);
        continue;
      }
      switch (brokerageTransaction.getAction()) {
        case BUY ->
            transactions.add(
                createTransactionFromBrokerageTransaction(
                    brokerageTransaction, targetAccount, TransactionType.BUY));
        case SELL ->
            transactions.add(
                createTransactionFromBrokerageTransaction(
                    brokerageTransaction, targetAccount, TransactionType.SELL));
        case TRANSFER -> {
          if (brokerageTransaction.getSymbol() != null
              && !brokerageTransaction.getSymbol().isEmpty()
              && brokerageTransaction.getQuantity() != null) {
            transfers.add(
                createTransferFromBrokerageTransaction(
                    brokerageTransaction, targetAccount, accountToTransfer));
          }
        }
        case SPLIT ->
            corporateActions.add(
                createCorporateActionFromBrokerageTransaction(brokerageTransaction));
        default -> log.warn("Unknown action type: {}", brokerageTransaction.getAction());
      }
    }

    events.addAll(transactions);
    events.addAll(transfers);
    events.addAll(corporateActions);

    if (!isSimulation) {
      transactionRepository.saveAll(transactions);
      transferRepository.saveAll(transfers);
      corporateActionRepository.saveAll(corporateActions);
      rebuildAllLots();
    }

    return events;
  }

  private Transaction createTransactionFromBrokerageTransaction(
      BrokerageTransaction brokerageTransaction, Account account, TransactionType type) {
    Transaction transaction = new Transaction();
    transaction.setType(type);
    transaction.setAccount(account);
    transaction.setSymbol(brokerageTransaction.getSymbol());
    transaction.setPrice(brokerageTransaction.getPrice());
    transaction.setShares(brokerageTransaction.getQuantity());
    transaction.setDateTransacted(brokerageTransaction.getTransactionDate());
    transaction.setDatetimeCreated(new Date());
    transaction.setDatetimeUpdated(transaction.getDatetimeCreated());
    log.debug("Created transaction: {}", transaction);
    return transaction;
  }

  private Transfer createTransferFromBrokerageTransaction(
      BrokerageTransaction brokerageTransaction, Account fromAccount, Account toAccount) {
    Transfer transfer = new Transfer();
    transfer.setSymbol(brokerageTransaction.getSymbol());
    transfer.setShares(brokerageTransaction.getQuantity());
    transfer.setFromAccount(fromAccount);
    transfer.setToAccount(toAccount);
    transfer.setDateTransacted(brokerageTransaction.getTransactionDate());
    transfer.setDatetimeCreated(new Date());
    transfer.setDatetimeUpdated(transfer.getDatetimeCreated());
    log.debug("Created transfer: {}", transfer);
    return transfer;
  }

  private CorporateAction createCorporateActionFromBrokerageTransaction(
      BrokerageTransaction brokerageTransaction) {
    CorporateAction corporateAction = new CorporateAction();
    corporateAction.setType(CorporateActionType.SPLIT);
    corporateAction.setRatioAntecedent(BigDecimal.ZERO);
    corporateAction.setRatioConsequent(BigDecimal.ZERO);
    corporateAction.setOldSymbol(brokerageTransaction.getSymbol());
    corporateAction.setNewSymbol(brokerageTransaction.getSymbol());
    corporateAction.setDateOfEvent(brokerageTransaction.getTransactionDate());
    corporateAction.setDatetimeCreated(new Date());
    corporateAction.setDatetimeUpdated(corporateAction.getDatetimeCreated());
    log.debug("Created corporate action: {}", corporateAction);
    return corporateAction;
  }
}
