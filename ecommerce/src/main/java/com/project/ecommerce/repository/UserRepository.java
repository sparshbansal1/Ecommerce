package com.project.ecommerce.repository;


import com.project.ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    //  This method searches the database for a user with the exact username you provide.
    Optional<User> findByUsername(String username);

    // This is a quick check to see if a username is already taken.
    boolean existsByUsername(String username);

    // This is a quick check to see if an email is already in use.
    boolean existsByEmail(String email);
}
