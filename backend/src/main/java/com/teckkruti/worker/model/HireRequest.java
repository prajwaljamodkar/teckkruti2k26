package com.teckkruti.worker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "hire_requests")
public class HireRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Worker ID is required")
    @Column(nullable = false)
    private Long workerId;

    @NotBlank(message = "Worker name is required")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String workerName;

    @NotBlank(message = "Client name is required")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String clientName;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "[+0-9\\s\\-]{7,15}", message = "Invalid phone number")
    @Column(nullable = false, length = 20)
    private String clientPhone;

    @NotBlank(message = "Job description is required")
    @Size(max = 500)
    @Column(nullable = false, length = 500)
    private String jobDescription;

    @NotBlank(message = "Scheduled date is required")
    @Column(nullable = false, length = 20)
    private String scheduledDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.PENDING;

    public enum Status { PENDING, CONFIRMED, COMPLETED, CANCELLED }

    /* ── Constructors ── */
    public HireRequest() {}

    /* ── Getters & Setters ── */
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getWorkerId() { return workerId; }
    public void setWorkerId(Long workerId) { this.workerId = workerId; }

    public String getWorkerName() { return workerName; }
    public void setWorkerName(String workerName) { this.workerName = workerName; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public String getClientPhone() { return clientPhone; }
    public void setClientPhone(String clientPhone) { this.clientPhone = clientPhone; }

    public String getJobDescription() { return jobDescription; }
    public void setJobDescription(String jobDescription) { this.jobDescription = jobDescription; }

    public String getScheduledDate() { return scheduledDate; }
    public void setScheduledDate(String scheduledDate) { this.scheduledDate = scheduledDate; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}
