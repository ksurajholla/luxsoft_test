package com.db.awmd.challenge.repository;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.AccountTransferMoney;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.LowBalanceException;
import com.db.awmd.challenge.exception.NoAccountIdException;

import lombok.Synchronized;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class AccountsRepositoryInMemory implements AccountsRepository {

  private final Map<String, Account> accounts = new ConcurrentHashMap<>();

  @Override
  public void createAccount(Account account) throws DuplicateAccountIdException {
    Account previousAccount = accounts.putIfAbsent(account.getAccountId(), account);
    if (previousAccount != null) {
      throw new DuplicateAccountIdException(
        "Account id " + account.getAccountId() + " already exists!");
    }
  }

  @Override
  public Account getAccount(String accountId) {
    return accounts.get(accountId);
  }
  
	@Override
	public void transferAmount(AccountTransferMoney accountTransferMoney) throws NoAccountIdException,LowBalanceException {
		if (accounts.containsKey(accountTransferMoney.getAccountFromId())
				&& accounts.containsKey(accountTransferMoney.getAccountToId())) {
			Account senderAccount=accounts.get(accountTransferMoney.getAccountFromId());
			if(senderAccount.getBalance().doubleValue()<accountTransferMoney.getAmount().doubleValue())
			{
				throw new LowBalanceException("Sender doesnt have sufficient balance!!");
			}
			synchronized(this)
	        {
			senderAccount.setBalance(senderAccount.getBalance().subtract(accountTransferMoney.getAmount()));
			Account recieverAccount=accounts.get(accountTransferMoney.getAccountToId());
			recieverAccount.setBalance(recieverAccount.getBalance().add(accountTransferMoney.getAmount()));
	        }
		} else {
			throw new NoAccountIdException("Account id does not exist! Please send correct information");
		}

	}

  @Override
  public void clearAccounts() {
    accounts.clear();
  }

}
