package com.wolf.ai.service.bean;

import lombok.Data;

@Data
public class MatchUser {
	private String group_id;
	private String user_id;
	private String user_info;
	double score;
}
