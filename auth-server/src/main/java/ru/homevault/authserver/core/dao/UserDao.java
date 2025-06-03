package ru.homevault.authserver.core.dao;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.homevault.authserver.core.entity.User;

import java.util.Optional;

@Repository
public interface UserDao extends JpaRepository<User, Long> {
    Optional<User> findByUsername(@Size(max = 255) @NotNull String username);

    boolean existsByUsername(@Size(max = 255) @NotNull String username);
}
