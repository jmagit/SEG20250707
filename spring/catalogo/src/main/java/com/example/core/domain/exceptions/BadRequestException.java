package com.example.core.domain.exceptions;

import java.io.Serial;

public class BadRequestException extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;
	
	public BadRequestException(String message) {
		super(message);
	}

	public BadRequestException(String message, Throwable cause) {
		super(message, cause);
	}

	public BadRequestException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
