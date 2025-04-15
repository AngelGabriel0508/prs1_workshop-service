package pe.edu.vallegrande.workshop_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.workshop_service.dto.WorkshopRequestDto;
import pe.edu.vallegrande.workshop_service.dto.WorkshopResponseDto;
import pe.edu.vallegrande.workshop_service.dto.WorkshopKafkaEventDto;
import pe.edu.vallegrande.workshop_service.model.Workshop;
import pe.edu.vallegrande.workshop_service.repository.WorkshopRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class WorkshopService {

    private final WorkshopRepository repository;
    private final KafkaProducerService kafkaProducer;

    /**
     * 🔹 Lista todos los talleres, con filtros opcionales por:
     * - estado (status)
     * - fecha de inicio (dateStart)
     * - fecha de fin (dateEnd)
     */
    public Flux<WorkshopResponseDto> findAll(String status, LocalDate dateStart, LocalDate dateEnd) {
        if (status != null) {
            if (dateStart != null && dateEnd != null) {
                return repository.findByStatusAndDateStartGreaterThanEqualAndDateEndLessThanEqual(status, dateStart, dateEnd)
                        .map(this::toResponseDto);
            } else if (dateStart != null) {
                return repository.findByStatusAndDateStartGreaterThanEqual(status, dateStart)
                        .map(this::toResponseDto);
            } else if (dateEnd != null) {
                return repository.findByStatusAndDateEndLessThanEqual(status, dateEnd)
                        .map(this::toResponseDto);
            } else {
                return repository.findByStatus(status).map(this::toResponseDto);
            }
        } else {
            return repository.findAll().map(this::toResponseDto);
        }
    }

    /**
     * 🔹 Busca un taller por su ID.
     */
    public Mono<WorkshopResponseDto> findById(Integer id) {
        return repository.findById(id)
                .map(this::toResponseDto);
    }

    /**
     * 🔹 Crea un nuevo taller (por defecto se guarda como activo).
     * También envía el evento por Kafka.
     */
    public Mono<WorkshopResponseDto> create(WorkshopRequestDto dto) {
        Workshop workshop = Workshop.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .dateStart(dto.getDateStart())
                .dateEnd(dto.getDateEnd())
                .status("A") // Activo por defecto
                .build();

        return repository.save(workshop)
                .doOnNext(saved -> kafkaProducer.sendWorkshopEvent(toKafkaDto(saved)))
                .map(this::toResponseDto);
    }

    /**
     * 🔹 Actualiza un taller por ID y envía el cambio por Kafka.
     */
    public Mono<WorkshopResponseDto> update(Integer id, WorkshopRequestDto dto) {
        return repository.findById(id)
                .flatMap(existing -> {
                    existing.setName(dto.getName());
                    existing.setDescription(dto.getDescription());
                    existing.setDateStart(dto.getDateStart());
                    existing.setDateEnd(dto.getDateEnd());
                    return repository.save(existing)
                            .doOnNext(updated -> kafkaProducer.sendWorkshopEvent(toKafkaDto(updated)))
                            .map(this::toResponseDto);
                });
    }

    /**
     * 🔹 Realiza eliminación lógica (status = 'I') y envía el cambio por Kafka.
     */
    public Mono<Void> deleteLogic(Integer id) {
        return repository.findById(id)
                .flatMap(workshop -> {
                    workshop.setStatus("I");
                    return repository.save(workshop)
                            .doOnNext(updated -> kafkaProducer.sendWorkshopEvent(toKafkaDto(updated)))
                            .then();
                });
    }

    /**
     * 🔹 Restaura un taller (status = 'A') y envía el evento por Kafka.
     */
    public Mono<Void> restore(Integer id) {
        return repository.findById(id)
                .flatMap(workshop -> {
                    workshop.setStatus("A");
                    return repository.save(workshop)
                            .doOnNext(updated -> kafkaProducer.sendWorkshopEvent(toKafkaDto(updated)))
                            .then();
                });
    }

    /**
     * 🔹 Elimina permanentemente un taller por ID (eliminación física).
     */
    public Mono<Void> deletePermanent(Integer id) {
        return repository.deleteById(id);
    }

    // ========== MAPPERS ==========

    /**
     * 🔄 Convierte entidad Workshop a DTO de respuesta.
     */
    private WorkshopResponseDto toResponseDto(Workshop workshop) {
        WorkshopResponseDto dto = new WorkshopResponseDto();
        dto.setId(workshop.getId());
        dto.setName(workshop.getName());
        dto.setDescription(workshop.getDescription());
        dto.setDateStart(workshop.getDateStart());
        dto.setDateEnd(workshop.getDateEnd());
        dto.setStatus(workshop.getStatus());
        return dto;
    }

    /**
     * 🔄 Convierte entidad Workshop a DTO para evento Kafka.
     */
    private WorkshopKafkaEventDto toKafkaDto(Workshop workshop) {
        WorkshopKafkaEventDto dto = new WorkshopKafkaEventDto();
        dto.setId(workshop.getId());
        dto.setName(workshop.getName());
        dto.setDateStart(workshop.getDateStart());
        dto.setDateEnd(workshop.getDateEnd());
        dto.setStatus(workshop.getStatus());
        return dto;
    }
}
