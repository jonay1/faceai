package com.wolf.ai.app.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class Staff {
	@Id
	private String phone;
	private String name;
	private String region;
	@Column(columnDefinition = "CLOB")
	private String image;
	private Boolean faced;
	private Boolean signed;
	private String sex;
	private int age;

}
