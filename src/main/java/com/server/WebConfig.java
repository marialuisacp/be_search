package com.server;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
//@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		//registry.addMapping("/**");
		//registry.addMapping("/**").allowCredentials(false).allowedOrigins("*").allowedMethods("PUT", "POST", "GET", "OPTIONS", "DELETE").exposedHeaders("Authorization", "Content-Type");
		registry.addMapping("/search")
        .allowedOrigins("*")
        .allowCredentials(false) 
        .allowedMethods("GET", "POST","PUT", "DELETE");
        //.exposedHeaders("Content-Type: application/json;charset=UTF-8"); 
	}
}

//Content-Type: application/json;charset=UTF-8
//Transfer-Encoding: chunked