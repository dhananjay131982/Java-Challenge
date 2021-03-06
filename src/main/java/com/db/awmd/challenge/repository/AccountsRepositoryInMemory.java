package com.db.awmd.challenge.repository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.stereotype.Repository;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.TransferRequest;
import com.db.awmd.challenge.exception.ApplicationException;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.service.EmailNotificationService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Dhananjay Jadhav
 *
 */
@Slf4j
@Repository
public class AccountsRepositoryInMemory implements AccountsRepository {

	private final Map<String, Account> accounts = new ConcurrentHashMap<>();

	private EmailNotificationService emailNotificationService = new EmailNotificationService();

	private Lock bankLock = new ReentrantLock();

	private BigDecimal totalBalance = new BigDecimal(0);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.db.awmd.challenge.repository.AccountsRepository#createAccount(com.db.awmd
	 * .challenge.domain.Account) Create account in memory
	 */
	@Override
	public void createAccount(Account account) throws DuplicateAccountIdException {
		log.info("Creating account {}", account);
		Account previousAccount = accounts.putIfAbsent(account.getAccountId(), account);
		if (previousAccount != null) {
			throw new DuplicateAccountIdException("Account id " + account.getAccountId() + " already exists!");
		}
		totalBalance = totalBalance.add(account.getBalance());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.db.awmd.challenge.repository.AccountsRepository#getAccount(java.lang.
	 * String)
	 * 
	 * Get the account from memory
	 */
	@Override
	public Account getAccount(String accountId) {
		log.info("getAccount ", accountId);
		return accounts.get(accountId);
	}

	@Override
	public void clearAccounts() {
		log.info("clearAccounts ");
		accounts.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.db.awmd.challenge.repository.AccountsRepository#transferMoney(com.db.awmd
	 * .challenge.domain.TransferRequest)
	 * 
	 * This method is used to transfer money from one account to another
	 */
	public void transferMoney(TransferRequest transferRequest) throws ApplicationException {

		log.info("Start of transferMoney ", transferRequest);

		bankLock.lock();

		try {

			Account accountFrom = getAccount(transferRequest.getAccountFrom());

			Account accountTo = getAccount(transferRequest.getAccountTo());

			if (accountFrom == null) {
				throw new ApplicationException("Account id " + transferRequest.getAccountFrom() + " not found");
			}
			if (accountTo == null) {
				throw new ApplicationException("Account id " + transferRequest.getAccountTo() + " not found");
			}

			if (accountFrom.getAccountId().compareToIgnoreCase(accountTo.getAccountId()) == 0) {
				throw new ApplicationException("AccountFrom " + accountFrom.getAccountId() + " is same as accountTo "
						+ accountTo.getAccountId());
			}

			BigDecimal beforeTotalOfTwoAccounts = getTwoAccountBalance(accountFrom, accountTo);
			BigDecimal afterTotalOfTwoAccounts;

			BigDecimal beforeTotal = getTotalBalance();
			BigDecimal afterTotal;

			if (transferRequest.getAmount().compareTo(accountFrom.getBalance()) == -1) {
				accountFrom.withdraw(transferRequest.getAmount());
				accountTo.deposit(transferRequest.getAmount());
				afterTotalOfTwoAccounts = getTwoAccountBalance(accountFrom, accountTo);
				afterTotal = getTotalBalance();
				if (beforeTotalOfTwoAccounts.compareTo(afterTotalOfTwoAccounts) == 0) {
					log.info("Two account balance before Transfer " + beforeTotalOfTwoAccounts
							+ " Two account balance after Transfer " + afterTotalOfTwoAccounts);
					log.info("Total balance before Transfer " + beforeTotal + " total balance after Transfer "
							+ afterTotal);
					emailNotificationService.notifyAboutTransfer(accountFrom, " withdraw sucess");
					emailNotificationService.notifyAboutTransfer(accountTo, " deposit sucess");
				}
			} else if (transferRequest.getAmount().compareTo(accountFrom.getBalance()) == 1) {
				throw new ApplicationException("Account id " + transferRequest.getAccountFrom()
						+ " balance is less than " + transferRequest.getAmount());
			}

		} finally {
			bankLock.unlock();
		}
		log.info("End of transferMoney ");
	}

	/**
	 * This method returns two account balance
	 * 
	 * @param accountFrom
	 * @param accountTo
	 * @return
	 */
	private BigDecimal getTwoAccountBalance(Account accountFrom, Account accountTo) {
		bankLock.lock();

		try {

			return accountFrom.getBalance().add(accountTo.getBalance());

		} finally {
			bankLock.unlock();
		}
	}

	/**
	 * This method returns total balance
	 * 
	 * @param accountFrom
	 * @param accountTo
	 * @return
	 */
	private BigDecimal getTotalBalance() {
		bankLock.lock();

		try {

			return totalBalance;

		} finally {
			bankLock.unlock();
		}
	}

}
