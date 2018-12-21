package com.wolf.ai.service.bean;

import com.wolf.ai.util.CommonUtil;

import lombok.Data;

@Data
public class Message {
	private Event event;
	private Object data;

	public static enum Event {
		CONNECT, C2S, S2C
	}

	public String toString() {
		return CommonUtil.toJson(this);
	}
}
