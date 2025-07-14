package com.example.core.domain.exceptions;

import java.io.Serial;

public class NotFoundException extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;
	private final static String MESSAGE_STRING = "Not found";
	
	public NotFoundException() {
		this(MESSAGE_STRING);
	}

	public NotFoundException(String message) {
		super(message);
	}

	public NotFoundException(Throwable cause) {
		this(MESSAGE_STRING, cause);
	}

	public NotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
