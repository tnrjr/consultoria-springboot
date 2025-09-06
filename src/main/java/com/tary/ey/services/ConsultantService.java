package com.tary.ey.services;

import com.tary.ey.domain.Consultant;
import com.tary.ey.dtos.ConsultantDTO;
import com.tary.ey.repositories.ConsultantRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConsultantService {

    private final ConsultantRepository repo;

    public ConsultantService(ConsultantRepository repo){
        this.repo = repo;
    }

    public List<ConsultantDTO> list(){
        return repo.findAll().stream().map(this::toDTO).toList();
    }

    public ConsultantDTO get(Long id) {
        var c = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Consultor não encontrado"));
        return toDTO(c);
    }

    @Transactional
    public ConsultantDTO create(ConsultantDTO dto) {
        var c = new Consultant();
        c.setNome(dto.nome());
        c.setEmail(dto.email());
        c.setEspecialidade(dto.especialidade());
        return toDTO(repo.save(c));
    }

    @Transactional
    public ConsultantDTO update(Long id, ConsultantDTO dto){
        var c =  repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Consultor não encontrado"));
        c.setNome(dto.nome());
        c.setEmail(dto.email());
        c.setEspecialidade(dto.especialidade());
        return toDTO(repo.save(c));
    }

    @Transactional
    public void delete(Long id){
        repo.deleteById(id);
    }

    private  ConsultantDTO toDTO(Consultant c) {
        return new ConsultantDTO(c.getId(), c.getNome(), c.getEmail(), c.getEspecialidade());
    }
}
