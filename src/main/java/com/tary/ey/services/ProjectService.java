package com.tary.ey.services;

import com.tary.ey.domain.Consultant;
import com.tary.ey.domain.Project;
import com.tary.ey.domain.ProjectStatus;
import com.tary.ey.dtos.ProjectDTO;
import com.tary.ey.repositories.ConsultantRepository;
import com.tary.ey.repositories.ProjectRepository;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.copy;


@Service
public class ProjectService {

    private final ProjectRepository projects;
    private final ConsultantRepository consultants;

    public ProjectService(ProjectRepository projects, ConsultantRepository consultants){
        this.projects = projects;
        this.consultants = consultants;
    }

    public java.util.List<ProjectDTO> list(ProjectStatus status){
        var data = (status == null) ? projects.findAll() : projects.findByStatus(status);
        return data.stream().map(this::toDTO).toList();
    }

    public ProjectDTO get(Long id) {
        var p = projects.findById(id).orElseThrow(() -> new IllegalArgumentException("Projeto não encontrado"));
        return toDTO(p);
    }

    @Transactional
    public ProjectDTO create(ProjectDTO dto) {
        var p = new Project();
        copy(dto, p);
        return toDTO(projects.save(p));
    }

    @Transactional
    public ProjectDTO update(Long id, ProjectDTO dto) {
        var p = projects.findById(id).orElseThrow(() -> new IllegalArgumentException("Projeto não encontrado"));
        copy(dto, p);
        return toDTO(projects.save(p));
    }

    @Transactional
    public void delete(Long id) {
        projects.deleteById(id);
    }

    @Transactional
    public ProjectDTO assignConsultant(Long projectId, Long consultantId) {
        var p = projects.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Projeto não encontrado"));
        var c = consultants.findById(consultantId)
                .orElseThrow(() -> new IllegalArgumentException("Consultor não encontrado"));
        p.getConsultores().add(c);
        return toDTO(projects.save(p));
    }


    @Transactional
    public ProjectDTO unassignConsultant(Long projectId, Long consultantId) {
        var p = projects.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));
        p.getConsultores().removeIf(c -> c.getId().equals(consultantId));
        return toDTO(projects.save(p));
    }



    // --- helpers simples

    /** Copia os campos do DTO para a entidade. */
    private void copy(ProjectDTO dto, Project p) {
        p.setNome(dto.nome());
        p.setDescricao(dto.descricao());
        p.setDataInicio(dto.dataInicio());
        p.setDataFim(dto.dataFim());
        p.setStatus(dto.status());


        if (dto.consultoresIds() != null) {
            Set<Consultant> set = dto.consultoresIds().stream()
                    .map(id -> consultants.findById(id).orElseThrow(() -> new IllegalArgumentException("Consultor não encontrado" + id)))
                    .collect(Collectors.toSet());
            p.setConsultores(set);
        }
    }


    private ProjectDTO toDTO(Project p) {
        var set = p.getConsultores() == null ? Set.<Consultant>of() : p.getConsultores();
        Set<Long> ids = set.stream().map(Consultant::getId).collect(Collectors.toSet());
        return new ProjectDTO(p.getId(), p.getNome(), p.getDescricao(), p.getDataInicio(), p.getDataFim(), p.getStatus(), ids);
    }
}
