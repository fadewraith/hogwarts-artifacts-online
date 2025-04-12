package edu.cs.hogwartsartifactsonline;

import edu.cs.hogwartsartifactsonline.artifact.utils.IdWorker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class HogwartsArtifactsOnlineApplication {

	public static void main(String[] args) {
		SpringApplication.run(HogwartsArtifactsOnlineApplication.class, args);
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
		String encoded = encoder.encode("Test@123");

	}

	@Bean
	public IdWorker idWorker() {
		return new IdWorker(1, 1);
	}

}
