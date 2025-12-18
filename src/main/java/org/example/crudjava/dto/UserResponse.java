package org.example.crudjava.dto;

import java.time.Instant;

public record UserResponse(Long id, String name, String email, boolean active, Instant createdAt,
        Instant updatedAt) {
}
