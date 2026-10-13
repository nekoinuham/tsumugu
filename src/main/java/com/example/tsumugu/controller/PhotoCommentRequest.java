package com.example.tsumugu.controller;

import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

/**
 * 写真のコメントの変更リクエスト
 * 空文字やnullを送ると、コメントを消す
 */
@Getter
@Setter
public class PhotoCommentRequest {

	@Size(max = 255, message = "写真のコメントは255文字以内で入力してください")
	private String comment;
}
