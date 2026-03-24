package com.teckkruti.worker.repository;

import com.teckkruti.worker.model.JobPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobPostRepository extends JpaRepository<JobPost, Long> {
    List<JobPost> findByCategory(String category);
    List<JobPost> findByStatus(JobPost.Status status);
}
