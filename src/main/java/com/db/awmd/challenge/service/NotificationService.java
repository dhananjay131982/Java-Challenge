package com.db.awmd.challenge.service;

import com.db.awmd.challenge.domain.Account;


/**
 * @author Dhananjay Jadhav
 *
 */
public interface NotificationService {

  void notifyAboutTransfer(Account account, String transferDescription);
}
