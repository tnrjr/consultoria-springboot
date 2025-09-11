package com.tary.ey.dtos;

import com.tary.ey.domain.ProjectStatus;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.Set;

public record ProjectDTO (
    Long id,

    @NotBlank(message = "nome é obrigatório")
    String nome,

    @NotBlank(message = "descrição é obrigatória")
    String descricao,

    @NotNull(message = "dataInicio é obrigatória")
    @PastOrPresent(message = "dataInicio não pode ser no futuro")
    LocalDate dataInicio,

    @FutureOrPresent(message = "dataFim não pode ser no passado")
    LocalDate dataFim,

    @NotNull(message = "status é obrigatório")
    ProjectStatus status,

    Set<Long> consultoresIds
){}