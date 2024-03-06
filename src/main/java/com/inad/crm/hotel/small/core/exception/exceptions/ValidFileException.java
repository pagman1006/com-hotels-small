package com.inad.crm.hotel.small.core.exception.exceptions;

import java.io.Serial;

public class ValidFileException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = 1L;

	public ValidFileException(String message) {
		super(message);
	}
}
