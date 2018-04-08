package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Random;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.TransferRequest;
import com.db.awmd.challenge.exception.ApplicationException;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.service.AccountsService;

/**
 * @author Dhananjay Jadhav
 * 
 *         Service Tests
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountsServiceTest {

	@Autowired
	private AccountsService accountsService;

	@Test
	public void addAccount() throws Exception {
		Account account = new Account("Id-123");
		account.setBalance(new BigDecimal(1000));
		this.accountsService.createAccount(account);

		assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(account);
	}

	@Test
	public void addAccount_failsOnDuplicateId() throws Exception {
		String uniqueId = "Id-" + System.currentTimeMillis();
		Account account = new Account(uniqueId);
		this.accountsService.createAccount(account);

		try {
			this.accountsService.createAccount(account);
			fail("Should have failed when adding duplicate account");
		} catch (DuplicateAccountIdException ex) {
			assertThat(ex.getMessage()).isEqualTo("Account id " + uniqueId + " already exists!");
		}

	}

	@Test
	public void transferMoney() throws Exception {
		Account fromAccount = new Account("Id1");
		fromAccount.setBalance(new BigDecimal(1000));
		this.accountsService.createAccount(fromAccount);

		Account toAccount = new Account("Id2");
		toAccount.setBalance(new BigDecimal(1000));
		this.accountsService.createAccount(toAccount);

		TransferRequest transferRequest = new TransferRequest();

		transferRequest.setAccountFrom(fromAccount.getAccountId());

		transferRequest.setAccountTo(toAccount.getAccountId());

		transferRequest.setAmount(new BigDecimal(10));

		this.accountsService.transferMoney(transferRequest);

	}

	@Test
	public void transferMoneyAmountNegative() throws Exception {
		Account fromAccount = new Account("Id3");
		fromAccount.setBalance(new BigDecimal(1000));
		this.accountsService.createAccount(fromAccount);

		Account toAccount = new Account("Id4");
		toAccount.setBalance(new BigDecimal(1000));
		this.accountsService.createAccount(toAccount);

		TransferRequest transferRequest = new TransferRequest();

		transferRequest.setAccountFrom(fromAccount.getAccountId());

		transferRequest.setAccountTo(toAccount.getAccountId());

		transferRequest.setAmount(new BigDecimal(-1));
		try {
			this.accountsService.transferMoney(transferRequest);
		} catch (ApplicationException ex) {
			assertThat(ex.getMessage()).isNotEmpty();
		}
	}

	@Test
	public void transferMoneyNoFromAccount() throws Exception {

		Account toAccount = new Account("Id5");
		toAccount.setBalance(new BigDecimal(1000));
		this.accountsService.createAccount(toAccount);

		TransferRequest transferRequest = new TransferRequest();

		transferRequest.setAccountFrom("0");

		transferRequest.setAccountTo(toAccount.getAccountId());

		transferRequest.setAmount(new BigDecimal(-1));
		try {
			this.accountsService.transferMoney(transferRequest);
		} catch (ApplicationException ex) {
			assertThat(ex.getMessage()).isNotEmpty();
		}
	}

	@Test
	public void transferMoneyNoToAccount() throws Exception {

		Account fromAccount = new Account("Id6");
		fromAccount.setBalance(new BigDecimal(1000));
		this.accountsService.createAccount(fromAccount);

		TransferRequest transferRequest = new TransferRequest();

		transferRequest.setAccountTo("0");

		transferRequest.setAccountFrom(fromAccount.getAccountId());

		transferRequest.setAmount(new BigDecimal(-1));
		try {
			this.accountsService.transferMoney(transferRequest);
		} catch (ApplicationException ex) {
			assertThat(ex.getMessage()).isNotEmpty();
		}
	}

	@Test
	public void transferMoneys() throws Exception {
		Account account;

		int min = 1;

		int max = 10000;

		TransferRequest transferRequest;
		Random random = new Random();
		String fromAccount;

		String toAccount;

		for (int i = min; i <= max; i++) {
			account = new Account(i + "");
			account.setBalance(new BigDecimal(1000));
			this.accountsService.createAccount(account);
		}
		
		for (int i = min; i < max; i++) {
			transferRequest = new TransferRequest();
			fromAccount = random.nextInt((max - min) + 1) + min + "";
			toAccount = random.nextInt((max - min) + 1) + min + "";
			if (fromAccount.compareTo(toAccount) == 0) {
				toAccount = random.nextInt((max - min) + 1) + min + "";
			}

			transferRequest.setAccountFrom(fromAccount);

			transferRequest.setAccountTo(toAccount);

			transferRequest.setAmount(new BigDecimal(10));

			this.accountsService.transferMoney(transferRequest);

		}

	}
}
