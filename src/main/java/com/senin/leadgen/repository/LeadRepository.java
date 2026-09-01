package com.senin.leadgen.repository;

import com.senin.leadgen.domain.Lead;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LeadRepository extends JpaRepository<Lead, Long> {
    Optional<Lead> findByPlaceId(String placeId);
}