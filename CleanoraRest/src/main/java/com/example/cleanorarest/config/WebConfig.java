package com.example.cleanorarest.config;

import com.sendgrid.SendGrid;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.net.http.HttpClient;
import java.nio.file.Paths;

@Configuration
@EnableWebMvc
@EnableCaching
public class WebConfig implements WebMvcConfigurer {
    @Value("${secret-key}")
    private String SECRET_KEY;

    @Bean
    public HttpClient httpClient() {
        return HttpClient.newHttpClient();
    }

    @Bean
    public Faker faker() {
        return new Faker();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public SendGrid sendGrid() {
        return new SendGrid(SECRET_KEY);
    }

    @Value("${upload.folder.path}")
    private String projectPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:static/");





        registry.addResourceHandler("/" + Paths.get(
                                projectPath)
                        .subpath(
                                Paths.get(projectPath).getNameCount()-1,
                                Paths.get(projectPath).getNameCount()) +
                        "/**")
                .addResourceLocations("file:" +  projectPath + "/");
    }


}
