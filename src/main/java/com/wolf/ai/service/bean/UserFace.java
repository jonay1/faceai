package com.wolf.ai.service.bean;

import lombok.Data;

@Data
public class UserFace {
	private double left;
	private double top;
	private double width;
	private double height;
	private int rotation;
	private String sex;
	private int age;
}
