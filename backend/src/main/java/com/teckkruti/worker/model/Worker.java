package com.teckkruti.worker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "workers")
public class Worker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Category is required")
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String category;

    @DecimalMin(value = "0.0")
    @DecimalMax(value = "5.0")
    @Column(nullable = false)
    private double rating;

    @Min(0)
    @Column(nullable = false)
    private int ratePerHour;

    @Column(nullable = false)
    private boolean available = true;

    @Size(max = 300)
    @Column(length = 300)
    private String bio;

    /* ── Constructors ── */
    public Worker() {}

    public Worker(String name, String category, double rating, int ratePerHour, boolean available, String bio) {
        this.name = name;
        this.category = category;
        this.rating = rating;
        this.ratePerHour = ratePerHour;
        this.available = available;
        this.bio = bio;
    }

    /* ── Getters & Setters ── */
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public int getRatePerHour() { return ratePerHour; }
    public void setRatePerHour(int ratePerHour) { this.ratePerHour = ratePerHour; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
}
