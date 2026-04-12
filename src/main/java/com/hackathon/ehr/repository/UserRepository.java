package com.hackathon.ehr.repository;

import com.hackathon.ehr.entity.User;
import com.hackathon.ehr.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByRole(RoleType role);
}
