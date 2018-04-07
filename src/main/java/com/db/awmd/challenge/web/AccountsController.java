package com.db.awmd.challenge.web;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.TransferRequest;
import com.db.awmd.challenge.exception.ApplicationException;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.service.AccountsService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Dhananjay Jadhav
 * 
 *         Account Controller Class
 *
 */
@RestController
@RequestMapping("/v1/accounts")
@Slf4j
public class AccountsController {

	private final AccountsService accountsService;

	@Autowired
	public AccountsController(AccountsService accountsService) {
		this.accountsService = accountsService;
	}

	/**
	 * This method us used to create a new account
	 * 
	 * @param account
	 * @return
	 */
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

	/**
	 * This method is used to retrieve account from memory
	 * 
	 * @param accountId
	 * @return
	 */
	@GetMapping(path = "/{accountId}")
	public Account getAccount(@PathVariable String accountId) {
		log.info("Retrieving account for id {}", accountId);
		return this.accountsService.getAccount(accountId);
	}

	/**
	 * This method is used to transfer money from one account to another
	 * 
	 * @param transferRequest
	 * @return
	 */
	@PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "/transfer")
	public ResponseEntity<Object> transferMoney(@RequestBody @Valid TransferRequest transferRequest) {
		log.info("transferMoney account {}", transferRequest);

		try {
			this.accountsService.transferMoney(transferRequest);

		} catch (ApplicationException daie) {
			return new ResponseEntity<>(daie.getMessage(), HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

}
