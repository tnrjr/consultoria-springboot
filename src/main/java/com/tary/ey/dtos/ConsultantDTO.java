package com.tary.ey.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ConsultantDTO(
        Long id,

        @NotBlank(message = "nome é obrigatório")
        @Size(max = 120, message = "nome deve ter no máximo 120 caracteres")
        String nome,

        @NotBlank(message = "email é obrigatório")
        @Size(max = 180, message = "email deve ter no máximo 180 caracteres")
        String email,

        @NotBlank(message = "especialidade é obrigatória")
        @Size(max = 80, message = "especialidade deve ter no máximo 80 caracteres")
        String especialidade
) {}
