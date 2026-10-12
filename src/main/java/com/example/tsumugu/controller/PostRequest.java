package com.example.tsumugu.controller;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostRequest {
	@Size(max = 255, message = "日記は255文字以内で入力してください")
    private String diaryText;
	
	@Min(value = 1, message = "気分は1～5で入力してください")
	@Max(value = 5, message = "気分は1～5で入力してください")
    private Integer mood;
	
	@DecimalMin(value = "0.0", message = "睡眠時間は0～24時間で入力してください")
	@DecimalMax(value = "24.0", message = "睡眠時間は0～24時間で入力してください")
    private Double sleepHours;
    private Boolean isPublic;
}
