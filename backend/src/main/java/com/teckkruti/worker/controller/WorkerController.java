package com.teckkruti.worker.controller;

import com.teckkruti.worker.model.HireRequest;
import com.teckkruti.worker.model.JobPost;
import com.teckkruti.worker.model.Worker;
import com.teckkruti.worker.service.WorkerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class WorkerController {

    private final WorkerService workerService;

    public WorkerController(WorkerService workerService) {
        this.workerService = workerService;
    }

    /* ─────────── Workers ─────────── */

    /** GET /api/workers – list all workers, optionally filtered by category */
    @GetMapping("/workers")
    public List<Worker> getWorkers(@RequestParam(required = false) String category) {
        if (category != null && !category.isBlank()) {
            return workerService.getWorkersByCategory(category);
        }
        return workerService.getAllWorkers();
    }

    /** GET /api/workers/{id} – get a single worker */
    @GetMapping("/workers/{id}")
    public ResponseEntity<Worker> getWorker(@PathVariable Long id) {
        return workerService.getWorkerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** GET /api/workers/available – list available workers only */
    @GetMapping("/workers/available")
    public List<Worker> getAvailableWorkers() {
        return workerService.getAvailableWorkers();
    }

    /* ─────────── Hire Requests ─────────── */

    /** POST /api/hire – submit a hire request */
    @PostMapping("/hire")
    public ResponseEntity<?> hireWorker(@Valid @RequestBody HireRequest request) {
        try {
            HireRequest saved = workerService.createHireRequest(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /** GET /api/hire/worker/{workerId} – get hire requests for a worker */
    @GetMapping("/hire/worker/{workerId}")
    public List<HireRequest> getHireRequestsByWorker(@PathVariable Long workerId) {
        return workerService.getHireRequestsByWorker(workerId);
    }

    /* ─────────── Job Posts ─────────── */

    /** POST /api/jobs – post a new job */
    @PostMapping("/jobs")
    public ResponseEntity<JobPost> postJob(@Valid @RequestBody JobPost jobPost) {
        JobPost saved = workerService.createJobPost(jobPost);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /** GET /api/jobs – list open job posts */
    @GetMapping("/jobs")
    public List<JobPost> getJobs(@RequestParam(defaultValue = "false") boolean all) {
        return all ? workerService.getAllJobPosts() : workerService.getOpenJobPosts();
    }
}
