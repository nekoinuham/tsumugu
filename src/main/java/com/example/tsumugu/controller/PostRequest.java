package com.example.tsumugu.controller;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostRequest {
    private String diaryText;
    private Integer mood;
    private Double sleepHours;
}