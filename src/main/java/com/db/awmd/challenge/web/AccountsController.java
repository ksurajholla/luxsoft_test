package com.db.awmd.challenge.web;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.AccountTransferMoney;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.LowBalanceException;
import com.db.awmd.challenge.exception.NoAccountIdException;
import com.db.awmd.challenge.service.AccountsService;
import com.db.awmd.challenge.service.EmailNotificationService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/accounts")
@Slf4j
public class AccountsController {

  private final AccountsService accountsService;
  
  private final EmailNotificationService emailNotificationService;

  @Autowired
  public AccountsController(AccountsService accountsService,EmailNotificationService emailNotificationService) {
    this.accountsService = accountsService;
    this.emailNotificationService=emailNotificationService;
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Object> createAccount(@RequestBody @Valid Account account) {
    log.info("Creating account {}", account);

    try {
    this.accountsService.createAccount(account);
    } catch (DuplicateAccountIdException daie) {
      return new ResponseEntity<>(daie.getMessage(), HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @GetMapping(path = "/{accountId}")
  public Account getAccount(@PathVariable String accountId) {
    log.info("Retrieving account for id {}", accountId);
    return this.accountsService.getAccount(accountId);
  }
  
	@PostMapping(path = "/transfermoney", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> transferAmount(@RequestBody @Valid AccountTransferMoney accountTransferMoney) {
		log.info("Transfering amount from account id : {} to account id : {}", accountTransferMoney.getAccountFromId(),
				accountTransferMoney.getAccountToId());

		try {
			this.accountsService.transferAmount(accountTransferMoney);
			Account senderAccount = getAccount(accountTransferMoney.getAccountFromId());
			Account recieverAccount = getAccount(accountTransferMoney.getAccountToId());
			this.emailNotificationService.notifyAboutTransfer(senderAccount, "your account is debited with Amount: "
					+ accountTransferMoney.getAmount() + " Updated account balance is : " + senderAccount.getBalance());
			this.emailNotificationService.notifyAboutTransfer(recieverAccount,
					"your account is credited with Amount: " + accountTransferMoney.getAmount()
							+ " Updated account balance is : " + recieverAccount.getBalance());

		} catch (NoAccountIdException | LowBalanceException daie) {
			return new ResponseEntity<>(daie.getMessage(), HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

}
