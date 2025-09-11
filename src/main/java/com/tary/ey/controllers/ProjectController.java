package com.tary.ey.controllers;

import com.tary.ey.domain.ProjectStatus;
import com.tary.ey.dtos.AssignRequest;
import com.tary.ey.dtos.ProjectDTO;
import com.tary.ey.services.ProjectService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService service;

    public ProjectController(ProjectService service) {
        this.service = service;
    }

    @GetMapping
    public Page<ProjectDTO> list(
            @RequestParam(required = false) ProjectStatus status,
            Pageable pageable
    ) {
        return service.list(status, pageable);
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectDTO create(@RequestBody @Valid ProjectDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public ProjectDTO update(@PathVariable Long id, @RequestBody @Valid ProjectDTO dto) {
        return service.update(id, dto);
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @PostMapping("/{id}/assign")
    public ProjectDTO assign(@PathVariable Long id, @RequestBody @Valid AssignRequest req) {
        return service.assignConsultant(id, req.consultantId());
    }

    @PostMapping("/{id}/unassign")
    public ProjectDTO unassign(@PathVariable Long id, @RequestBody @Valid AssignRequest req) {
        return service.unassignConsultant(id, req.consultantId());
    }
}
