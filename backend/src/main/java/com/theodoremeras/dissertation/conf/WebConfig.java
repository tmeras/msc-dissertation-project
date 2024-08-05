package com.theodoremeras.dissertation.conf;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    // Enable CORS for all HTTP methods
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        CorsRegistration corsRegistration = registry.addMapping("/**");
        corsRegistration.allowedMethods("*");

        // This should be enabled typically, limiting the allowed origins
        //corsRegistration.allowedOrigins("http://localhost:5173/");

        // This is enabled in order to avoid any issues
        // in case the default client port (5173) isn't available at the examiner's machine
        corsRegistration.allowedOrigins("*");
    }

}
