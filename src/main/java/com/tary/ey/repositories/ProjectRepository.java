package com.tary.ey.repositories;

import com.tary.ey.domain.Project;
import com.tary.ey.domain.ProjectStatus;
import org.hibernate.query.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.print.Pageable;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByStatus(ProjectStatus status);

    List<Project> findByNomeContainingIgnoreCase(String fragment);


    //Page<Project> findByStatus(ProjectStatus status, Pageable pageable);

}
