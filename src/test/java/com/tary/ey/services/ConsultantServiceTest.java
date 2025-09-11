package com.tary.ey.services;

import com.tary.ey.domain.Consultant;
import com.tary.ey.dtos.ConsultantDTO;
import com.tary.ey.repositories.ConsultantRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConsultantServiceTest {

    @Test
    void create_thenGet() {
        var repo = mock(ConsultantRepository.class);
        var svc = new ConsultantService(repo);

        var saved = Consultant.builder()
                .id(10L).nome("Ana").email("ana@ey.com").especialidade("Java").build();

        when(repo.save(any(Consultant.class))).thenReturn(saved);
        when(repo.findById(10L)).thenReturn(Optional.of(saved));

        var dto = new ConsultantDTO(null, "Ana", "ana@ey.com", "Java");
        var created = svc.create(dto);

        assertNotNull(created.id());
        assertEquals("Ana", created.nome());

        var fetched = svc.get(10L);
        assertEquals("ana@ey.com", fetched.email());
    }
}
