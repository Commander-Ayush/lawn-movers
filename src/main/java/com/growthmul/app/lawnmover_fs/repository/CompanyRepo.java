package com.growthmul.app.lawnmover_fs.repository;

import com.growthmul.app.lawnmover_fs.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepo extends JpaRepository<Company, Long> {
}
