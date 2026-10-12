package com.example.tsumugu.exception;

/** すでに存在するなど、状態が衝突するとき(409) */
public class ConflictException extends RuntimeException {
	public ConflictException(String message) {
		super(message);
	}
}
