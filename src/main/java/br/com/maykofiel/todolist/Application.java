package br.com.maykofiel.todolist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Configuration
	public static class SecurityConfig {

		@Bean
		public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
			http
					.csrf(AbstractHttpConfigurer::disable) // Desabilita CSRF para testar o POST
					.authorizeHttpRequests(auth -> auth
							.requestMatchers("/h2-console/**").permitAll() // Libera o H2
							.anyRequest().permitAll()
					)
					.headers(headers -> headers
							.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable) // Libera os Frames do H2
					);
			return http.build();
		}
	}
}
