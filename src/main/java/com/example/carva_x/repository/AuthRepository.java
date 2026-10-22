package com.example.carva_x.repository;

import com.example.carva_x.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthRepository extends JpaRepository<User, Integer> {

    @Override
    User save(User user);

    Optional<User> findUserByEmail(String email);
}
