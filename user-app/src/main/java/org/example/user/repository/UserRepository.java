package org.example.user.repository;

import lombok.NonNull;
import org.example.user.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    UserEntity findByLogin(String login);
    UserEntity findByLoginAndPassword(String login, String password);
    boolean existsById(@NonNull UUID id);
}
