package pe.edu.vallegrande.workshop_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.reactive.server.WebTestClient;
import pe.edu.vallegrande.workshop_service.dto.WorkshopResponseDto;
import pe.edu.vallegrande.workshop_service.service.KafkaProducerService;
import pe.edu.vallegrande.workshop_service.service.WorkshopService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.LocalDate;
import static org.mockito.Mockito.when;

public class WorkshopControllerTest {

    private WorkshopService service;
    private WebTestClient webTestClient;

    @BeforeEach
    void setup() {
        service = Mockito.mock(WorkshopService.class);
        KafkaProducerService kafkaProducerService = Mockito.mock(KafkaProducerService.class);
        WorkshopController controller = new WorkshopController(service, kafkaProducerService);
        webTestClient = WebTestClient.bindToController(controller).build();
    }


    @Test
    void testGetAllWorkshops() {
        WorkshopResponseDto dto = new WorkshopResponseDto();
        dto.setId(1);
        dto.setName("Taller Test");
        dto.setDescription("Descripción");
        dto.setDateStart(LocalDate.of(2025, 4, 1));
        dto.setDateEnd(LocalDate.of(2025, 4, 10));
        dto.setStatus("A");

        when(service.findAll(null, null, null)).thenReturn(Flux.just(dto));

        webTestClient.get()
                .uri("/api/workshops")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(WorkshopResponseDto.class)
                .hasSize(1)
                .contains(dto);
    }

    @Test
    void testGetWorkshopById() {
        WorkshopResponseDto dto = new WorkshopResponseDto();
        dto.setId(1);
        dto.setName("Taller 1");

        when(service.findById(1)).thenReturn(Mono.just(dto));

        webTestClient.get()
                .uri("/api/workshops/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(WorkshopResponseDto.class)
                .isEqualTo(dto);
    }

    @Test
    void testDeleteWorkshop() {
        when(service.deletePermanent(1)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/workshops/1")
                .exchange()
                .expectStatus().isNoContent();
    }
}
