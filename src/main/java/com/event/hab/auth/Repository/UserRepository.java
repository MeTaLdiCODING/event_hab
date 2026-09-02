package com.event.hab.auth.Repository;

import com.event.hab.auth.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

Optional<User> findByEmail(String email);
}
