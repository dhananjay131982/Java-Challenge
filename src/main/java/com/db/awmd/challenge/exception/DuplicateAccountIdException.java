package com.db.awmd.challenge.exception;

/**
 * @author Dhananjay Jadhav
 * 
 *         This exception is used when account id is duplicate
 *
 */
public class DuplicateAccountIdException extends RuntimeException {

	public DuplicateAccountIdException(String message) {
		super(message);
	}
}
