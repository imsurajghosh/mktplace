package com.supaki.mktplace.repositories;

import com.supaki.mktplace.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRespository extends JpaRepository<User, Integer> {

    Optional<User> findByUserId(String userId);
}
