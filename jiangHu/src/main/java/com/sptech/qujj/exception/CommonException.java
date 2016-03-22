package com.sptech.qujj.exception;

public class CommonException extends Exception {

	private static final long serialVersionUID = 1L;

	public CommonException(String msg) {
		super(msg);
	}

	public CommonException(Exception cause) {
		super(cause);
	}

	public CommonException(String msg, Exception cause) {
		super(msg, cause);
	}

}
