package com.tary.ey.dtos;

import com.tary.ey.domain.ProjectStatus;
import java.time.LocalDate;
import java.util.Set;

public record ProjectDTO (
    Long id,
    String nome,
    String Descricao,
    LocalDate dataInicio,
    LocalDate dataFim,
    ProjectStatus status,
    Set<Long> consultoresIds
){}