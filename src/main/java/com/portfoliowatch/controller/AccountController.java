package com.portfoliowatch.controller;

import com.portfoliowatch.model.dto.AccountDto;
import com.portfoliowatch.service.AccountService;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api")
public class AccountController {

  private final AccountService accountService;

  @GetMapping("/accounts")
  public ResponseEntity<List<AccountDto>> getAllAccounts(
      @RequestParam(name = "showHidden", required = false, defaultValue = "true")
          boolean showHidden) {
    if (showHidden) {
      return ResponseEntity.ok(accountService.getAllAccounts());
    } else {
      return ResponseEntity.ok(accountService.getAllAccountsVisibleOnly());
    }
  }

  @GetMapping("/accounts/{id}")
  public ResponseEntity<AccountDto> getAccount(@PathVariable Long id) {
    AccountDto accountDto = accountService.getAccount(id);
    if (accountDto != null) {
      return ResponseEntity.ok(accountDto);
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @PostMapping("/accounts")
  public ResponseEntity<AccountDto> createAccount(@RequestBody AccountDto account) {
    return ResponseEntity.status(HttpStatus.CREATED).body(accountService.createAccount(account));
  }

  @PutMapping("/accounts")
  public ResponseEntity<AccountDto> updateAccount(@RequestBody AccountDto account) {
    AccountDto data = accountService.updateAccount(account);
    if (data != null) {
      return ResponseEntity.ok(data);
    } else {
      log.error("Account not found.");
      return ResponseEntity.noContent().build();
    }
  }

  @DeleteMapping("/accounts/{id}")
  public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
    accountService.deleteAccount(id);
    return ResponseEntity.noContent().build();
  }
}
