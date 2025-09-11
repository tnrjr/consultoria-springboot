package com.tary.ey.repositories;

import com.tary.ey.domain.Project;
import com.tary.ey.domain.ProjectStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    Page<Project> findByStatus(ProjectStatus status, Pageable pageable);
    Page<Project> findByNomeContainingIgnoreCase(String fragment, Pageable pageable);
}