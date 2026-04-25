package unipi.d3fender.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import unipi.d3fender.models.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

        Optional<User> findByUsername(String username);

        boolean existsByUsername(String username);

        boolean existsByEmail(String email);
}

