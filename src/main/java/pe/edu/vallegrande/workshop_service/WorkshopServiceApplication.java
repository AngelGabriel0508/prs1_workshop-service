package pe.edu.vallegrande.workshop_service;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WorkshopServiceApplication {

	public static void main(String[] args) {
		// Cargar variables del .env ubicado en la raíz del proyecto
		Dotenv dotenv = Dotenv.configure()
				.directory(".") // asumiendo que estás en: nph-prs/workshop-service
				.ignoreIfMalformed()
				.ignoreIfMissing()
				.load();

		dotenv.entries().forEach(entry ->
				System.setProperty(entry.getKey(), entry.getValue())
		);

		SpringApplication.run(WorkshopServiceApplication.class, args);
	}
}
