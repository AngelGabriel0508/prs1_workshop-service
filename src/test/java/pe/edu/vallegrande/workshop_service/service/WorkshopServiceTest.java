package pe.edu.vallegrande.workshop_service.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pe.edu.vallegrande.workshop_service.dto.WorkshopRequestDto;
import pe.edu.vallegrande.workshop_service.dto.WorkshopResponseDto;
import pe.edu.vallegrande.workshop_service.model.Workshop;
import pe.edu.vallegrande.workshop_service.repository.WorkshopRepository;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class WorkshopServiceTest {

    @Mock
    private WorkshopRepository repository;

    @Mock
    private KafkaProducerService kafkaProducer;

    @InjectMocks
    private WorkshopService service;

    public WorkshopServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateWorkshop() {
        WorkshopRequestDto dto = new WorkshopRequestDto();
        dto.setName("Taller 1");
        dto.setDescription("Descripción");
        dto.setDateStart(LocalDate.now());
        dto.setDateEnd(LocalDate.now().plusDays(1));

        Workshop saved = Workshop.builder()
                .id(1)
                .name(dto.getName())
                .description(dto.getDescription())
                .dateStart(dto.getDateStart())
                .dateEnd(dto.getDateEnd())
                .status("A")
                .build();

        when(repository.save(any(Workshop.class))).thenReturn(Mono.just(saved));
        doNothing().when(kafkaProducer).sendWorkshopEvent(any());

        Mono<WorkshopResponseDto> result = service.create(dto);

        StepVerifier.create(result)
                .expectNextMatches(r -> r.getName().equals("Taller 1"))
                .verifyComplete();
    }

}
