package com.teckkruti.worker;

import com.teckkruti.worker.model.HireRequest;
import com.teckkruti.worker.model.JobPost;
import com.teckkruti.worker.model.Worker;
import com.teckkruti.worker.repository.HireRequestRepository;
import com.teckkruti.worker.repository.JobPostRepository;
import com.teckkruti.worker.repository.WorkerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class WorkerApplicationTests {

    @Autowired MockMvc mockMvc;
    @Autowired WorkerRepository workerRepository;
    @Autowired HireRequestRepository hireRequestRepository;
    @Autowired JobPostRepository jobPostRepository;

    @BeforeEach
    void setUp() {
        hireRequestRepository.deleteAll();
        jobPostRepository.deleteAll();
        workerRepository.deleteAll();

        workerRepository.save(new Worker("Test Plumber", "Plumber", 4.5, 300, true,  "Test bio"));
        workerRepository.save(new Worker("Busy Worker",  "Cleaner", 4.0, 200, false, "Busy bio"));
    }

    /* ── GET /api/workers ── */

    @Test
    void getWorkers_returnsAllWorkers() throws Exception {
        mockMvc.perform(get("/api/workers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getWorkers_filterByCategory() throws Exception {
        mockMvc.perform(get("/api/workers").param("category", "Plumber"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Test Plumber")));
    }

    @Test
    void getWorker_unknownId_returns404() throws Exception {
        mockMvc.perform(get("/api/workers/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAvailableWorkers_returnsOnlyAvailable() throws Exception {
        mockMvc.perform(get("/api/workers/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].available", everyItem(is(true))));
    }

    /* ── POST /api/hire ── */

    @Test
    void hireWorker_validRequest_returns201() throws Exception {
        Long workerId = workerRepository.findByCategory("Plumber").get(0).getId();

        String body = String.format("""
                {
                  "workerId": %d,
                  "workerName": "Test Plumber",
                  "clientName": "Alice",
                  "clientPhone": "9876543210",
                  "jobDescription": "Fix kitchen sink",
                  "scheduledDate": "2026-04-01"
                }""", workerId);

        mockMvc.perform(post("/api/hire")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("CONFIRMED")));
    }

    @Test
    void hireWorker_busyWorker_returns409() throws Exception {
        Long workerId = workerRepository.findByAvailable(false).get(0).getId();

        String body = String.format("""
                {
                  "workerId": %d,
                  "workerName": "Busy Worker",
                  "clientName": "Bob",
                  "clientPhone": "1234567890",
                  "jobDescription": "Clean house",
                  "scheduledDate": "2026-04-05"
                }""", workerId);

        mockMvc.perform(post("/api/hire")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict());
    }

    @Test
    void hireWorker_unknownWorker_returns404() throws Exception {
        String body = """
                {
                  "workerId": 9999,
                  "workerName": "Ghost",
                  "clientName": "Charlie",
                  "clientPhone": "0000000000",
                  "jobDescription": "Something",
                  "scheduledDate": "2026-05-01"
                }""";

        mockMvc.perform(post("/api/hire")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    void hireWorker_missingFields_returns400() throws Exception {
        mockMvc.perform(post("/api/hire")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    /* ── POST /api/jobs ── */

    @Test
    void postJob_validRequest_returns201() throws Exception {
        String body = """
                {
                  "category": "Plumber",
                  "description": "Fix bathroom pipe",
                  "budget": 500,
                  "dateNeeded": "2026-04-10"
                }""";

        mockMvc.perform(post("/api/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("OPEN")));
    }

    @Test
    void getJobs_returnsOpenJobs() throws Exception {
        JobPost job = new JobPost();
        job.setCategory("Electrician");
        job.setDescription("Install lights");
        job.setDateNeeded("2026-04-15");
        jobPostRepository.save(job);

        mockMvc.perform(get("/api/jobs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }
}
