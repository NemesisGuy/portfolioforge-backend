package za.co.nemesisnet.portfolioforgebackend.repository;


import za.co.nemesisnet.portfolioforgebackend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring Data JPA will generate queries based on method names:

    /**
     * Finds a user by their username. Case-sensitive by default in most DBs.
     * Use findByUsernameIgnoreCase if needed.
     * @param username The username to search for.
     * @return An Optional containing the user if found.
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by their email address. Case-sensitive by default.
     * Use findByEmailIgnoreCase if needed.
     * @param email The email address to search for.
     * @return An Optional containing the user if found.
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a user exists with the given username.
     * @param username The username to check.
     * @return true if a user exists, false otherwise.
     */
    Boolean existsByUsername(String username);

    /**
     * Checks if a user exists with the given email address.
     * @param email The email to check.
     * @return true if a user exists, false otherwise.
     */
    Boolean existsByEmail(String email);
}
