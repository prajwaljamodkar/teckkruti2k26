package com.teckkruti.worker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "job_posts")
public class JobPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Category is required")
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String category;

    @NotBlank(message = "Description is required")
    @Size(max = 500)
    @Column(nullable = false, length = 500)
    private String description;

    @Min(0)
    @Column
    private Integer budget;

    @NotBlank(message = "Date needed is required")
    @Column(nullable = false, length = 20)
    private String dateNeeded;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.OPEN;

    public enum Status { OPEN, ASSIGNED, CLOSED }

    /* ── Constructors ── */
    public JobPost() {}

    /* ── Getters & Setters ── */
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getBudget() { return budget; }
    public void setBudget(Integer budget) { this.budget = budget; }

    public String getDateNeeded() { return dateNeeded; }
    public void setDateNeeded(String dateNeeded) { this.dateNeeded = dateNeeded; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}
