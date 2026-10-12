package com.example.tsumugu.exception;

/** 権限がなく、操作できないとき(403) */
public class ForbiddenException extends RuntimeException {
	public ForbiddenException(String message) {
		super(message);
	}
}
