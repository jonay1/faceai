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
	@Column(length=20)
	private String name;
	@Column(length=10)
	private String region;
	@Column(length=255)
	private String image;
	private Boolean faced;
	private Boolean signed;
	@Column(length=10)
	private String sex;
	private int age;

}
