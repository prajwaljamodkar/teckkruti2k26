package com.teckkruti.worker.repository;

import com.teckkruti.worker.model.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkerRepository extends JpaRepository<Worker, Long> {
    List<Worker> findByCategory(String category);
    List<Worker> findByAvailable(boolean available);
    List<Worker> findByCategoryAndAvailable(String category, boolean available);
}
