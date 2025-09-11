package com.tary.ey.repositories;

import com.tary.ey.domain.Consultant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConsultantRepository extends JpaRepository<Consultant, Long> {
    boolean existsByEmailIgnoreCase(String email);
    Optional<Consultant> findByEmailIgnoreCase(String email);
    Page<Consultant> findAll(Pageable pageable); // opcional, já herdado, mas ok declarar
}