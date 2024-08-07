package com.profitsoft.restapi.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AppConfig implements WebMvcConfigurer {

    @Value("${cloudinary.cloud_name}")
    String cloudName;

    @Value("${cloudinary.api_key}")
    String apiKey;

    @Value("${cloudinary.api_secret}")
    String apiSecret;


    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(
                ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
                )
        );
    }
}
