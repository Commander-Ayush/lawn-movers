// BookingRepository.java
package com.growthmul.app.lawnmover_fs.repository;

import com.growthmul.app.lawnmover_fs.entity.BookingRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookingRepository extends JpaRepository<BookingRequest, Long> {
    List<BookingRequest> findByCompanyIdOrderBySubmittedAtDesc(Long companyId);
    List<BookingRequest> findByCompanyIdAndCompleted(Long companyId, boolean completed);
    long countByCompanyId(Long companyId);
}