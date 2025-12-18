package org.example.crudjava.service;

import java.util.List;

import org.example.crudjava.dto.UserPatchRequest;
import org.example.crudjava.dto.UserRequest;
import org.example.crudjava.dto.UserResponse;
import org.example.crudjava.entity.UserEntity;
import org.example.crudjava.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {

    private static final String USER_NOT_FOUND_MESSAGE = "User not found";

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserResponse> getUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public UserResponse getUser(Long id) {
        return userRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND_MESSAGE));
    }

    public UserResponse createUser(UserRequest request) {
        UserEntity user = new UserEntity(request.name(), request.email(), request.password());
        UserEntity saved = userRepository.save(user);
        return toResponse(saved);
    }

    public UserResponse updateUser(Long id, UserRequest request) {
        UserEntity existing = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND_MESSAGE));
        existing.setName(request.name());
        existing.setEmail(request.email());
        existing.setPassword(request.password());
        UserEntity updated = userRepository.save(existing);
        return toResponse(updated);
    }

    public UserResponse patchUser(Long id, UserPatchRequest request) {
        UserEntity existing = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND_MESSAGE));
        if (request.name() != null) {
            existing.setName(request.name());
        }
        if (request.email() != null) {
            existing.setEmail(request.email());
        }
        if (request.password() != null) {
            existing.setPassword(request.password());
        }
        if (request.isActive() != null) {
            existing.setActive(request.isActive());
        }
        UserEntity patched = userRepository.save(existing);
        return toResponse(patched);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND_MESSAGE);
        }
        userRepository.deleteById(id);
    }

    private UserResponse toResponse(UserEntity entity) {
        return new UserResponse(entity.getId(), entity.getName(), entity.getEmail(), entity.isActive(),
                entity.getCreatedAt(), entity.getUpdatedAt());
    }
}
