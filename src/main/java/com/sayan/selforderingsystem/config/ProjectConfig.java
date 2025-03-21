package com.sayan.selforderingsystem.config;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ProjectConfig {

    @Bean
    public Cloudinary getCloudinary() {

        Map<String, Object> config = new HashMap();
        config.put("cloud_name", "dnonotrgl" );
        config.put("api_key", "223946125738722");
        config.put("api_secret","nsN2f_KBwFHEQGrVkJfn324ZA_o");
        config.put("secure", true);

        return new Cloudinary(config);
    }
}
