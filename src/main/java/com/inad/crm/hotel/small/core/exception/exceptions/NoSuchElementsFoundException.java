package com.inad.crm.hotel.small.core.exception.exceptions;

import java.io.Serial;

public class NoSuchElementsFoundException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = 1L;

	public NoSuchElementsFoundException(String message) {
		super(message);
	}
}
