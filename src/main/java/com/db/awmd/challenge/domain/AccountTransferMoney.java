package com.db.awmd.challenge.domain;

import java.math.BigDecimal;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AccountTransferMoney {
	@NotNull
	@NotEmpty
	private String accountFromId;
	@NotNull
	@NotEmpty
	private String accountToId;
	@NotNull
	@Min(value = 0, message = "Transfer amount must be positive.")
	private BigDecimal amount;

	@JsonCreator
	public AccountTransferMoney(@JsonProperty("accountFromId") String accountFromId,
			@JsonProperty("accountToId") String accountToId, @JsonProperty("amount") BigDecimal amount) {
		super();
		this.accountFromId = accountFromId;
		this.accountToId = accountToId;
		this.amount = amount;
	}

	public String getAccountFromId() {
		return accountFromId;
	}

	public void setAccountFromId(String accountFromId) {
		this.accountFromId = accountFromId;
	}

	public String getAccountToId() {
		return accountToId;
	}

	public void setAccountToId(String accountToId) {
		this.accountToId = accountToId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

}
