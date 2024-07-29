package com.theodoremeras.dissertation;

import com.theodoremeras.dissertation.conf.StorageProperties;
import com.theodoremeras.dissertation.evidence.EvidenceService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class DissertationApplication {

	public static void main(String[] args) {
		SpringApplication.run(DissertationApplication.class, args);
	}

	@Bean
	CommandLineRunner init(EvidenceService evidenceService) {
		return (args) -> {
			evidenceService.deleteAll();
			evidenceService.init();
		};
	}

}
