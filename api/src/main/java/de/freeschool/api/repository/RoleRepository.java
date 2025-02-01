package de.freeschool.api.repository;

import de.freeschool.api.models.Role;
import de.freeschool.api.models.type.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    Optional<Role> findByName(RoleType name);
}
