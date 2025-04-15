package pe.edu.vallegrande.workshop_service.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.workshop_service.model.Workshop;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Repository
public interface WorkshopRepository extends ReactiveCrudRepository<Workshop, Integer> {
    Mono<Workshop> findById(Long id);
    Flux<Workshop> findByStatus(String status);
    Flux<Workshop> findByStatusAndDateStartGreaterThanEqualAndDateEndLessThanEqual(
            String status, LocalDate dateStart, LocalDate dateEnd);
    Flux<Workshop> findByStatusAndDateStartGreaterThanEqual(String status, LocalDate dateStart);
    Flux<Workshop> findByStatusAndDateEndLessThanEqual(String status, LocalDate dateEnd);
}

