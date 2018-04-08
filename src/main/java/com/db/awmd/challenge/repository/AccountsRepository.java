package com.db.awmd.challenge.repository;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.TransferRequest;
import com.db.awmd.challenge.exception.ApplicationException;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;

/**
 * @author Dhananjay Jadhav
 * 
 * Account reposiroty class
 *
 */
public interface AccountsRepository {

  void createAccount(Account account) throws DuplicateAccountIdException;

  Account getAccount(String accountId);

  void clearAccounts();
  
  void transferMoney(TransferRequest transferRequest) throws ApplicationException;
}
