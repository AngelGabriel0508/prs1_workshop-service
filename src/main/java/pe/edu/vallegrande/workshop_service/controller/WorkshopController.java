package pe.edu.vallegrande.workshop_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.workshop_service.dto.WorkshopRequestDto;
import pe.edu.vallegrande.workshop_service.dto.WorkshopResponseDto;
import pe.edu.vallegrande.workshop_service.service.WorkshopService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/workshops")
@RequiredArgsConstructor
public class WorkshopController {

    private final WorkshopService service;

    /**
     * 🔹 Listado de talleres con filtros opcionales:
     * status (A/I), fecha de inicio (dateStart), fecha de fin (dateEnd).
     */
    @GetMapping
    public Flux<WorkshopResponseDto> findAll(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateStart,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateEnd) {
        return service.findAll(status, dateStart, dateEnd);
    }

    /**
     * 🔹 Obtener un taller por su ID.
     */
    @GetMapping("/{id}")
    public Mono<WorkshopResponseDto> findById(@PathVariable Integer id) {
        return service.findById(id);
    }

    /**
     * 🔹 Crear un nuevo taller.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<WorkshopResponseDto> create(@RequestBody WorkshopRequestDto dto) {
        return service.create(dto);
    }

    /**
     * 🔹 Actualizar los datos de un taller existente por ID.
     */
    @PutMapping("/{id}")
    public Mono<WorkshopResponseDto> update(@PathVariable Integer id, @RequestBody WorkshopRequestDto dto) {
        return service.update(id, dto);
    }

    /**
     * 🔹 Eliminación lógica de un taller (status = 'I').
     */
    @PutMapping("/{id}/disable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> disable(@PathVariable Integer id) {
        return service.deleteLogic(id);
    }

    /**
     * 🔹 Restaurar un taller deshabilitado (status = 'A').
     */
    @PutMapping("/{id}/restore")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> restore(@PathVariable Integer id) {
        return service.restore(id);
    }

    /**
     * 🔹 Eliminación física de un taller (se borra de la base de datos).
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable Integer id) {
        return service.deletePermanent(id);
    }
}
