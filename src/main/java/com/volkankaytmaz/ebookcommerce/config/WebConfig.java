package com.volkankaytmaz.ebookcommerce.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map root level resources
        registry.addResourceHandler("/*.html")
                .addResourceLocations("classpath:/static/");

        // Map CSS resources
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/");

        // Map JavaScript resources
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");

        // Map all other static resources
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:63342", "http://localhost:8082")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("Authorization", "Content-Type", "Accept")
                .allowCredentials(true);
    }
}

