package com.example.tsumugu.exception;

/** リクエストの内容が正しくないとき(400) */
public class BadRequestException extends RuntimeException {
	public BadRequestException(String message) {
		super(message);
	}
}
