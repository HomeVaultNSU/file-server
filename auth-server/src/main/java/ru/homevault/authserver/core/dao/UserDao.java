package ru.homevault.authserver.core.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.homevault.authserver.core.entity.User;

@Repository
public interface UserDao extends JpaRepository<User, Long> {
}
