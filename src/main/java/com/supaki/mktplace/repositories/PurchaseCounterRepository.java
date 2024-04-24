package com.supaki.mktplace.repositories;

import com.supaki.mktplace.entities.PurchaseCounter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PurchaseCounterRepository extends JpaRepository<PurchaseCounter, Integer> {

    Optional<PurchaseCounter> findByCounterKey(String counterKey);
}
