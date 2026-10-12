package com.example.tsumugu.controller;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class RegisterRequest {
	@NotBlank(message = "メールアドレスを入力してください")
	@Email(message = "メールアドレスの形式が正しくありません")
	@Size(max = 255, message = "メールアドレスは255文字以内で入力してください")
	private String email;
	
	@NotBlank(message = "パスワードを入力してください")
	@Size(min = 8, max = 72, message ="パスワードは8～72文字で入力してください")
	private String password;
	
	@NotBlank(message = "表示名を入力してください")
	@Size(max = 20, message = "表示名は20文字以内で入力してください")
	private String displayName;
}
