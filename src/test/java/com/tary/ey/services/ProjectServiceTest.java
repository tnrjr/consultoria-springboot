package com.tary.ey.services;

import com.tary.ey.domain.Consultant;
import com.tary.ey.domain.Project;
import com.tary.ey.domain.ProjectStatus;
import com.tary.ey.dtos.ProjectDTO;
import com.tary.ey.repositories.ConsultantRepository;
import com.tary.ey.repositories.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProjectServiceTest {

    @Test
    void createProject_mapsDtoAndSaves() {
        // mocks
        ProjectRepository projectRepo = mock(ProjectRepository.class);
        ConsultantRepository consultantRepo = mock(ConsultantRepository.class);

        var service = new ProjectService(projectRepo, consultantRepo);

        // stubs
        var saved = Project.builder()
                .id(10L)
                .nome("Projeto EY")
                .dataInicio(LocalDate.of(2025, 1, 1))
                .status(ProjectStatus.PLANEJADO)
                .consultores(new HashSet<>()) // <- evita NPE
                .build();
        when(projectRepo.save(any(Project.class))).thenReturn(saved);

        // act
        var dto = new ProjectDTO(
                null, "Projeto EY", "Desc",
                LocalDate.of(2025, 1, 1), null,
                ProjectStatus.PLANEJADO, null
        );
        var result = service.create(dto);

        // assert
        assertNotNull(result.id());
        assertEquals("Projeto EY", result.nome());

        // captura o que foi salvo
        ArgumentCaptor<Project> captor = ArgumentCaptor.forClass(Project.class);
        verify(projectRepo).save(captor.capture());
        assertEquals("Projeto EY", captor.getValue().getNome());
        assertEquals(ProjectStatus.PLANEJADO, captor.getValue().getStatus());
    }

    @Test
    void assignConsultant_addsToSetAndSaves() {
        var projectRepo = mock(ProjectRepository.class);
        var consultantRepo = mock(ConsultantRepository.class);
        var service = new ProjectService(projectRepo, consultantRepo);

        var project = Project.builder()
                .id(1L)
                .nome("P1")
                .dataInicio(LocalDate.now())
                .status(ProjectStatus.PLANEJADO)
                .consultores(new HashSet<>()) // evita NPE
                .build();

        var consultant = Consultant.builder()
                .id(99L)
                .nome("Ana").email("ana@ey.com").especialidade("Java")
                .build();

        when(projectRepo.findById(1L)).thenReturn(Optional.of(project));
        when(consultantRepo.findById(99L)).thenReturn(Optional.of(consultant));
        when(projectRepo.save(any(Project.class))).thenAnswer(i -> i.getArgument(0));

        var after = service.assignConsultant(1L, 99L);

        assertTrue(after.consultoresIds().contains(99L));
    }


    @Test
    void get_whenNotFound_throws() {
        ProjectRepository projectRepo = mock(ProjectRepository.class);
        ConsultantRepository consultantRepo = mock(ConsultantRepository.class);
        var service = new ProjectService(projectRepo, consultantRepo);

        when(projectRepo.findById(123L)).thenReturn(Optional.empty());

        var ex = assertThrows(IllegalArgumentException.class, () -> service.get(123L));
        assertTrue(ex.getMessage().toLowerCase().contains("não encontrado"));
    }

    @Test
    void update_replacesFields_andConsultantsIfProvided() {
        ProjectRepository projectRepo = mock(ProjectRepository.class);
        ConsultantRepository consultantRepo = mock(ConsultantRepository.class);
        var service = new ProjectService(projectRepo, consultantRepo);

        var existing = Project.builder()
                .id(5L).nome("Old").dataInicio(LocalDate.of(2025,1,1))
                .status(ProjectStatus.PLANEJADO)
                .consultores(new HashSet<>()) // <- seguro
                .build();


        when(projectRepo.findById(5L)).thenReturn(Optional.of(existing));
        when(projectRepo.save(any(Project.class))).thenAnswer(i -> i.getArgument(0));

        var c1 = Consultant.builder().id(1L).nome("Ana").email("ana@ey.com").especialidade("Java").build();
        var c2 = Consultant.builder().id(2L).nome("Bruno").email("bruno@ey.com").especialidade("Dados").build();

        when(consultantRepo.findById(1L)).thenReturn(Optional.of(c1));
        when(consultantRepo.findById(2L)).thenReturn(Optional.of(c2));

        var dto = new ProjectDTO(
                null, "Novo Nome", "Nova Desc",
                LocalDate.of(2025,1,10), null, ProjectStatus.EM_ANDAMENTO,
                Set.of(1L, 2L)
        );

        var updated = service.update(5L, dto);

        assertEquals("Novo Nome", updated.nome());
        assertEquals(ProjectStatus.EM_ANDAMENTO, updated.status());
        assertEquals(Set.of(1L,2L), updated.consultoresIds());
    }
}
