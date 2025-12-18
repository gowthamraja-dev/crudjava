package org.example.crudjava.dto;

public record UserPatchRequest(String name, String email, String password, Boolean isActive) {
}
