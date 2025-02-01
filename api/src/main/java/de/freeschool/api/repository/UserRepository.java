package de.freeschool.api.repository;

import de.freeschool.api.models.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByUid(String uid);

    Boolean existsByUsername(String username);

    Optional<UserEntity> findByEmail(String email);

    Page<UserEntity> findAll(Pageable pageable);

    Optional<UserEntity> findByTelegramCode(String linkUpCode);
}
