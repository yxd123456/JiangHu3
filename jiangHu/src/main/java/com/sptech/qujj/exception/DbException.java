package com.sptech.qujj.exception;

/**
 * HTTP StatusCode is not 200
 */
public class DbException extends Exception {

	private static final long serialVersionUID = 1L;
	private int statusCode = -1;

	public DbException(String msg) {
		super(msg);
	}

	public DbException(Exception cause) {
		super(cause);
	}

	public DbException(String msg, int statusCode) {
		super(msg);
		this.statusCode = statusCode;
	}

	public DbException(String msg, Exception cause) {
		super(msg, cause);
	}

	public DbException(String msg, Exception cause, int statusCode) {
		super(msg, cause);
		this.statusCode = statusCode;
	}

	public int getStatusCode() {
		return this.statusCode;
	}

}
