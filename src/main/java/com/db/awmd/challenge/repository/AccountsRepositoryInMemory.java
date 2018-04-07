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

/**
 * @author Dhananjay Jadhav
 *
 */
@Repository
public class AccountsRepositoryInMemory implements AccountsRepository {

	private final Map<String, Account> accounts = new ConcurrentHashMap<>();

	private EmailNotificationService emailNotificationService = new EmailNotificationService();

	private Lock bankLock = new ReentrantLock();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.db.awmd.challenge.repository.AccountsRepository#createAccount(com.db.awmd
	 * .challenge.domain.Account) Create account in memory
	 */
	@Override
	public void createAccount(Account account) throws DuplicateAccountIdException {
		Account previousAccount = accounts.putIfAbsent(account.getAccountId(), account);
		if (previousAccount != null) {
			throw new DuplicateAccountIdException("Account id " + account.getAccountId() + " already exists!");
		}
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
		return accounts.get(accountId);
	}

	@Override
	public void clearAccounts() {
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
	public void transferMoney(TransferRequest transferRequest) {

		bankLock.lock();

		try {

			BigDecimal beforeTotal = getTotalBalance();

			if (getAccount(transferRequest.getAccountFrom()) == null) {
				throw new ApplicationException("Account id " + transferRequest.getAccountFrom() + " not found");
			}
			if (getAccount(transferRequest.getAccountTo()) == null) {
				throw new ApplicationException("Account id " + transferRequest.getAccountTo() + " not found");
			}
			if (transferRequest.getAmount()
					.compareTo(getAccount(transferRequest.getAccountFrom()).getBalance()) == -1) {
				getAccount(transferRequest.getAccountFrom()).withdraw(transferRequest.getAmount());
				getAccount(transferRequest.getAccountTo()).deposit(transferRequest.getAmount());

				if (beforeTotal.compareTo(getTotalBalance()) == 0) {
					emailNotificationService.notifyAboutTransfer(getAccount(transferRequest.getAccountFrom()),
							" withdraw sucess");
					emailNotificationService.notifyAboutTransfer(getAccount(transferRequest.getAccountTo()),
							" deposit sucess");
				}
			} else if (transferRequest.getAmount()
					.compareTo(getAccount(transferRequest.getAccountFrom()).getBalance()) == 1) {
				throw new ApplicationException("Account id " + transferRequest.getAccountFrom()
						+ " balance is less than " + transferRequest.getAmount());
			}

		} finally {
			bankLock.unlock();
		}
	}

	/**
	 * @return
	 * 
	 * 		This method returns total balance of accounts
	 */
	private BigDecimal getTotalBalance() {
		bankLock.lock();

		try {
			BigDecimal total = new BigDecimal(0);

			for (Map.Entry<String, Account> entry : accounts.entrySet()) {

				total = total.add(((Account) entry.getValue()).getBalance());
			}

			return total;
		} finally {
			bankLock.unlock();
		}
	}

}
