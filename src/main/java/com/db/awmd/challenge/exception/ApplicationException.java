package com.db.awmd.challenge.exception;

/**
 * @author Dhananjay Jadhav
 * 
 *         THis is generic exception for any error
 *
 */
public class ApplicationException extends RuntimeException {

	public ApplicationException(String message) {
		super(message);
	}
}
