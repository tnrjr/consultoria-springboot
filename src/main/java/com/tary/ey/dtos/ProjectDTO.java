package com.tary.ey.dtos;

import com.tary.ey.domain.ProjectStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Set;

public record ProjectDTO (
    Long id,

    @NotBlank(message = "nome é obrigatório")
    String nome,

    @NotBlank(message = "descrição é obrigatória")
    String descricao,

    @NotBlank(message = "dataInicio é obrigatória")
    @PastOrPresent(message = "dataInicio não pode ser no futuro")
    LocalDate dataInicio,

    @FutureOrPresent(message = "dataFim não pode ser no passado")
    LocalDate dataFim,

    @NotBlank(message = "status é obrigatório")
    ProjectStatus status,

    Set<Long> consultoresIds
){}