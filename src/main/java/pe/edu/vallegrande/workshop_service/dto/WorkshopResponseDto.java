package pe.edu.vallegrande.workshop_service.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class WorkshopResponseDto {
    private Integer id;
    private String name;
    private String description;
    private LocalDate dateStart;
    private LocalDate dateEnd;
    private String status;
}
