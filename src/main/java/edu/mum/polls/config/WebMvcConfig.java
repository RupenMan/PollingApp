package edu.mum.polls.config;

import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class WebMvcConfig implements WebMvcConfigurer{
	private final long MAX_AGE_SECS = 3600;

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
			.allowedOrigins("*")
			.allowedMethods("HEAD","OPTION", "GET","POST", "PUT","DELETE","PATCH")
			.maxAge(MAX_AGE_SECS);
	}
	
	
	
}
