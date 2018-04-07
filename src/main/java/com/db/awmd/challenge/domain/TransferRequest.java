package com.db.awmd.challenge.domain;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author Dhananjay Jadhav
 * 
 *         This class holds inforamtion related to transfer money
 *
 */
public class TransferRequest {
	@NotNull
	@NotEmpty
	String accountFrom;

	@NotNull
	@NotEmpty
	String accountTo;

	@NotNull
	@DecimalMin("0.0")
	BigDecimal amount;

	public String getAccountFrom() {
		return accountFrom;
	}

	public void setAccountFrom(String accountFrom) {
		this.accountFrom = accountFrom;
	}

	public String getAccountTo() {
		return accountTo;
	}

	public void setAccountTo(String accountTo) {
		this.accountTo = accountTo;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

}
