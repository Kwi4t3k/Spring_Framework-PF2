package com.example.store.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {
    @Id
    @Column(nullable = false, unique = true)
    private String id;

    private String title;

    private String author;

    @Column(columnDefinition = "NUMERIC")
    private double price;

    @Column(name = "is_active")
    @Builder.Default
    private boolean isActive = true;
}
