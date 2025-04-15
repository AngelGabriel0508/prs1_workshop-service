package pe.edu.vallegrande.workshop_service.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import pe.edu.vallegrande.workshop_service.dto.WorkshopRequestDto;
import pe.edu.vallegrande.workshop_service.dto.WorkshopResponseDto;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureWebTestClient(timeout = "10000") // ⏱ Aumentamos timeout para evitar TimeoutException
@TestPropertySource(properties = {
        "spring.r2dbc.url=r2dbc:postgresql://ep-broad-king-a5u5dwsk-pooler.us-east-2.aws.neon.tech:5432/users?sslmode=require",
        "spring.r2dbc.username=users_owner",
        "spring.r2dbc.password=npg_NFSkIrt3qA5z"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // 💡 Cierra contexto correctamente tras cada test
public class WorkshopIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    private static final String JWT = "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6Ijg1NzA4MWNhOWNiYjM3YzIzNDk4ZGQzOTQzYmYzNzFhMDU4ODNkMjgiLCJ0eXAiOiJKV1QifQ.eyJyb2xlIjoiQURNSU4iLCJpc3MiOiJodHRwczovL3NlY3VyZXRva2VuLmdvb2dsZS5jb20vc2VjdXJpdHktcHJzMSIsImF1ZCI6InNlY3VyaXR5LXByczEiLCJhdXRoX3RpbWUiOjE3NDQ3MzkwMDgsInVzZXJfaWQiOiJQSmZaN1ZiSlhpY3RqUExKNHFha3h1UWNpcjgyIiwic3ViIjoiUEpmWjdWYkpYaWN0alBMSjRxYWt4dVFjaXI4MiIsImlhdCI6MTc0NDczOTAwOCwiZXhwIjoxNzQ0NzQyNjA4LCJlbWFpbCI6ImFuZ2VsLmNhc3RpbGxhQHZhbGxlZ3JhbmRlLmVkdS5wZSIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwiZmlyZWJhc2UiOnsiaWRlbnRpdGllcyI6eyJlbWFpbCI6WyJhbmdlbC5jYXN0aWxsYUB2YWxsZWdyYW5kZS5lZHUucGUiXX0sInNpZ25faW5fcHJvdmlkZXIiOiJwYXNzd29yZCJ9fQ.S3BoYH4TJRTGuEgi0ChDvr5keYBjshGgq1tIBdc3UZiEA_xS54YXt4JI3TTtKam8R43apihOG2XAuFFQ8YjV3N5VrXbX0dSwIImrJlFMtJ6oJ3ZCMWF3XM7jWpWpps7H_QZSBc4I4yWpAZ2676o8Cv3IM9GnNzQYwcgaDWm_VzQCm3kvDP0cGsE_poMr2jaiq-bSOX2iUw6G0e1W5ra8Ika0h6qNu_ibhHYvVjNS5EfOgq7v5Xs4uxgy7xOFbdRtGqGl-_3PJHMHibVEz-cBXmCLAd1vFQxnmE6jxJFN7FmKFHzHqUDuaQkl4bU1VyqsuEmin-Piu8jQBpUbsoVQ9Q"; // Reemplaza por uno válido

    @Test
    void testCreateAndFetchWorkshop() {
        // DTO para crear taller
        WorkshopRequestDto dto = new WorkshopRequestDto();
        dto.setName("Taller integración");
        dto.setDescription("Prueba de integración");
        dto.setDateStart(LocalDate.now());
        dto.setDateEnd(LocalDate.now().plusDays(5));

        // Crear taller y verificar
        webTestClient.post()
                .uri("/api/workshops")
                .header("Authorization", JWT)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(WorkshopResponseDto.class)
                .value(responseDto -> {
                    assertThat(responseDto).isNotNull();
                    assertThat(responseDto.getId()).isNotNull();
                    assertThat(responseDto.getName()).isEqualTo("Taller integración");

                    // Obtener por ID y verificar
                    webTestClient.get()
                            .uri("/api/workshops/" + responseDto.getId())
                            .header("Authorization", JWT)
                            .exchange()
                            .expectStatus().isOk()
                            .expectBody(WorkshopResponseDto.class)
                            .value(fetched -> {
                                assertThat(fetched).isNotNull();
                                assertThat(fetched.getName()).isEqualTo("Taller integración");
                                assertThat(fetched.getStatus()).isEqualTo("A");
                            });
                });
    }
}
