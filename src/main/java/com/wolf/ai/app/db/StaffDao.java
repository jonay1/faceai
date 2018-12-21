package com.wolf.ai.app.db;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wolf.ai.app.db.entity.Staff;

public interface StaffDao extends JpaRepository<Staff, String> {

}
