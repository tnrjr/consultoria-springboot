package com.tary.ey.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(
    name = "projects",
    indexes = {
        @Index(name = "ix_project_status", columnList = "status"),
        @Index(name = "ix_project_data_inicio", columnList = "data_inicio")
    }
)


@NoArgsConstructor @AllArgsConstructor @Builder
@Getter @Setter
@EqualsAndHashCode(of = "id")
@ToString(exclude = "consultores")
public class    Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 160)
    private String nome;

    @Column(columnDefinition = "text")
    private String Descricao;

    @Column(name = "data_inico", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "data_fim")
    private LocalDate dataFim;

    @Enumerated
    @Column(nullable = false, length = 20)
    private ProjectStatus status;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "project_consultants",
            joinColumns = @JoinColumn(name = "project_id", foreignKey = @ForeignKey(name = "fk_pc_project")),
            inverseJoinColumns = @JoinColumn(name = "consultant_id", foreignKey = @ForeignKey(name = "fk_pc_consultant")),
            uniqueConstraints = @UniqueConstraint(name = "uk_pc_project_consultant", columnNames = {"project_id","consultant_id"})
    )

    private Set<Consultant> consultores = new HashSet<>();

}
