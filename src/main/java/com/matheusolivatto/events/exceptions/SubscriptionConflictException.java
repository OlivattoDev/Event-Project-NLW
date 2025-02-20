package com.matheusolivatto.events.exceptions;

public class SubscriptionConflictException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public SubscriptionConflictException(String msg) {
		super(msg);
	}

}
