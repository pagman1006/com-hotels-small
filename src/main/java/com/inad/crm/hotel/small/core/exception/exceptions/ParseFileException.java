package com.inad.crm.hotel.small.core.exception.exceptions;

import java.io.Serial;

public class ParseFileException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = 1L;

	public ParseFileException(String message) {
		super(message);
	}
}
