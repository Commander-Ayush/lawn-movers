// ServiceRepository.java
package com.growthmul.app.lawnmover_fs.repository;

import com.growthmul.app.lawnmover_fs.entity.ServiceOffering;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ServiceRepository extends JpaRepository<ServiceOffering, Long> {
    List<ServiceOffering> findByCompanyIdAndTypeOrderBySortOrder(Long companyId, String type);
}