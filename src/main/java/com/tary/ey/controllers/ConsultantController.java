package com.tary.ey.controllers;

import com.tary.ey.dtos.ConsultantDTO;
import com.tary.ey.services.ConsultantService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/consultants")
public class ConsultantController {

    private final ConsultantService service;

    public ConsultantController(ConsultantService service) {
        this.service = service;
    }

    @GetMapping
    public List<ConsultantDTO> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public ConsultantDTO get (@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ConsultantDTO create(@RequestBody @Valid ConsultantDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public ConsultantDTO update(@PathVariable Long id, @Valid ConsultantDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
