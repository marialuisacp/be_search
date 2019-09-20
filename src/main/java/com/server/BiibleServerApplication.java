package com.server;

import java.io.File;
import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
public class BiibleServerApplication extends SpringBootServletInitializer {
	
	@Override protected SpringApplicationBuilder configure(SpringApplicationBuilder application) { 
		return application.sources(BiibleServerApplication.class); 
	} 
	
	@Bean
	public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
            	registry.addMapping("/search")
                .allowedOrigins("*")  
                .allowedMethods("GET", "POST","PUT", "DELETE") 
                .exposedHeaders("Content-Type: application/hal+json;charset=ISO-8859-1"); 
            	registry.addMapping("/search/en")
                .allowedOrigins("*")  
                .allowedMethods("GET", "POST","PUT", "DELETE") 
                .exposedHeaders("Content-Type: application/hal+json;charset=ISO-8859-1"); 
            }
            
            @Override  
            public void addViewControllers(ViewControllerRegistry registry) {  
                registry.addViewController("/").setViewName("index.html");  
                registry.setOrder(Ordered.HIGHEST_PRECEDENCE);  
            }  
          
            @Override  
            public void configurePathMatch(PathMatchConfigurer configurer) {  
                super.configurePathMatch(configurer);  
                configurer.setUseSuffixPatternMatch(false);  
            }
        };
    }
	
	public static void main(String[] args) {
		SpringApplication.run(BiibleServerApplication.class, args);
	}
}
