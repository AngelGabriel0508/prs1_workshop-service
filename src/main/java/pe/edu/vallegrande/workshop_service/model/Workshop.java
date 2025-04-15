package pe.edu.vallegrande.workshop_service.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("workshop")
public class Workshop {

    @Id
    private Integer id;
    private String name;
    private String description;
    @Column("date_start")
    private LocalDate dateStart;
    @Column("date_end")
    private LocalDate dateEnd;
    private String status;
}
