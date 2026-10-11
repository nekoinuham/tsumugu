package com.example.tsumugu.exception;

/** 対象が見つからないとき(404) */
public class ResourceNotFoundException extends RuntimeException {
	public ResourceNotFoundException(String message) {
		super(message);
	}
}
