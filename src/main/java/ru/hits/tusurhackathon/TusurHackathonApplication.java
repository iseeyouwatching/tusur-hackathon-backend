package ru.hits.tusurhackathon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan("ru.hits.tusurhackathon")
@SpringBootApplication
public class TusurHackathonApplication {

	public static void main(String[] args) {
		SpringApplication.run(TusurHackathonApplication.class, args);
	}

}
