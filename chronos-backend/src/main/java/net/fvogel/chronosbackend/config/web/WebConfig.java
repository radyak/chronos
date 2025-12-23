package net.fvogel.chronosbackend.config.web;


import net.fvogel.chronosbackend.config.web.converter.StringToSupportedLanguagConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToSupportedLanguagConverter());
    }
}