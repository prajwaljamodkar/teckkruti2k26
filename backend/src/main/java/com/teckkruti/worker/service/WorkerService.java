package com.teckkruti.worker.service;

import com.teckkruti.worker.model.HireRequest;
import com.teckkruti.worker.model.JobPost;
import com.teckkruti.worker.model.Worker;
import com.teckkruti.worker.repository.HireRequestRepository;
import com.teckkruti.worker.repository.JobPostRepository;
import com.teckkruti.worker.repository.WorkerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class WorkerService {

    private final WorkerRepository workerRepository;
    private final HireRequestRepository hireRequestRepository;
    private final JobPostRepository jobPostRepository;

    public WorkerService(WorkerRepository workerRepository,
                         HireRequestRepository hireRequestRepository,
                         JobPostRepository jobPostRepository) {
        this.workerRepository      = workerRepository;
        this.hireRequestRepository = hireRequestRepository;
        this.jobPostRepository     = jobPostRepository;
    }

    /* ── Workers ── */

    public List<Worker> getAllWorkers() {
        return workerRepository.findAll();
    }

    public Optional<Worker> getWorkerById(Long id) {
        return workerRepository.findById(id);
    }

    public List<Worker> getWorkersByCategory(String category) {
        return workerRepository.findByCategory(category);
    }

    public List<Worker> getAvailableWorkers() {
        return workerRepository.findByAvailable(true);
    }

    /* ── Hire Requests ── */

    /**
     * Creates a hire request and marks the worker as unavailable.
     * Returns the persisted {@link HireRequest}.
     *
     * @throws IllegalArgumentException if the worker is not found or is unavailable.
     */
    public HireRequest createHireRequest(HireRequest request) {
        Worker worker = workerRepository.findById(request.getWorkerId())
                .orElseThrow(() -> new IllegalArgumentException("Worker not found: " + request.getWorkerId()));

        if (!worker.isAvailable()) {
            throw new IllegalStateException("Worker is currently unavailable.");
        }

        worker.setAvailable(false);
        workerRepository.save(worker);

        request.setStatus(HireRequest.Status.CONFIRMED);
        return hireRequestRepository.save(request);
    }

    public List<HireRequest> getHireRequestsByWorker(Long workerId) {
        return hireRequestRepository.findByWorkerId(workerId);
    }

    /* ── Job Posts ── */

    public JobPost createJobPost(JobPost jobPost) {
        return jobPostRepository.save(jobPost);
    }

    public List<JobPost> getAllJobPosts() {
        return jobPostRepository.findAll();
    }

    public List<JobPost> getOpenJobPosts() {
        return jobPostRepository.findByStatus(JobPost.Status.OPEN);
    }
}
