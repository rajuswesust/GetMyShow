package com.raju.getmyshow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing  // ✅ Add this annotation
@SpringBootApplication
public class GetmyshowApplication {

	public static void main(String[] args) {
		SpringApplication.run(GetmyshowApplication.class, args);
	}

}
