package com.tary.ey.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(
        name = "consultants",
        indexes = {
                @Index(name = "ix_consultant_email", columnList = "email")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_consultant_email", columnNames = "email")
        }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(of = "id")
@ToString
public class Consultant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String nome;

    @Email
    @NotBlank
    @Column(nullable = false, unique = true, length = 160)
    private String email;

    @NotBlank
    @Column(nullable = false, length = 80)
    private String especialidade;
}
