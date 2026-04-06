package gov.cms.madie.notification_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class NotificationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificationServiceApplication.class, args);
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry
						.addMapping("/**")
						.allowedOrigins(
								"http://localhost:9000",
								"https://dev-madie.hcqis.org",
								"https://test-madie.hcqis.org",
								"https://impl-madie.hcqis.org",
								"https://dev.madie.internal.cms.gov",
								"https://test.madie.internal.cms.gov",
								"https://impl.madie.internal.cms.gov",
								"https://madie.cms.gov")
						.allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE");
			}
		};
	}

}
