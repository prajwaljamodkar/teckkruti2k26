package com.teckkruti.worker.repository;

import com.teckkruti.worker.model.HireRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HireRequestRepository extends JpaRepository<HireRequest, Long> {
    List<HireRequest> findByWorkerId(Long workerId);
    List<HireRequest> findByClientPhone(String clientPhone);
}
